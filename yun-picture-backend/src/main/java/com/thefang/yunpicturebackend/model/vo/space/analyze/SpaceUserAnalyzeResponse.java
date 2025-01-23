package com.thefang.yunpicturebackend.model.vo.space.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description 空间用户上传行为分析响应类
 * @Author Thefang
 * @Create 2025/1/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceUserAnalyzeResponse implements Serializable {

    private static final long serialVersionUID = -8230557171604257391L;

    /**
     * 时间区间
     */
    private String period;

    /**
     * 上传数量
     */
    private Long count;

}

