package com.thefang.yunpicturebackend.common;

import lombok.Data;

/**
 * @Description 通用的分页请求类
 * @Author Thefang
 * @Create 2024/12/8
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private  int current;

    /**
     * 页面大小
     */
    private  int pageSize;

    /**
     * 排序字段
     */
    private  String sortField;

    /**
     * 排序顺序（默认排序）
     */
    private  String sortOrder = "descend";
}
