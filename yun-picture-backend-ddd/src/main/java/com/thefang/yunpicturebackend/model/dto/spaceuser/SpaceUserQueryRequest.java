package com.thefang.yunpicturebackend.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 查询空间成员请求对象
 * @Author Thefang
 * @Create 2025/1/20
 */
@Data
public class SpaceUserQueryRequest implements Serializable {

    private static final long serialVersionUID = -8501840301355968145L;
    
    /**
     * ID
     */
    private Long id;

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

