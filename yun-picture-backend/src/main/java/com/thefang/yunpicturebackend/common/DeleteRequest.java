package com.thefang.yunpicturebackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 通用的删除请求类
 * @Author Thefang
 * @Create 2024/12/8
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
