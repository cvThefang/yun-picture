package com.thefang.yunpicturebackend.common;

import com.thefang.yunpicturebackend.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description 全局响应封装类
 * @Author Thefang
 * @Create 2024/12/8
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 6958925150559294943L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 数据
     */
    private T data;

    /**
     * 消息
     */
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }
    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
