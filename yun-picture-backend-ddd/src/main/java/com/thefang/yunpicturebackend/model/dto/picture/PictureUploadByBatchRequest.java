package com.thefang.yunpicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 批量上传图片请求对象
 * @Author Thefang
 * @Create 2024/12/26
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {

    private static final long serialVersionUID = -8641537955538131243L;
    
    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 抓取数量
     */
    private Integer count = 10;

    /**
     * 图片名称前缀
     */
    private String namePrefix;

}
