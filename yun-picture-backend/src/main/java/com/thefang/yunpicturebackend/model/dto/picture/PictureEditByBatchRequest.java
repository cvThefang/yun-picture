package com.thefang.yunpicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 图片批量编辑请求类
 * @Author Thefang
 * @Create 2025/1/14
 */
@Data
public class PictureEditByBatchRequest implements Serializable {

    private static final long serialVersionUID = 3598586008579106970L;

    /**
     * 图片 id 列表
     */
    private List<Long> pictureIdList;

    /**
     * 空间 id
     */
    private Long spaceId;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 命名规则
     */
    private String nameRule;

}
