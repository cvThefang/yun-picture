package com.thefang.yunpicturebackend.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description 图片标签分类
 * @Author Thefang
 * @Create 2024/12/16
 */
@Data
public class PictureTagCategory {

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 分类列表
     */
    private List<String> categoryList;
}
