package com.thefang.yunpicture.interfaces.controller;

import cn.hutool.core.util.ObjectUtil;
import com.thefang.yunpicture.infrastructure.common.BaseResponse;
import com.thefang.yunpicture.infrastructure.common.DeleteRequest;
import com.thefang.yunpicture.infrastructure.common.ResultUtils;
import com.thefang.yunpicture.infrastructure.exception.BusinessException;
import com.thefang.yunpicture.infrastructure.exception.ErrorCode;
import com.thefang.yunpicture.infrastructure.exception.ThrowUtils;
import com.thefang.yunpicture.interfaces.assembler.SpaceAssembler;
import com.thefang.yunpicture.interfaces.assembler.SpaceUserAssembler;
import com.thefang.yunpicturebackend.manager.auth.annotation.SaSpaceCheckPermission;
import com.thefang.yunpicturebackend.manager.auth.model.SpaceUserPermissionConstant;
import com.thefang.yunpicture.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.thefang.yunpicture.interfaces.dto.spaceuser.SpaceUserEditRequest;
import com.thefang.yunpicture.interfaces.dto.spaceuser.SpaceUserQueryRequest;
import com.thefang.yunpicture.domain.space.entity.SpaceUser;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.interfaces.vo.space.SpaceUserVO;
import com.thefang.yunpicture.application.service.SpaceUserApplicationService;
import com.thefang.yunpicture.application.service.UserApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description 空间用户相关的接口
 * @Author Thefang
 * @Create 2025/1/20
 */
@RestController
@RequestMapping("/spaceUser")
@Slf4j
public class SpaceUserController {

    @Resource
    private SpaceUserApplicationService spaceUserApplicationService;

    @Resource
    private UserApplicationService userApplicationService;

    /**
     * 创建空间成员
     *
     * @param spaceUserAddRequest 添加空间成员请求对象
     * @param request             请求对象
     * @return 添加的空间成员 ID
     */
    @PostMapping("/add")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<Long> addSpaceUser(@RequestBody SpaceUserAddRequest spaceUserAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
        long id = spaceUserApplicationService.addSpaceUser(spaceUserAddRequest);
        return ResultUtils.success(id);
    }

    /**
     * 从空间移除成员
     *
     * @param deleteRequest 删除请求对象
     * @param request       请求对象
     * @return 是否成功
     */
    @PostMapping("/delete")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest,
                                                 HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        SpaceUser oldSpaceUser = spaceUserApplicationService.getById(id);
        ThrowUtils.throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = spaceUserApplicationService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 查询某个成员在某个空间的信息
     *
     * @param spaceUserQueryRequest 查询空间成员请求对象
     * @return 空间用户关联对象
     */
    @PostMapping("/get")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<SpaceUser> getSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest) {
        // 参数校验
        ThrowUtils.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAMS_ERROR);
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        ThrowUtils.throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCode.PARAMS_ERROR);
        // 查询数据库
        SpaceUser spaceUser = spaceUserApplicationService.getOne(spaceUserApplicationService.getQueryWrapper(spaceUserQueryRequest));
        ThrowUtils.throwIf(spaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(spaceUser);
    }

    /**
     * 查询成员信息列表
     *
     * @param spaceUserQueryRequest 查询空间成员请求对象
     * @param request               请求对象
     * @return 空间用户关联对象列表
     */
    @PostMapping("/list")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<List<SpaceUserVO>> listSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest,
                                                         HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAMS_ERROR);
        List<SpaceUser> spaceUserList = spaceUserApplicationService.list(
                spaceUserApplicationService.getQueryWrapper(spaceUserQueryRequest)
        );
        return ResultUtils.success(spaceUserApplicationService.getSpaceUserVOList(spaceUserList));
    }

    /**
     * 编辑成员信息（设置权限）
     *
     * @param spaceUserEditRequest 编辑空间成员请求对象
     * @param request              请求对象
     * @return 是否成功
     */
    @PostMapping("/edit")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<Boolean> editSpaceUser(@RequestBody SpaceUserEditRequest spaceUserEditRequest,
                                               HttpServletRequest request) {
        if (spaceUserEditRequest == null || spaceUserEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        SpaceUser spaceUserEntity = SpaceUserAssembler.toSpaceUserEntity(spaceUserEditRequest);
        // 数据校验
        spaceUserApplicationService.validSpaceUser(spaceUserEntity, false);
        // 判断是否存在
        long id = spaceUserEditRequest.getId();
        SpaceUser oldSpaceUser = spaceUserApplicationService.getById(id);
        ThrowUtils.throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        // 判断是否与之前角色相同
        ThrowUtils.throwIf(oldSpaceUser.getSpaceRole().equals(spaceUserEntity.getSpaceRole()), ErrorCode.PARAMS_ERROR, "该成员的角色与之前相同，无需修改");
        // 操作数据库
        boolean result = spaceUserApplicationService.updateById(spaceUserEntity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 查询我加入的团队空间列表
     *
     * @param request 请求对象
     * @return 空间用户关联对象列表
     */
    @PostMapping("/list/my")
    public BaseResponse<List<SpaceUserVO>> listMyTeamSpace(HttpServletRequest request) {
        User loginUser = userApplicationService.getLoginUser(request);
        SpaceUserQueryRequest spaceUserQueryRequest = new SpaceUserQueryRequest();
        spaceUserQueryRequest.setUserId(loginUser.getId());
        List<SpaceUser> spaceUserList = spaceUserApplicationService.list(
                spaceUserApplicationService.getQueryWrapper(spaceUserQueryRequest)
        );
        return ResultUtils.success(spaceUserApplicationService.getSpaceUserVOList(spaceUserList));
    }
}

