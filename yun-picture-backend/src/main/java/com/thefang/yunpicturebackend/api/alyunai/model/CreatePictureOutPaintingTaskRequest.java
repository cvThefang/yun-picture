package com.thefang.yunpicturebackend.api.alyunai.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description AI 扩图请求类
 * @Author Thefang
 * @Create 2025/1/18
 */
@Data
public class CreatePictureOutPaintingTaskRequest implements Serializable {

    private static final long serialVersionUID = -7078540739969522271L;
    
    /**
     * 图片 id
     */
    private Long pictureId;

    /**
     * 扩图参数
     */
    private CreateOutPaintingTaskRequest.Parameters parameters;

}
