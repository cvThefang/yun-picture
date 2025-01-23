package com.thefang.yunpicturebackend.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 编辑空间成员请求对象
 * @Author Thefang
 * @Create 2025/1/20
 */
@Data
public class SpaceUserEditRequest implements Serializable {

    private static final long serialVersionUID = -1867406965320915460L;

    /**
     * id
     */
    private Long id;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

}
