package com.thefang.yunpicture.infrastructure.api.imagesearch.model;

import lombok.Data;

/**
 * @Description 图片搜索结果类
 * @Author Thefang
 * @Create 2025/1/12
 */
@Data
public class ImageSearchResult {

    /**
     * 缩略图地址
     */
    private String thumbUrl;

    /**
     * 来源地址
     */
    private String fromUrl;

}
