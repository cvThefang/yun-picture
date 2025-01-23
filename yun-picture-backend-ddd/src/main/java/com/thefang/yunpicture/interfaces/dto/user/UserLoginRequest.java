package com.thefang.yunpicture.interfaces.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 用户登录请求对象
 * @Author Thefang
 * @Create 2024/12/11
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -6686986753267299231L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

}
