package com.thefang.yunpicturebackend.manager.websocket.disruptor;

import com.thefang.yunpicturebackend.manager.websocket.model.PictureEditRequestMessage;
import com.thefang.yunpicturebackend.model.entity.User;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
 * @Description 图片编辑事件
 * @Author Thefang
 * @Create 2025/1/22
 */
@Data
public class PictureEditEvent {

    /**
     * 消息
     */
    private PictureEditRequestMessage pictureEditRequestMessage;

    /**
     * 当前用户的 session
     */
    private WebSocketSession session;

    /**
     * 当前用户
     */
    private User user;

    /**
     * 图片 id
     */
    private Long pictureId;

}
