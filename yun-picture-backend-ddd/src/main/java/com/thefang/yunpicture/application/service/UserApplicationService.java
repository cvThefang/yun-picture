package com.thefang.yunpicture.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.thefang.yunpicture.infrastructure.common.DeleteRequest;
import com.thefang.yunpicture.interfaces.dto.user.UserLoginRequest;
import com.thefang.yunpicture.interfaces.dto.user.UserQueryRequest;
import com.thefang.yunpicture.interfaces.dto.user.UserRegisterRequest;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.interfaces.vo.user.LoginUserVO;
import com.thefang.yunpicture.interfaces.vo.user.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * @author Thefang
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-12-10 23:38:02
 * @versions 1.0.0
 */
public interface UserApplicationService {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求对象
     * @return 用户id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求对象
     * @param request          请求对象
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取加密后的密码
     *
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取登录用户信息(脱敏)
     *
     * @param user 用户实体
     * @return 脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏后的用户信息
     *
     * @param user 用户实体
     * @return 脱敏后的用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户信息列表
     *
     * @param userList 用户实体列表
     * @return 脱敏后的用户信息列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取当前登录用户
     *
     * @param request 请求对象
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求对象
     * @return 是否注销成功
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 添加用户
     *
     * @param user 用户实体
     * @return 用户 id
     */
    long addUser(User user);

    /**
     * 获取用户实体
     *
     * @param id 用户 id
     * @return 用户实体
     */
    User getUserById(long id);

    /**
     * 获取用户信息（脱敏）
     *
     * @param id 用户 id
     * @return 用户信息（脱敏）
     */
    UserVO getUserVOById(long id);

    /**
     * 删除用户
     *
     * @param deleteRequest 删除请求对象
     * @return 是否删除成功
     */
    boolean deleteUser(DeleteRequest deleteRequest);

    /**
     * 更新用户
     *
     * @param user 用户实体
     */
    void updateUser(User user);

    /**
     * 分页查询用户信息（脱敏）
     *
     * @param userQueryRequest 分页查询条件
     * @return 分页查询结果
     */
    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);

    /**
     * 根据 id 集合查询用户列表
     *
     * @param userIdSet id 集合
     * @return 用户列表
     */
    List<User> listByIds(Set<Long> userIdSet);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 查询条件
     * @return 查询条件包装器
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}
