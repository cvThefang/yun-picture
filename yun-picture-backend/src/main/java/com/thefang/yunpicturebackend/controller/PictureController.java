package com.thefang.yunpicturebackend.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.thefang.yunpicturebackend.annotation.AuthCheck;
import com.thefang.yunpicturebackend.api.alyunai.AliYunAiApi;
import com.thefang.yunpicturebackend.api.alyunai.model.CreateOutPaintingTaskResponse;
import com.thefang.yunpicturebackend.api.alyunai.model.CreatePictureOutPaintingTaskRequest;
import com.thefang.yunpicturebackend.api.alyunai.model.GetOutPaintingTaskResponse;
import com.thefang.yunpicturebackend.api.imagesearch.ImageSearchApiFacade;
import com.thefang.yunpicturebackend.api.imagesearch.model.ImageSearchResult;
import com.thefang.yunpicturebackend.common.BaseResponse;
import com.thefang.yunpicturebackend.common.DeleteRequest;
import com.thefang.yunpicturebackend.common.ResultUtils;
import com.thefang.yunpicturebackend.constant.UserConstant;
import com.thefang.yunpicturebackend.exception.BusinessException;
import com.thefang.yunpicturebackend.exception.ErrorCode;
import com.thefang.yunpicturebackend.exception.ThrowUtils;
import com.thefang.yunpicturebackend.manager.auth.SpaceUserAuthManager;
import com.thefang.yunpicturebackend.manager.auth.StpKit;
import com.thefang.yunpicturebackend.manager.auth.annotation.SaSpaceCheckPermission;
import com.thefang.yunpicturebackend.manager.auth.model.SpaceUserPermissionConstant;
import com.thefang.yunpicturebackend.model.dto.picture.*;
import com.thefang.yunpicturebackend.model.entity.Picture;
import com.thefang.yunpicturebackend.model.entity.Space;
import com.thefang.yunpicturebackend.model.entity.User;
import com.thefang.yunpicturebackend.model.enums.PictureReviewStatusEnum;
import com.thefang.yunpicturebackend.model.vo.PictureTagCategory;
import com.thefang.yunpicturebackend.model.vo.PictureVO;
import com.thefang.yunpicturebackend.service.PictureService;
import com.thefang.yunpicturebackend.service.SpaceService;
import com.thefang.yunpicturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description 图片相关的接口
 * @Author Thefang
 * @Create 2024/12/15
 */
@Slf4j
@RestController
@RequestMapping("/picture")
public class PictureController {

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private AliYunAiApi aliYunAiApi;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    /**
     * 本地缓存
     */
    private final Cache<String, String> LOCAL_CACHE = Caffeine.newBuilder()
            .initialCapacity(1024) // 初始容量
            .maximumSize(10000L) // 最大容量
            // 缓存 5 分钟移除
            .expireAfterWrite(5L, TimeUnit.MINUTES)
            .build();


