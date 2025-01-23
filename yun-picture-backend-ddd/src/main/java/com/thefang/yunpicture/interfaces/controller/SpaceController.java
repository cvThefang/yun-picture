package com.thefang.yunpicture.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thefang.yunpicture.infrastructure.annotation.AuthCheck;
import com.thefang.yunpicture.infrastructure.common.BaseResponse;
import com.thefang.yunpicture.infrastructure.common.DeleteRequest;
import com.thefang.yunpicture.infrastructure.common.ResultUtils;
import com.thefang.yunpicture.domain.user.constant.UserConstant;
import com.thefang.yunpicture.infrastructure.exception.BusinessException;
import com.thefang.yunpicture.infrastructure.exception.ErrorCode;
import com.thefang.yunpicture.infrastructure.exception.ThrowUtils;
import com.thefang.yunpicture.interfaces.assembler.SpaceAssembler;
import com.thefang.yunpicture.interfaces.dto.space.*;
import com.thefang.yunpicturebackend.manager.auth.SpaceUserAuthManager;
import com.thefang.yunpicture.domain.space.entity.Space;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.domain.space.valueobject.SpaceLevelEnum;
import com.thefang.yunpicture.interfaces.vo.space.SpaceVO;
import com.thefang.yunpicture.application.service.SpaceApplicationService;
import com.thefang.yunpicture.application.service.UserApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 空间相关的接口
 * @Author Thefang
 * @Create 2025/1/5
 */
@Slf4j
@RestController
@RequestMapping("/space")
public class SpaceController {

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private SpaceApplicationService spaceApplicationService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    /**
     * 添加空间
     *
     * @param spaceAddRequest 创建空间请求对象
     * @param request         请求
     * @return 空间 id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取登录用户
        User loginUser = userApplicationService.getLoginUser(request);
        long newId = spaceApplicationService.addSpace(spaceAddRequest, loginUser);
        return ResultUtils.success(newId);
    }

    /**
     * 删除空间
     *
     * @param deleteRequest 空间id
     * @param request       请求
     * @return 成功或失败
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 删除空间不用权限校验注解@AuthCheck，是因为管理员和自己都能删除空间（自己仅能删除本人的空间）
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userApplicationService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断空间是否存在
        Space oldSpace = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除 权限校验
        spaceApplicationService.checkSpaceAuth(loginUser, oldSpace);
        // 操作数据库
        boolean result = spaceApplicationService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新空间信息（仅管理员可用）
     *
     * @param spaceUpdateRequest 空间更新信息
     * @param request            请求
     * @return 成功或失败
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest,
                                             HttpServletRequest request) {
        if (spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Space spaceEntity = SpaceAssembler.toSpaceEntity(spaceUpdateRequest);
        // 自动填充数据
        spaceApplicationService.fillSpaceBySpaceLevel(spaceEntity);
        // 数据校验 并且不是创建操作
        spaceEntity.validSpace(false);
        // 判断是否存在
        long id = spaceUpdateRequest.getId();
        Space oldSpace = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = spaceApplicationService.updateById(spaceEntity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取空间信息（仅管理员可用）
     *
     * @param id      空间id
     * @param request 请求
     * @return 空间信息
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Space> getSpaceById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(space);
    }

    /**
     * 根据 id 获取空间包装类
     *
     * @param id      空间id
     * @param request 请求
     * @return 空间包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<SpaceVO> getSpaceVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        SpaceVO spaceVO = spaceApplicationService.getSpaceVO(space, request);
        User loginUser = userApplicationService.getLoginUser(request);
        List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
        spaceVO.setPermissionList(permissionList);
        // 返回封装类结果对象
        return ResultUtils.success(spaceVO);
    }

    /**
     * 分页获取空间列表（仅管理员可用）
     *
     * @param spaceQueryRequest 空间查询条件
     * @return 分页空间列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // 查询数据库
        Page<Space> spacePage = spaceApplicationService.page(new Page<>(current, size),
                spaceApplicationService.getQueryWrapper(spaceQueryRequest));
        return ResultUtils.success(spacePage);
    }

    /**
     * 分页获取空间列表（包装类）
     *
     * @param spaceQueryRequest 空间查询条件
     * @param request           请求
     * @return 分页空间包装类列表
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest,
                                                         HttpServletRequest request) {
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Space> spacePage = spaceApplicationService.page(new Page<>(current, size),
                spaceApplicationService.getQueryWrapper(spaceQueryRequest));
        // 获取封装类
        return ResultUtils.success(spaceApplicationService.getSpaceVOPage(spacePage, request));
    }

    /**
     * 编辑空间（给用户使用）
     *
     * @param spaceEditRequest 空间编辑信息
     * @param request          请求
     * @return 成功或失败
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        if (spaceEditRequest == null || spaceEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Space spaceEntity = SpaceAssembler.toSpaceEntity(spaceEditRequest);
        // 数据填充
        spaceApplicationService.fillSpaceBySpaceLevel(spaceEntity);
        // 设置编辑时间
        spaceEntity.setEditTime(new Date());
        // 数据校验
        spaceEntity.validSpace(false);
        User loginUser = userApplicationService.getLoginUser(request);
        // 判断是否存在
        long id = spaceEditRequest.getId();
        Space oldSpace = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        spaceApplicationService.checkSpaceAuth(loginUser, oldSpace);
        // 操作数据库
        boolean result = spaceApplicationService.updateById(spaceEntity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 获取所有的空间级别，便于前端展示
     *
     * @return 空间级别列表
     */
    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> listSpaceLevel() {
        return ResultUtils.success(spaceApplicationService.listSpaceLevel());
    }
}
