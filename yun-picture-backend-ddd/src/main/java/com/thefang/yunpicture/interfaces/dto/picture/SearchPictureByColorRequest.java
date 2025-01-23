package com.thefang.yunpicture.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 颜色搜索图片的请求对象
 * @Author Thefang
 * @Create 2025/1/12
 */
@Data
public class SearchPictureByColorRequest implements Serializable {

    private static final long serialVersionUID = -2014814110388986876L;

    /**
     * 图片主色调
     */
    private String picColor;

    /**
     * 空间 id
     */
    private Long spaceId;

}
