package com.thefang.yunpicturebackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.thefang.yunpicturebackend.exception.ErrorCode;
import com.thefang.yunpicturebackend.exception.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @Description 文件图片上传
 * @Author Thefang
 * @Create 2024/12/25
 */
@Service
public class FilePictureUpload extends PictureUploadTemplate {
    @Override
    protected void validPicture(Object inputSource) {
        // 类型强转
        MultipartFile multipartFile = (MultipartFile) inputSource;
        // 图片不能为空
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "图片不能为空");
        // 文件存在
        // 1. 校验文件大小
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024 * 1024;
        ThrowUtils.throwIf(fileSize > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "图片大小不能超过2M");
        // 2. 校验文件格式（后缀）
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // 允许上传的文件后缀格式列表
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpg", "jpeg", "png", "webp");
        // 判断文件后缀是否在允许上传的列表中
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "图片格式不支持");
    }

    @Override
    protected String getOriginFilename(Object inputSource) {
        // 类型强转
        MultipartFile multipartFile = (MultipartFile) inputSource;
        // 返回文件名
        return multipartFile.getOriginalFilename();
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        // 类型强转
        MultipartFile multipartFile = (MultipartFile) inputSource;
        // 保存文件到临时文件
        multipartFile.transferTo(file);
    }
}
