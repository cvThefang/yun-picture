package com.thefang.yunpicturebackend.model.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 空间使用排行分析请求对象
 * @Author Thefang
 * @Create 2025/1/19
 */
@Data
public class SpaceRankAnalyzeRequest implements Serializable {

    private static final long serialVersionUID = -3837056725850499855L;
    
    /**
     * 排名前 N 的空间 默认取前 10
     */
    private Integer topN = 10;

}

