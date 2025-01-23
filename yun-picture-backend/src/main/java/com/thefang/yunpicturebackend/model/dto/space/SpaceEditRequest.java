package com.thefang.yunpicturebackend.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 空间编辑请求对象
 * @Author Thefang
 * @Create 2025/1/3
 */
@Data
public class SpaceEditRequest implements Serializable {

    private static final long serialVersionUID = 1198941288542597762L;

    /**
     * 空间 id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

}
