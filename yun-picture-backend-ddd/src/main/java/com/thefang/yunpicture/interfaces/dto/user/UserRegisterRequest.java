package com.thefang.yunpicture.interfaces.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 用户注册请求对象
 * @Author Thefang
 * @Create 2024/12/11
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 7084338699723288198L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;

}
