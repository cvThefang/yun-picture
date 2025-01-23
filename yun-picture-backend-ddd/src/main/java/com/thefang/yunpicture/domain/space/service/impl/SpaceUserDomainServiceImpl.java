package com.thefang.yunpicture.domain.space.service.impl;


import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thefang.yunpicture.domain.space.entity.SpaceUser;
import com.thefang.yunpicture.domain.space.service.SpaceUserDomainService;
import com.thefang.yunpicture.interfaces.dto.spaceuser.SpaceUserQueryRequest;
import org.springframework.stereotype.Service;


/**
 * @author Thefang
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service实现
 * @createDate 2025-01-20 15:18:25
 */
@Service
public class SpaceUserDomainServiceImpl implements SpaceUserDomainService {

    /**
     * 获取查询对象
     *
     * @param spaceUserQueryRequest 查询请求对象
     * @return 查询对象
     */
    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if (spaceUserQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceUserQueryRequest.getId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        String spaceRole = spaceUserQueryRequest.getSpaceRole();
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceRole), "spaceRole", spaceRole);
        return queryWrapper;
    }
}




