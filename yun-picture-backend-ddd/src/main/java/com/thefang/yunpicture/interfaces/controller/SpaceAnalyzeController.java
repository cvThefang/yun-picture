package com.thefang.yunpicture.interfaces.controller;

import com.thefang.yunpicture.infrastructure.common.BaseResponse;
import com.thefang.yunpicture.infrastructure.common.ResultUtils;
import com.thefang.yunpicture.infrastructure.exception.ErrorCode;
import com.thefang.yunpicture.infrastructure.exception.ThrowUtils;
import com.thefang.yunpicture.interfaces.dto.space.analyze.*;
import com.thefang.yunpicture.interfaces.vo.space.analyze.*;
import com.thefang.yunpicture.domain.space.entity.Space;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.application.service.SpaceAnalyzeApplicationService;
import com.thefang.yunpicture.application.service.UserApplicationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description 空间分析相关接口
 * @Author Thefang
 * @Create 2025/1/19
 */
@RestController
@RequestMapping("/space/analyze")
public class SpaceAnalyzeController {

    @Resource
    private SpaceAnalyzeApplicationService spaceAnalyzeApplicationService;

    @Resource
    private UserApplicationService userApplicationService;

    /**
     * 获取空间使用情况
     *
     * @param spaceUsageAnalyzeRequest 空间使用情况请求参数
     * @param request                  请求对象
     * @return 空间使用情况响应对象
     */
    @PostMapping("/usage")
    public BaseResponse<SpaceUsageAnalyzeResponse> getSpaceUsageAnalyze(@RequestBody SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest,
                                                                        HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUsageAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userApplicationService.getLoginUser(request);
        SpaceUsageAnalyzeResponse spaceUsageAnalyze = spaceAnalyzeApplicationService.getSpaceUsageAnalyze(spaceUsageAnalyzeRequest, loginUser);
        return ResultUtils.success(spaceUsageAnalyze);
    }

    /**
     * 获取图片空间分类分析
     *
     * @param spaceCategoryAnalyzeRequest 图片空间分类分析请求参数
     * @param request                     请求对象
     * @return 图片空间分类分析响应对象
     */
    @PostMapping("/category")
    public BaseResponse<List<SpaceCategoryAnalyzeResponse>> getSpaceCategoryAnalyze(@RequestBody SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userApplicationService.getLoginUser(request);
        List<SpaceCategoryAnalyzeResponse> resultList = spaceAnalyzeApplicationService.getSpaceCategoryAnalyze(spaceCategoryAnalyzeRequest, loginUser);
        return ResultUtils.success(resultList);
    }

    /**
     * 获取图片空间标签分析
     *
     * @param spaceTagAnalyzeRequest 图片空间标签分析请求参数
     * @param request                请求对象
     * @return 图片空间标签分析响应对象
     */
    @PostMapping("/tag")
    public BaseResponse<List<SpaceTagAnalyzeResponse>> getSpaceTagAnalyze(@RequestBody SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userApplicationService.getLoginUser(request);
        List<SpaceTagAnalyzeResponse> resultList = spaceAnalyzeApplicationService.getSpaceTagAnalyze(spaceTagAnalyzeRequest, loginUser);
        return ResultUtils.success(resultList);
    }

    /**
     * 获取图片空间大小分析
     *
     * @param spaceSizeAnalyzeRequest 图片空间大小分析请求参数
     * @param request                 请求对象
     * @return 图片空间大小分析响应对象
     */
    @PostMapping("/size")
    public BaseResponse<List<SpaceSizeAnalyzeResponse>> getSpaceSizeAnalyze(@RequestBody SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userApplicationService.getLoginUser(request);
        List<SpaceSizeAnalyzeResponse> resultList = spaceAnalyzeApplicationService.getSpaceSizeAnalyze(spaceSizeAnalyzeRequest, loginUser);
        return ResultUtils.success(resultList);
    }

    /**
     * 获取空间用户上传行为分析
     *
     * @param spaceUserAnalyzeRequest 空间用户上传行为分析请求参数
     * @param request                 请求对象
     * @return 空间用户上传行为分析响应对象
     */
    @PostMapping("/user")
    public BaseResponse<List<SpaceUserAnalyzeResponse>> getSpaceUserAnalyze(@RequestBody SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userApplicationService.getLoginUser(request);
        List<SpaceUserAnalyzeResponse> resultList = spaceAnalyzeApplicationService.getSpaceUserAnalyze(spaceUserAnalyzeRequest, loginUser);
        return ResultUtils.success(resultList);
    }


    /**
     * 获取空间使用排行分析
     *
     * @param spaceRankAnalyzeRequest 空间使用排行分析请求参数
     * @param request                 请求对象
     * @return 空间使用排行分析响应对象
     */
    @PostMapping("/rank")
    public BaseResponse<List<Space>> getSpaceRankAnalyze(@RequestBody SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userApplicationService.getLoginUser(request);
        List<Space> resultList = spaceAnalyzeApplicationService.getSpaceRankAnalyze(spaceRankAnalyzeRequest, loginUser);
        return ResultUtils.success(resultList);
    }
}
