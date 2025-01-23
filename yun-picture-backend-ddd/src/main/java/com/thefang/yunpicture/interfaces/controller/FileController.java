package com.thefang.yunpicture.interfaces.controller;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import com.thefang.yunpicture.infrastructure.annotation.AuthCheck;
import com.thefang.yunpicture.infrastructure.common.BaseResponse;
import com.thefang.yunpicture.infrastructure.common.ResultUtils;
import com.thefang.yunpicture.domain.user.constant.UserConstant;
import com.thefang.yunpicture.infrastructure.exception.BusinessException;
import com.thefang.yunpicture.infrastructure.exception.ErrorCode;
import com.thefang.yunpicture.infrastructure.api.CosManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @Description 文件上传下载接口
 * @Author Thefang
 * @Create 2024/12/15
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private CosManager cosManager;

    /**
     * 上传文件接口
     *
     * @param multipartFile 上传的文件
     * @return 上传成功的响应
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        // 当前用户上传的文件名
        String fileName = multipartFile.getOriginalFilename();
        // 上传文件到对象存储的路径
        String filePath = String.format("/test/%s", fileName);
        // 将multipartFile转换成file对象
        File file = null;
        try {
            // 上传文件
            // 创建临时文件
            file = File.createTempFile(filePath, null);
            // 将multipartFile写入临时文件
            multipartFile.transferTo(file);
            // 上传文件到对象存储
            cosManager.putObject(filePath, file);
            return ResultUtils.success(filePath);
        } catch (Exception e) {
            log.error("file upload error, filePath: {}", filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            // 删除临时文件
            if (file != null) {
                boolean deleteFlag = file.delete();
                if (!deleteFlag) {
                    log.error("file delete error, filePath: {}", filePath);
                }
            }
        }
    }

    /**
     * 测试文件下载
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            // 下载文件
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 将文件流转换成字节数组
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头 浏览器通过响应头判断是查看文件还是下载文件
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入文件到响应体
            response.getOutputStream().write(bytes);
            // 刷新缓冲区
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            // 释放流
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }
}
