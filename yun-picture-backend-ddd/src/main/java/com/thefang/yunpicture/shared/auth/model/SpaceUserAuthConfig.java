package com.thefang.yunpicture.shared.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 空间成员权限配置
 * @Author Thefang
 * @Create 2025/1/20
 */
@Data
public class SpaceUserAuthConfig implements Serializable {

    private static final long serialVersionUID = 2205898104135486658L;

    /**
     * 权限列表
     */
    private List<SpaceUserPermission> permissions;

    /**
     * 角色列表
     */
    private List<SpaceUserRole> roles;

}
