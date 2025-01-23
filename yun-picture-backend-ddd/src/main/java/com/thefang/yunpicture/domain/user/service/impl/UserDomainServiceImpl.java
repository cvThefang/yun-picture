package com.thefang.yunpicture.domain.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thefang.yunpicture.domain.user.repository.UserRepository;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.domain.user.service.UserDomainService;
import com.thefang.yunpicture.domain.user.valueobject.UserRoleEnum;
import com.thefang.yunpicture.infrastructure.exception.BusinessException;
import com.thefang.yunpicture.infrastructure.exception.ErrorCode;
import com.thefang.yunpicture.infrastructure.exception.ThrowUtils;
import com.thefang.yunpicture.interfaces.dto.user.UserLoginRequest;
import com.thefang.yunpicture.interfaces.dto.user.UserQueryRequest;
import com.thefang.yunpicture.interfaces.dto.user.UserRegisterRequest;
import com.thefang.yunpicture.interfaces.vo.user.LoginUserVO;
import com.thefang.yunpicture.interfaces.vo.user.UserVO;
import com.thefang.yunpicturebackend.manager.auth.StpKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.thefang.yunpicture.domain.user.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author Thefang
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-12-10 23:38:02
 */
@Service
@Slf4j
public class UserDomainServiceImpl implements UserDomainService {

    @Resource
    private UserRepository userRepository;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求对象
     * @return 用户id
     */
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 2. 检查是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userRegisterRequest.getUserAccount());
        long count = userRepository.getBaseMapper().selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 3. 加密
        String encryptPassword = getEncryptPassword(userRegisterRequest.getUserPassword());
        // 4. 插入数据
        User user = new User();
        user.setUserAccount(userRegisterRequest.getUserAccount());
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = userRepository.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
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
        // 2. 加密
        String encryptPassword = getEncryptPassword(userLoginRequest.getUserPassword());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userLoginRequest.getUserAccount());
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userRepository.getBaseMapper().selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态 保存登录信息
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 4. 记录用户登录态到 Sa-token，便于空间鉴权时使用，注意保证该用户信息与 SpringSession 中的信息过期时间一致
        StpKit.SPACE.login(user.getId());
        StpKit.SPACE.getSession().set(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取加密后的密码
     *
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        // 加盐，防止暴力破解，混淆密码
        final String SALT = "Thefang";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     * 获取登录用户信息（脱敏）
     *
     * @param user 用户实体
     * @return 登录用户信息（脱敏）
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            // 如果用户为空，直接返回不用new一个对象，节省资源
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取用户信息（脱敏）
     *
     * @param user 用户实体
     * @return 用户信息（脱敏）
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取用户信息（脱敏）列表
     *
     * @param userList 用户实体列表
     * @return 用户信息（脱敏）列表
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * 获取登录用户
     *
     * @param request 请求对象
     * @return 登录用户实体
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 获取会话中的用户对象
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 检查会话属性是否存在且为 User 类型
        if (!(userObj instanceof User)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        User currentUser = (User) userObj;
        if (currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库中查询，（追求性能的话可以注释 直接返回上述结果）
        Long userId = currentUser.getId();
        currentUser = userRepository.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 用户登录态注销
     *
     * @param request 请求对象
     * @return 是否注销成功
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }


    /**
     * 获取查询条件
     *
     * @param userQueryRequest 查询条件
     * @return 查询条件
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        // 构造查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 添加用户
     *
     * @param user 用户实体
     * @return 用户 id
     */
    @Override
    public Long addUser(User user) {
        // 默认密码 12345678
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = this.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean result = userRepository.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return user.getId();
    }

    /**
     * 删除用户
     *
     * @param id 用户 id
     * @return 是否删除成功
     */
    @Override
    public Boolean removeById(Long id) {
        return userRepository.removeById(id);
    }

    /**
     * 更新用户
     *
     * @param user 用户实体
     * @return 是否更新成功
     */
    @Override
    public boolean updateById(User user) {
        return userRepository.updateById(user);
    }

    /**
     * 根据 id 获取用户信息
     *
     * @param id 用户 id
     * @return 用户实体
     */
    @Override
    public User getById(long id) {
        return userRepository.getById(id);
    }

    /**
     * 分页查询用户列表
     *
     * @param userPage     分页对象
     * @param queryWrapper 查询条件
     * @return 分页对象
     */
    @Override
    public Page<User> page(Page<User> userPage, QueryWrapper<User> queryWrapper) {
        return userRepository.page(userPage, queryWrapper);
    }

    /**
     * 根据 id 列表获取用户列表
     *
     * @param userIdSet id 列表
     * @return 用户列表
     */
    @Override
    public List<User> listByIds(Set<Long> userIdSet) {
        return userRepository.listByIds(userIdSet);
    }
}




