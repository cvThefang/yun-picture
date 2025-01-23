package com.thefang.yunpicture.application.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thefang.yunpicture.application.service.PictureApplicationService;
import com.thefang.yunpicture.application.service.UserApplicationService;
import com.thefang.yunpicture.domain.picture.entity.Picture;
import com.thefang.yunpicture.domain.picture.service.PictureDomainService;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.infrastructure.api.alyunai.model.CreateOutPaintingTaskResponse;
import com.thefang.yunpicture.infrastructure.api.alyunai.model.CreatePictureOutPaintingTaskRequest;
import com.thefang.yunpicture.infrastructure.exception.BusinessException;
import com.thefang.yunpicture.infrastructure.exception.ErrorCode;
import com.thefang.yunpicture.infrastructure.mapper.PictureMapper;
import com.thefang.yunpicture.interfaces.dto.picture.*;
import com.thefang.yunpicture.interfaces.vo.picture.PictureVO;
import com.thefang.yunpicture.interfaces.vo.user.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Thefang
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2024-12-15 14:43:01
 */
@Slf4j
@Service
public class PictureApplicationServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureApplicationService {

    @Resource
    private PictureDomainService pictureDomainService;

    @Resource
    private UserApplicationService userApplicationService;

    /**
     * 校验图片对象
     *
     * @param picture 图片对象
     */
    @Override
    public void validPicture(Picture picture) {
        if (picture == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        picture.validPicture();
    }

    /**
     * 上传图片
     *
     * @param inputSource          文件输入源
     * @param pictureUploadRequest 图片上传请求
     * @param loginUser            登录用户
     * @return 图片封装对象 VO
     */
    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        return pictureDomainService.uploadPicture(inputSource, pictureUploadRequest, loginUser);
    }

    /**
     * 获取图片包装类（单条）
     *
     * @param picture 图片对象
     * @param request 请求对象
     * @return 图片包装类对象
     */
    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userApplicationService.getUserById(userId);
            UserVO userVO = userApplicationService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    /**
     * 获取图片包装类（分页）
     *
     * @param picturePage 图片分页对象
     * @param request     请求对象
     * @return 图片包装类分页对象
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream()
                .map(PictureVO::objToVo)
                .collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        // userIdSet 查出来是 1,2,3,4
        // 这样的集合 这里需要把集合转换成 userId => List<User> 的映射
        // 然后再根据 userId 取出用户信息 根据map对应的 userId 填充到 PictureVO 中
        Map<Long, List<User>> userIdUserListMap = userApplicationService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userApplicationService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }


    /**
     * 查询对象转换成  QueryWrapper 查询条件
     *
     * @param pictureQueryRequest 图片查询请求
     * @return QueryWrapper 查询条件
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        return pictureDomainService.getQueryWrapper(pictureQueryRequest);
    }

    /**
     * 审核图片
     *
     * @param pictureReviewRequest 图片审核请求
     * @param loginUser            登录用户
     */
    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        pictureDomainService.doPictureReview(pictureReviewRequest, loginUser);
    }

    /**
     * 填充审核参数
     *
     * @param picture   图片对象
     * @param loginUser 登录用户
     */
    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        pictureDomainService.fillReviewParams(picture, loginUser);
    }

    /**
     * 批量上传图片
     *
     * @param pictureUploadByBatchRequest 批量上传请求
     * @param loginUser                   登录用户
     * @return 上传成功的图片数量
     */
    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        return pictureDomainService.uploadPictureByBatch(pictureUploadByBatchRequest, loginUser);
    }

    /**
     * 删除图片文件
     * Async注解 可以使得方法被异步调用
     *
     * @param oldPicture 图片对象
     */
    @Async
    @Override
    public void clearPictureFile(Picture oldPicture) {
        pictureDomainService.clearPictureFile(oldPicture);
    }

    /**
     * 删除图片
     *
     * @param pictureId 图片ID
     * @param loginUser 登录用户
     */
    @Override
    public void deletePicture(long pictureId, User loginUser) {
        pictureDomainService.deletePicture(pictureId, loginUser);
    }

    /**
     * 编辑图片
     *
     * @param picture 图片编辑请求
     * @param loginUser          登录用户
     */
    @Override
    public void editPicture(Picture picture, User loginUser) {
        pictureDomainService.editPicture(picture, loginUser);
    }

    /**
     * 校验空间图片的权限
     *
     * @param loginUser 登录用户
     * @param picture   图片对象
     */
    @Override
    public void checkPictureAuth(User loginUser, Picture picture) {
        pictureDomainService.checkPictureAuth(loginUser, picture);
    }

    /**
     * 按照颜色搜图
     *
     * @param spaceId   空间ID
     * @param picColor  颜色
     * @param loginUser 登录用户
     * @return 图片列表
     */
    @Override
    public List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser) {
        return pictureDomainService.searchPictureByColor(spaceId, picColor, loginUser);
    }

    /**
     * 批量编辑图片信息
     *
     * @param pictureEditByBatchRequest 图片编辑请求
     * @param loginUser                 登录用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser) {
        pictureDomainService.editPictureByBatch(pictureEditByBatchRequest, loginUser);
    }

    /**
     * 创建扩图任务
     *
     * @param createPictureOutPaintingTaskRequest 扩图任务请求
     * @param loginUser                           登录用户
     * @return 扩图任务响应
     */
    @Override
    public CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser) {
        return pictureDomainService.createPictureOutPaintingTask(createPictureOutPaintingTaskRequest, loginUser);
    }
}