    /**
     * 上传图片 （可重新上传）
     *
     * @param multipartFile        文件
     * @param pictureUploadRequest 图片信息
     * @param request              请求
     * @return 图片封装的VO
     */
    @PostMapping("/upload")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_UPLOAD)
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureVO> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 通过url上传图片 （可重新上传）
     *
     * @param pictureUploadRequest 图片信息
     * @param request              请求
     * @return 图片封装的VO
     */
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_UPLOAD)
    @PostMapping("/upload/url")
    public BaseResponse<PictureVO> uploadPictureByUrl(
            @RequestBody PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String fileUrl = pictureUploadRequest.getFileUrl();
        PictureVO pictureVO = pictureService.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 删除图片
     *
     * @param deleteRequest 图片id
     * @param request       请求
     * @return 成功或失败
     */
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_DELETE)
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 删除图片不用权限校验注解@AuthCheck，是因为管理员和自己都能删除图片（自己仅能删除本人的图片）
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        pictureService.deletePicture(id, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 更新图片信息（仅管理员可用）
     *
     * @param pictureUpdateRequest 图片更新信息
     * @param request              请求
     * @return 成功或失败
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,
                                               HttpServletRequest request) {
        if (pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 数据校验
        pictureService.validPicture(picture);
        // 判断是否存在
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 补充审核参数
        pictureService.fillReviewParams(picture, loginUser);
        // 操作数据库
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 获取图片信息（仅管理员可用）
     *
     * @param id      图片id
     * @param request 请求
     * @return 图片信息
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(picture);
    }

    /**
     * 获取图片包装类
     *
     * @param id      图片id
     * @param request 请求
     * @return 图片包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Picture picture = pictureService.getById(id);
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        // 为了防止用户直接通过url拼接id获取未过审的图片，这里做一下数据过滤
        // 空间权限校验 图片所属的空间必须是已登录用户有的权限
        Long spaceId = picture.getSpaceId();
        Space space = null;
        // 只对私有空间做权限校验（公共图库的图片不需要校验权限）
        if (spaceId == null) {
            // 如果是公共空间，允许上传的用户和管理员看到刚上传 但是未过审的图片 其他用户则不允许看到
            if (!loginUser.getId().equals(picture.getUserId()) && !userService.isAdmin(loginUser)) {
                ThrowUtils.throwIf(picture.getReviewStatus() != PictureReviewStatusEnum.PASS.getValue(), ErrorCode.NOT_FOUND_ERROR);
            }
        } else {
            // 使用 Sa-Token 编程式权限校验
            boolean hasPermission = StpKit.SPACE.hasPermission(SpaceUserPermissionConstant.PICTURE_VIEW);
            ThrowUtils.throwIf(!hasPermission, ErrorCode.NO_AUTH_ERROR, "没有权限访问该图片");

//            // 私有空间，校验权限
//            Space space = spaceService.getById(spaceId);
//            // 空间不存在
//            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
//            // 空间存在，仅空间的管理员可以访问
//            if (!loginUser.getId().equals(space.getUserId())) {
//                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间权限");
//            }

            space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        }
        List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
        // 获取封装类
        PictureVO pictureVO = pictureService.getPictureVO(picture, request);
        pictureVO.setPermissionList(permissionList);
        // 返回封装类结果集对象
        return ResultUtils.success(pictureVO);
    }

    /**
     * 分页获取图片列表（仅管理员可用）
     *
     * @param pictureQueryRequest 图片查询条件
     * @return 分页图片列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(picturePage);
    }

    /**
     * 分页获取图片列表（包装类）
     *
     * @param pictureQueryRequest 图片查询条件
     * @param request             请求
     * @return 分页图片包装类列表
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                             HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 空间权限校验
        Long spaceId = pictureQueryRequest.getSpaceId();
        if (spaceId == null) {
            // 公开图库
            // 设置查询的条件,普通用户只能看到已经审核通过的图片，所以在这给他添加审核状态的查询条件
            pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            pictureQueryRequest.setNullSpaceId(true);
        } else {
            // 使用 Sa-Token 编程式权限校验
            boolean hasPermission = StpKit.SPACE.hasPermission(SpaceUserPermissionConstant.PICTURE_VIEW);
            ThrowUtils.throwIf(!hasPermission, ErrorCode.NO_AUTH_ERROR, "没有权限访问该图片");

//            // 私有空间，校验权限
//            User loginUser = userService.getLoginUser(request);
//            Space space = spaceService.getById(spaceId);
//            // 空间不存在
//            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
//            // 空间存在，仅空间的管理员可以访问
//            if (!loginUser.getId().equals(space.getUserId())) {
//                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间权限");
//            }
        }
        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        // 获取封装类
        return ResultUtils.success(pictureService.getPictureVOPage(picturePage, request));
    }

    /**
     * 分页获取图片列表（包装类 有缓存）
     * 私有空间其实不需要进行缓存 接口启用，后续如果需要做优化可以打开并添加相关权限校验
     *
     * @param pictureQueryRequest 图片查询条件
     * @param request             请求
     * @return 分页图片包装类列表
     */
    @Deprecated
    @PostMapping("/list/page/vo/cache")
    public BaseResponse<Page<PictureVO>> listPictureVOByPageWithCache(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                                      HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 设置查询的条件,普通用户只能看到已经审核通过的图片，所以在这给他添加审核状态的查询条件
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        // 查询数据库 之前可以查询缓存 如果没有缓存 则查询数据库并缓存
        // 构建缓存key
        String queryCondition = JSONUtil.toJsonStr(pictureQueryRequest);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        String cacheKey = String.format("yunpicture:listPictureVOByPage:%s", hashKey);
        // 1. 先从本地缓存中查询
        String cachedValue = LOCAL_CACHE.getIfPresent(cacheKey);
        if (cachedValue != null) {
            // 如果本地缓存命中 直接返回缓存结果
            Page<PictureVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return ResultUtils.success(cachedPage);
        }
        // 2. 本地缓存没有命中，查询 redis 分布式缓存 从缓存中获取数据
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        cachedValue = opsForValue.get(cacheKey);
        if (cachedValue != null) {
            // 如果缓存命中,需要更新本地缓存，并返回缓存结果
            LOCAL_CACHE.put(cacheKey, cachedValue);
            Page<PictureVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return ResultUtils.success(cachedPage);
        }
        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(picturePage, request);
        // 3. 更新缓存，本地缓存和 redis 分布式缓存
        // 将分页结果转换为json字符串
        String cacheValue = JSONUtil.toJsonStr(pictureVOPage);
        // 本地缓存
        LOCAL_CACHE.put(cacheKey, JSONUtil.toJsonStr(pictureVOPage));
        // 设置缓存过期时间 5 - 10 分钟过期 为了防止缓存雪崩 不能在同一时间将所有缓存过期 不然查询就直接打到数据库了
        // 五分钟加上随机数（五分钟） 防止缓存雪崩
        int cacheExpireTime = 300 + RandomUtil.randomInt(0, 300);
        opsForValue.set(cacheKey, cacheValue, cacheExpireTime, TimeUnit.SECONDS);
        // 获取封装类
        return ResultUtils.success(pictureVOPage);
    }

    /**
     * 编辑图片（给用户使用）
     *
     * @param pictureEditRequest 图片编辑信息
     * @param request            请求
     * @return 成功或失败
     */
    @PostMapping("/edit")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_EDIT)
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest,
                                             HttpServletRequest request) {
        if (pictureEditRequest == null || pictureEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        pictureService.editPicture(pictureEditRequest, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 获取预置标签和分类
     *
     * @return 图片标签分类
     */
    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }

    /**
     * 图片审核（管理员可用）
     *
     * @param pictureReviewRequest 图片审核信息
     * @param request              请求
     * @return 成功或失败
     */
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> doPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest,
                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        User loginUser = userService.getLoginUser(request);
        pictureService.doPictureReview(pictureReviewRequest, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 批量上传图片（管理员可用）
     *
     * @param pictureUploadByBatchRequest 图片批量上传信息
     * @param request                     请求
     * @return 成功的数量
     */
    @PostMapping("/upload/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> uploadPictureByBatch(@RequestBody PictureUploadByBatchRequest pictureUploadByBatchRequest,
                                                      HttpServletRequest request) {
        ThrowUtils.throwIf(pictureUploadByBatchRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        User loginUser = userService.getLoginUser(request);
        int uploadCount = pictureService.uploadPictureByBatch(pictureUploadByBatchRequest, loginUser);
        return ResultUtils.success(uploadCount);
    }

    /**
     * 以图搜图
     *
     * @param searchPictureByPictureRequest 图片搜图信息
     * @return 图片搜图结果
     */
    @PostMapping("/search/picture")
    public BaseResponse<List<ImageSearchResult>> searchPictureByPicture(@RequestBody SearchPictureByPictureRequest searchPictureByPictureRequest) {
        ThrowUtils.throwIf(searchPictureByPictureRequest == null, ErrorCode.PARAMS_ERROR);
        Long pictureId = searchPictureByPictureRequest.getPictureId();
        ThrowUtils.throwIf(pictureId == null || pictureId <= 0, ErrorCode.PARAMS_ERROR);
        Picture picture = pictureService.getById(pictureId);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        // 坑点：百度的以图搜图不支持webp格式的图片，所以这里我们可以使用 thumbnailUrl（是原图的格式也有可能会有webp格式）
        // 但是上传到cos的图片是原图经过压缩的，格式为webp格式， 这里要是需要兼容百度以图搜图的话可以改变原图的压缩策略
        // 或者专门生成png的图片格式用于检索 或者使用其他的图片搜索引擎
        List<ImageSearchResult> resultList = ImageSearchApiFacade.searchImage(picture.getThumbnailUrl());
        return ResultUtils.success(resultList);
    }

    /**
     * 以图片色调搜图
     *
     * @param searchPictureByColorRequest 颜色搜索图片的请求对象
     * @param request                     请求
     * @return 图片色调搜图结果
     */
    @PostMapping("/search/color")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_VIEW)
    public BaseResponse<List<PictureVO>> searchPictureByColor(@RequestBody SearchPictureByColorRequest searchPictureByColorRequest,
                                                              HttpServletRequest request) {
        ThrowUtils.throwIf(searchPictureByColorRequest == null, ErrorCode.PARAMS_ERROR);
        String picColor = searchPictureByColorRequest.getPicColor();
        Long spaceId = searchPictureByColorRequest.getSpaceId();
        User loginUser = userService.getLoginUser(request);
        List<PictureVO> result = pictureService.searchPictureByColor(spaceId, picColor, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 批量编辑图片信息
     *
     * @param pictureEditByBatchRequest 图片批量编辑信息
     * @param request                   请求
     * @return 成功或失败
     */
    @PostMapping("/edit/batch")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_EDIT)
    public BaseResponse<Boolean> editPictureByBatch(@RequestBody PictureEditByBatchRequest pictureEditByBatchRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureEditByBatchRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.editPictureByBatch(pictureEditByBatchRequest, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 创建扩图任务
     *
     * @param createPictureOutPaintingTaskRequest 创建扩图任务请求类
     * @param request                             请求
     * @return 扩图任务响应类
     */
    @PostMapping("/out_painting/create_task")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_EDIT)
    public BaseResponse<CreateOutPaintingTaskResponse> createPictureOutPaintingTask(@RequestBody CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, HttpServletRequest request) {
        if (createPictureOutPaintingTaskRequest == null || createPictureOutPaintingTaskRequest.getPictureId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        CreateOutPaintingTaskResponse response = pictureService.createPictureOutPaintingTask(createPictureOutPaintingTaskRequest, loginUser);
        return ResultUtils.success(response);
    }

    /**
     * 查询 AI 扩图任务
     */
    @GetMapping("/out_painting/get_task")
    public BaseResponse<GetOutPaintingTaskResponse> getPictureOutPaintingTask(String taskId) {
        ThrowUtils.throwIf(StrUtil.isBlank(taskId), ErrorCode.PARAMS_ERROR);
        GetOutPaintingTaskResponse task = aliYunAiApi.getOutPaintingTask(taskId);
        return ResultUtils.success(task);
    }
}
