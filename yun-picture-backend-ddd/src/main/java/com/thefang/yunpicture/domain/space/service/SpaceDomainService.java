package com.thefang.yunpicture.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thefang.yunpicture.domain.space.entity.Space;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.interfaces.dto.space.SpaceLevel;
import com.thefang.yunpicture.interfaces.dto.space.SpaceQueryRequest;

import java.util.List;

/**
 * @author Thefang
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2025-01-03 16:09:22
 */
public interface SpaceDomainService {


    /**
     * 查询对象转换成  QueryWrapper 查询条件
     *
     * @param spaceQueryRequest 空间查询请求
     * @return QueryWrapper 查询条件
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 根据空间等级填充空间对象
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     *
     * @param space 空间对象
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 校验空间权限
     *
     * @param user  登录用户对象
     * @param space 空间对象
     */
    void checkSpaceAuth(User user, Space space);

    /**
     * 获取空间等级列表
     *
     * @return 空间等级列表
     */
    List<SpaceLevel> listSpaceLevel();

}
