package com.thefang.yunpicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 以图搜图请求对象
 * @Author Thefang
 * @Create 2025/1/12
 */
@Data
public class SearchPictureByPictureRequest implements Serializable {

    private static final long serialVersionUID = 7000848001514790640L;

    /**
     * 图片 id
     */
    private Long pictureId;
}
