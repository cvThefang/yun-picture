package com.thefang.yunpicture.infrastructure.api.imagesearch.sub;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.thefang.yunpicture.infrastructure.exception.BusinessException;
import com.thefang.yunpicture.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 获取以图搜图的页面地址的 API（step 1）
 * @Author Thefang
 * @Create 2025/1/12
 */
@Slf4j
public class GetImagePageUrlApi {

    /**
     * 获取以图搜图的页面地址
     *
     * @param imageUrl 图片地址
     * @return 以图搜图的页面地址
     */
    public static String getImagePageUrl(String imageUrl) {
        // 此方法是调用了百度的以图搜图的功能
        // 1. 准备请求参数
        Map<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");
        // 获取当前时间戳
        long uptime = System.currentTimeMillis();
        // 构造请求地址
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;
        try {
            // 2. 发送请求
            HttpResponse httpResponse = HttpRequest.post(url)
                    .form(formData)
                    .timeout(5000)
                    .execute();
            // 3. 获取响应结果 解析响应结果
            if (httpResponse.getStatus() != HttpStatus.HTTP_OK) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            // 解析响应
            String body = httpResponse.body();
            // 将body转换成一个map的结构
            Map<String, Object> result = JSONUtil.toBean(body, Map.class);

            // 3. 处理响应结果
            if (result == null || !Integer.valueOf(0).equals(result.get("status"))) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            // 如果响应结果是0，则获取data数据
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            // 拿到url
            String rawUrl = (String) data.get("url");
            // 对url进行解码 用hutool的urlUtil工具解码url 因为原始的url可能会存在未解码的字符串导致请求失败
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            // 如果url为空，则抛出异常
            if (StrUtil.isBlankIfStr(searchResultUrl)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效的结果地址");
            }
            return searchResultUrl;
        } catch (Exception e) {
            log.error("获取以图搜图的页面地址失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
        }
    }

    // 示例用法
    public static void main(String[] args) {
        String imageUrl = "https://img0.baidu.com/it/u=123639004,4073167453&fm=253&fmt=auto&app=138&f=JPEG?w=665&h=665";
        String searchResultUrl = getImagePageUrl(imageUrl);
        System.out.println("搜索成功，结果 URL：" + searchResultUrl);
    }
}
