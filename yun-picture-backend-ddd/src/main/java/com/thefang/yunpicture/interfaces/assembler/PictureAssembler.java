package com.thefang.yunpicture.interfaces.assembler;

import cn.hutool.json.JSONUtil;
import com.thefang.yunpicture.domain.picture.entity.Picture;
import com.thefang.yunpicture.interfaces.dto.picture.PictureEditRequest;
import com.thefang.yunpicture.interfaces.dto.picture.PictureUpdateRequest;
import org.springframework.beans.BeanUtils;

/**
 * @Description 图片对象转换器
 * @Author Thefang
 * @Create 2025/1/23
 */
public class PictureAssembler {

    public static Picture toPictureEntity(PictureEditRequest request) {
        Picture picture = new Picture();
        BeanUtils.copyProperties(request, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(request.getTags()));
        return picture;
    }

    public static Picture toPictureEntity(PictureUpdateRequest request) {
        Picture picture = new Picture();
        BeanUtils.copyProperties(request, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(request.getTags()));
        return picture;
    }
}
