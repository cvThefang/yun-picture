package com.thefang.yunpicturebackend.manager.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 图片编辑请求消息
 * @Author Thefang
 * @Create 2025/1/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureEditRequestMessage {

    /**
     * 消息类型，例如 "ENTER_EDIT", "EXIT_EDIT", "EDIT_ACTION"
     */
    private String type;

    /**
     * 执行的编辑动作
     */
    private String editAction;
}

