package com.thefang.yunpicture.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thefang.yunpicture.domain.space.entity.SpaceUser;
import com.thefang.yunpicture.interfaces.dto.spaceuser.SpaceUserQueryRequest;

/**
 * @author Thefang
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service
 * @createDate 2025-01-20 15:18:25
 */
public interface SpaceUserDomainService {

    /**
     * 获取查询对象
     *
     * @param spaceUserQueryRequest 查询请求对象
     * @return 查询对象
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

}
