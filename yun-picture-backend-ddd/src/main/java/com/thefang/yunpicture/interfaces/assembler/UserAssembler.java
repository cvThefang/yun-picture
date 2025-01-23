package com.thefang.yunpicture.interfaces.assembler;

import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.interfaces.dto.user.UserAddRequest;
import com.thefang.yunpicture.interfaces.dto.user.UserUpdateRequest;
import org.springframework.beans.BeanUtils;

/**
 * @Description 用户对象转换器
 * @Author Thefang
 * @Create 2025/1/23
 */
public class UserAssembler {

    public static User toUserEntity(UserAddRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        return user;
    }

    public static User toUserEntity(UserUpdateRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        return user;
    }
}
