package com.thefang.yunpicturebackend.model.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 通用空间分析请求对象
 * @Author Thefang
 * @Create 2025/1/19
 */
@Data
public class SpaceAnalyzeRequest implements Serializable {

    private static final long serialVersionUID = -5087778124679354510L;

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 是否查询公共图库
     */
    private boolean queryPublic;

    /**
     * 全空间分析
     */
    private boolean queryAll;

}
