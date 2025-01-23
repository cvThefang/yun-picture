package com.thefang.yunpicturebackend.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 添加空间成员请求对象
 * @Author Thefang
 * @Create 2025/1/20
 */
@Data
public class SpaceUserAddRequest implements Serializable {

    private static final long serialVersionUID = 3365623909665795361L;

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

}
