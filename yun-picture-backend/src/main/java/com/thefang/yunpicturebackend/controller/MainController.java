package com.thefang.yunpicturebackend.controller;

import com.thefang.yunpicturebackend.common.BaseResponse;
import com.thefang.yunpicturebackend.common.ResultUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 健康检查接口
 * @Author Thefang
 * @Create 2024/12/8
 */
@RestController
@RequestMapping("/")
public class MainController {

    /**
     * 健康检查接口
     */
    @RequestMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}
