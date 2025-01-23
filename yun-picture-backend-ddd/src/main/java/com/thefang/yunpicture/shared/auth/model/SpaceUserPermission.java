package com.thefang.yunpicture.shared.auth.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 空间成员权限
 * @Author Thefang
 * @Create 2025/1/20
 */
@Data
public class SpaceUserPermission implements Serializable {

    private static final long serialVersionUID = 8968288817685431376L;

    /**
     * 权限键
     */
    private String key;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限描述
     */
    private String description;

}
