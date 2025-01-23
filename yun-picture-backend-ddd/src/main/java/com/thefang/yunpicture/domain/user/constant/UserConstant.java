package com.thefang.yunpicture.domain.user.constant;

/**
 * @Description 用户常量
 * @Author Thefang
 * @Create 2024/12/12
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限 （idea可以使用这个用法可以实现折叠效果 region 和 endregion）

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    // endregion
}
