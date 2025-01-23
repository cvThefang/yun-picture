package com.thefang.yunpicture.interfaces.assembler;

import com.thefang.yunpicture.domain.space.entity.Space;
import com.thefang.yunpicture.interfaces.dto.space.SpaceAddRequest;
import com.thefang.yunpicture.interfaces.dto.space.SpaceEditRequest;
import com.thefang.yunpicture.interfaces.dto.space.SpaceUpdateRequest;
import org.springframework.beans.BeanUtils;

/**
 * @Description 空间对象转换器
 * @Author Thefang
 * @Create 2025/1/23
 */
public class SpaceAssembler {

    public static Space toSpaceEntity(SpaceAddRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceUpdateRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceEditRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }
}
