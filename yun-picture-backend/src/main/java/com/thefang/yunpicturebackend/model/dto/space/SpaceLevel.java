package com.thefang.yunpicturebackend.model.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description 空间级别
 * @Author Thefang
 * @Create 2025/1/6
 */
@Data
@AllArgsConstructor
public class SpaceLevel {

    /**
     * 空间级别值
     */
    private int value;

    /**
     * 空间级别中文
     */
    private String text;

    /**
     * 空间级别最大数量
     */
    private long maxCount;

    /**
     * 空间级别最大容量
     */
    private long maxSize;

}

