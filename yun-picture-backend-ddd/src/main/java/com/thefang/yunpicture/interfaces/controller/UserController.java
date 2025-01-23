package com.thefang.yunpicture.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thefang.yunpicture.infrastructure.annotation.AuthCheck;
import com.thefang.yunpicture.infrastructure.common.BaseResponse;
import com.thefang.yunpicture.infrastructure.common.DeleteRequest;
import com.thefang.yunpicture.infrastructure.common.ResultUtils;
import com.thefang.yunpicture.interfaces.assembler.UserAssembler;
import com.thefang.yunpicture.interfaces.dto.user.*;
import com.thefang.yunpicture.domain.user.constant.UserConstant;
import com.thefang.yunpicture.infrastructure.exception.BusinessException;
import com.thefang.yunpicture.infrastructure.exception.ErrorCode;
import com.thefang.yunpicture.infrastructure.exception.ThrowUtils;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.interfaces.vo.user.LoginUserVO;
import com.thefang.yunpicture.interfaces.vo.user.UserVO;
import com.thefang.yunpicture.application.service.UserApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description 用户相关接口
 * @Author Thefang
 * @Create 2024/12/11
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserApplicationService userApplicationService;

    /**
     * 用户注册接口
     *
     * @param userRegisterRequest 用户注册请求
     * @return 注册成功的用户id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        long result = userApplicationService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录接口
     *
     * @param userLoginRequest 用户登录请求
     * @param request          http请求
     * @return 登录成功的用户信息
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        LoginUserVO loginUserVO = userApplicationService.userLogin(userLoginRequest, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request http请求
     * @return 当前登录用户信息（脱敏）
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userApplicationService.getLoginUser(request);
        return ResultUtils.success(userApplicationService.getLoginUserVO(loginUser));
    }

    /**
     * 用户注销接口
     *
     * @param request http请求
     * @return 退出成功的标志
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userApplicationService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 创建用户接口
     *
     * @param userAddRequest 用户创建请求
     * @return 新用户的id
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User userEntity = UserAssembler.toUserEntity(userAddRequest);
        return ResultUtils.success(userApplicationService.addUser(userEntity));
    }


    /**
     * 获取用户信息接口
     *
     * @param id 用户id
     * @return 用户信息
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userApplicationService.getUserById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id 用户id
     * @return 用户包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userApplicationService.getUserVO(user));
    }

    /**
     * 删除用户接口
     *
     * @param deleteRequest 删除请求
     * @return 删除成功的标志
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userApplicationService.deleteUser(deleteRequest);
        return ResultUtils.success(result);
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateRequest 用户更新请求
     * @return 更新成功的标志
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User userEntity = UserAssembler.toUserEntity(userUpdateRequest);
        userApplicationService.updateUser(userEntity);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 用户查询请求
     * @return 分页用户封装列表
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userApplicationService.listUserVOByPage(userQueryRequest));
    }

}
