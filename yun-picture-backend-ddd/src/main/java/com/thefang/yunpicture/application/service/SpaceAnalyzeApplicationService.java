package com.thefang.yunpicture.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.thefang.yunpicture.interfaces.dto.space.analyze.*;
import com.thefang.yunpicture.interfaces.vo.space.analyze.*;
import com.thefang.yunpicture.domain.space.entity.Space;
import com.thefang.yunpicture.domain.user.entity.User;

import java.util.List;

/**
 * @Description 空间分析服务
 * @Author Thefang
 * @Create 2025/1/19
 */
public interface SpaceAnalyzeApplicationService {

    /**
     * 获取空间使用情况分析
     *
     * @param spaceUsageAnalyzeRequest 空间使用情况分析请求
     * @param loginUser                登录用户
     * @return 空间使用情况分析响应类
     */
    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);

    /**
     * 获取空间图片分类分析
     *
     * @param spaceCategoryAnalyzeRequest 空间图片分类分析请求对象
     * @param loginUser                   登录用户
     * @return 空间图片分类分析响应类
     */
    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);

    /**
     * 获取空间图片标签分析
     *
     * @param spaceTagAnalyzeRequest 空间图片标签分析请求对象
     * @param loginUser              登录用户
     * @return 空间图片标签分析响应类
     */
    List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser);

    /**
     * 获取空间图片大小分析
     *
     * @param spaceSizeAnalyzeRequest 空间图片大小分析请求对象
     * @param loginUser               登录用户
     * @return 空间图片大小分析响应类
     */
    List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);

    /**
     * 获取空间用户上传行为分析
     *
     * @param spaceUserAnalyzeRequest 空间用户上传行为分析请求对象
     * @param loginUser               登录用户
     * @return 空间用户上传行为分析响应类
     */
    List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser);

    /**
     * 获取空间使用排行分析
     *
     * @param spaceRankAnalyzeRequest 空间使用排行分析请求对象
     * @param loginUser               登录用户
     * @return 空间使用排行分析响应类
     */
    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);

}
