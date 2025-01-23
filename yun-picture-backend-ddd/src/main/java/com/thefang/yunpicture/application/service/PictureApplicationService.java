package com.thefang.yunpicture.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.thefang.yunpicture.infrastructure.api.alyunai.model.CreateOutPaintingTaskResponse;
import com.thefang.yunpicture.infrastructure.api.alyunai.model.CreatePictureOutPaintingTaskRequest;
import com.thefang.yunpicture.interfaces.dto.picture.*;
import com.thefang.yunpicture.domain.picture.entity.Picture;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.interfaces.vo.picture.PictureVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Thefang
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2024-12-15 14:43:01
 */
public interface PictureApplicationService extends IService<Picture> {

    /**
     * 校验图片对象
     *
     * @param picture 图片对象
     */
    void validPicture(Picture picture);

    /**
     * 上传图片
     *
     * @param inputSource          文件输入源
     * @param pictureUploadRequest 图片上传请求
     * @param loginUser            登录用户
     * @return 图片信息 VO
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 获取图片包装类（单条）
     *
     * @param picture 图片对象
     * @param request 请求对象
     * @return 图片包装类对象
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取图片包装类（分页）
     *
     * @param picturePage 图片分页对象
     * @param request     请求对象
     * @return 图片包装类分页对象
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 查询对象转换成  QueryWrapper 查询条件
     *
     * @param pictureQueryRequest 图片查询请求
     * @return QueryWrapper 查询条件
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest 图片审核请求
     * @param loginUser            登录用户
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 填充审核参数
     *
     * @param picture   图片对象
     * @param loginUser 登录用户
     */
    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 批量上传图片
     *
     * @param pictureUploadByBatchRequest 批量上传请求
     * @param loginUser                   登录用户
     * @return 上传成功的图片数量
     */
    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);

    /**
     * 清理图片文件
     *
     * @param oldPicture 图片对象
     */
    void clearPictureFile(Picture oldPicture);

    /**
     * 删除图片
     *
     * @param pictureId 图片ID
     * @param loginUser 登录用户
     */
    void deletePicture(long pictureId, User loginUser);

    /**
     * 编辑图片
     *
     * @param picture 图片编辑请求
     * @param loginUser          登录用户
     */
    void editPicture(Picture picture , User loginUser);

    /**
     * 校验空间图片的权限
     *
     * @param loginUser 登录用户
     * @param picture   图片对象
     */
    void checkPictureAuth(User loginUser, Picture picture);

    /**
     * 按照颜色搜图
     *
     * @param spaceId   空间ID
     * @param picColor  颜色
     * @param loginUser 登录用户
     * @return 图片包装类列表
     */
    List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    /**
     * 批量编辑图片信息
     *
     * @param pictureEditByBatchRequest 图片编辑请求
     * @param loginUser                 登录用户
     */
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

    /**
     * 创建扩图任务
     *
     * @param createPictureOutPaintingTaskRequest 扩图任务请求
     * @param loginUser                           登录用户
     */
    CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser);

}
