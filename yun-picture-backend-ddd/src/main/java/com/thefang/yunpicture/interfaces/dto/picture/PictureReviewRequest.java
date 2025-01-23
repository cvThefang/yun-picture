package com.thefang.yunpicture.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 图片审核请求类
 * @Author Thefang
 * @Create 2024/12/23
 */
@Data
public class PictureReviewRequest implements Serializable {

    private static final long serialVersionUID = -390049660236669582L;

    /**
     * id
     */
    private Long id;

    /**
     * 状态：0-待审核, 1-通过, 2-拒绝  
     */
    private Integer reviewStatus;

    /**
     * 审核信息  
     */
    private String reviewMessage;
    
}
