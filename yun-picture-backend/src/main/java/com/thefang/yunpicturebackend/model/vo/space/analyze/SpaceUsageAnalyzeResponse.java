package com.thefang.yunpicturebackend.model.vo.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 空间资源使用分析响应类
 * @Author Thefang
 * @Create 2025/1/19
 */
@Data
public class SpaceUsageAnalyzeResponse implements Serializable {

    private static final long serialVersionUID = -3547055933552968172L;

    /**
     * 已使用大小
     */
    private Long usedSize;

    /**
     * 总大小
     */
    private Long maxSize;

    /**
     * 空间使用比例
     */
    private Double sizeUsageRatio;

    /**
     * 当前图片数量
     */
    private Long usedCount;

    /**
     * 最大图片数量
     */
    private Long maxCount;

    /**
     * 图片数量占比
     */
    private Double countUsageRatio;

}
