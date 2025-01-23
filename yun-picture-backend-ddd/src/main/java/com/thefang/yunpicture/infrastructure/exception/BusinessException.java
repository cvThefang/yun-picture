package com.thefang.yunpicture.infrastructure.exception;

import lombok.Getter;

/**
 * @Description 自定义业务异常类
 * @Author Thefang
 * @Create 2024/12/8
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -9023417657546513649L;

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

}
