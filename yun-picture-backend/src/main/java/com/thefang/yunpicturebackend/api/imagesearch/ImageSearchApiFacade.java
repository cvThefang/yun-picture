package com.thefang.yunpicturebackend.api.imagesearch;

import com.thefang.yunpicturebackend.api.imagesearch.model.ImageSearchResult;
import com.thefang.yunpicturebackend.api.imagesearch.sub.GetImageFirstUrlApi;
import com.thefang.yunpicturebackend.api.imagesearch.sub.GetImageListApi;
import com.thefang.yunpicturebackend.api.imagesearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Description 图片搜索服务（门面模式）
 * 注意：这里运用一种设计模式来提供图片搜索服务。门面模式-通过提供一个统一的接口来简化多个接口的调用，使得客户端不需要关注内部的具体实现。
 * 作用：将多个 API 整合到一个门面类中，简化调用过程。
 * @Author Thefang
 * @Create 2025/1/12
 */
@Slf4j
public class ImageSearchApiFacade {

    /**
     * 获取图片
     *
     * @param imageUrl 图片 URL
     * @return 图片搜索结果列表
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        // 1. 获取以图搜图的页面地址的 URL
        String searchResultUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        // 2. 获取图片列表页面地址的 URL
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(searchResultUrl);
        // 3. 获取图片列表内容
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
        return imageList;
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://img0.baidu.com/it/u=123639004,4073167453&fm=253&fmt=auto&app=138&f=JPEG?w=665&h=665";
        List<ImageSearchResult> resultList = searchImage(imageUrl);
        System.out.println("结果列表" + resultList);
    }
}
