package com.thefang.yunpicture.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 图片编辑请求
 * @Author Thefang
 * @Create 2024/12/15
 */
@Data
public class PictureEditRequest implements Serializable {

    private static final long serialVersionUID = 7167644820881597073L;

    /**
     * id
     */
    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;
}
