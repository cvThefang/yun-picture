package com.thefang.yunpicture.interfaces.assembler;

import com.thefang.yunpicture.domain.space.entity.SpaceUser;
import com.thefang.yunpicture.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.thefang.yunpicture.interfaces.dto.spaceuser.SpaceUserEditRequest;
import org.springframework.beans.BeanUtils;

/**
 * @Description 空间用户对象转换器
 * @Author Thefang
 * @Create 2025/1/23
 */
public class SpaceUserAssembler {

    public static SpaceUser toSpaceUserEntity(SpaceUserAddRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }

    public static SpaceUser toSpaceUserEntity(SpaceUserEditRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }
}
