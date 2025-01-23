package com.thefang.yunpicture.interfaces.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 空间更新请求对象
 * @Author Thefang
 * @Create 2025/1/3
 */
@Data
public class SpaceUpdateRequest implements Serializable {

    private static final long serialVersionUID = -4446730768358913007L;
    
    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;

}
