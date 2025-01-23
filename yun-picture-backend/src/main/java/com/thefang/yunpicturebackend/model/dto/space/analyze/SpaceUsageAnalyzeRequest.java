package com.thefang.yunpicturebackend.model.dto.space.analyze;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 空间资源使用分析请求对象
 * @Author Thefang
 * @Create 2025/1/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUsageAnalyzeRequest extends SpaceAnalyzeRequest {

    private static final long serialVersionUID = -5972443784336190283L;

}
