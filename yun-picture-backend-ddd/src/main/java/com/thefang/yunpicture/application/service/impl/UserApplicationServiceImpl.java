package com.thefang.yunpicture.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thefang.yunpicture.domain.user.service.UserDomainService;
import com.thefang.yunpicture.infrastructure.common.DeleteRequest;
import com.thefang.yunpicture.infrastructure.exception.BusinessException;
import com.thefang.yunpicture.infrastructure.exception.ErrorCode;
import com.thefang.yunpicture.infrastructure.exception.ThrowUtils;
import com.thefang.yunpicture.interfaces.dto.user.UserLoginRequest;
import com.thefang.yunpicture.interfaces.dto.user.UserQueryRequest;
import com.thefang.yunpicture.interfaces.dto.user.UserRegisterRequest;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.domain.user.valueobject.UserRoleEnum;
import com.thefang.yunpicture.interfaces.vo.user.LoginUserVO;
import com.thefang.yunpicture.interfaces.vo.user.UserVO;
import com.thefang.yunpicture.application.service.UserApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * @author Thefang
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-12-10 23:38:02
 */
@Service
@Slf4j
public class UserApplicationServiceImpl implements UserApplicationService {

    @Resource
    private UserDomainService userDomainService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求对象
     * @return 用户id
     */
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 1. 校验
        User.validUserRegister(userRegisterRequest.getUserAccount(), userRegisterRequest.getUserPassword(), userRegisterRequest.getCheckPassword());
        // 2. 执行注册
        return userDomainService.userRegister(userRegisterRequest);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求对象
     * @param request          请求对象
     * @return 返回登录用户信息（脱敏）
     */
    @Override
    public LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 1. 校验
        User.validUserLogin(userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword());
        // 2. 执行登录
        return userDomainService.userLogin(userLoginRequest, request);
    }

    /**
     * 获取加密后的密码
     *
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        return userDomainService.getEncryptPassword(userPassword);
    }

    /**
     * 获取登录用户信息（脱敏）
     *
     * @param user 用户实体
     * @return 登录用户信息（脱敏）
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        return userDomainService.getLoginUserVO(user);
    }

    /**
     * 获取用户信息（脱敏）
     *
     * @param user 用户实体
     * @return 用户信息（脱敏）
     */
    @Override
    public UserVO getUserVO(User user) {
        return userDomainService.getUserVO(user);
    }

    /**
     * 获取用户信息（脱敏）列表
     *
     * @param userList 用户实体列表
     * @return 用户信息（脱敏）列表
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        return userDomainService.getUserVOList(userList);
    }

    /**
     * 获取登录用户
     *
     * @param request 请求对象
     * @return 登录用户实体
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        return userDomainService.getLoginUser(request);
    }

    /**
     * 用户登录态注销
     *
     * @param request 请求对象
     * @return 是否注销成功
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        return userDomainService.userLogout(request);
    }

    /**
     * 添加用户
     *
     * @param user 用户实体
     * @return 用户 id
     */
    @Override
    public long addUser(User user) {
        return userDomainService.addUser(user);
    }

    /**
     * 获取用户实体
     *
     * @param id 用户 id
     * @return 用户实体
     */
    @Override
    public User getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userDomainService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return user;
    }

    /**
     * 获取用户信息（脱敏）
     *
     * @param id 用户 id
     * @return 用户信息（脱敏）
     */
    @Override
    public UserVO getUserVOById(long id) {
        return userDomainService.getUserVO(getUserById(id));
    }

    /**
     * 删除用户
     *
     * @param deleteRequest 删除请求对象
     * @return 是否删除成功
     */
    @Override
    public boolean deleteUser(DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userDomainService.removeById(deleteRequest.getId());
    }

    /**
     * 更新用户
     *
     * @param user 用户实体
     */
    @Override
    public void updateUser(User user) {
        boolean result = userDomainService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    /**
     * 分页查询用户信息（脱敏）
     *
     * @param userQueryRequest 分页查询条件
     * @return 分页查询结果
     */
    @Override
    public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取分页参数 current 当前页 和 pageSize 每页大小
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userDomainService.page(new Page<>(current, size),
                userDomainService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userDomainService.getUserVOList(userPage.getRecords());
        // 转换后的用户列表设置到分页对象中
        userVOPage.setRecords(userVO);
        return userVOPage;
    }

    /**
     * 根据 id 集合查询用户列表
     *
     * @param userIdSet id 集合
     * @return 用户列表
     */
    @Override
    public List<User> listByIds(Set<Long> userIdSet) {
        return userDomainService.listByIds(userIdSet);
    }

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 查询条件
     * @return 查询条件
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        return userDomainService.getQueryWrapper(userQueryRequest);
    }
}




