package com.thefang.yunpicture.interfaces.dto.space.analyze;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 空间用户上传行为分析请求对象
 * @Author Thefang
 * @Create 2025/1/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUserAnalyzeRequest extends SpaceAnalyzeRequest {

    private static final long serialVersionUID = -8786524462163881886L;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 时间维度：day / week / month
     */
    private String timeDimension;

}
