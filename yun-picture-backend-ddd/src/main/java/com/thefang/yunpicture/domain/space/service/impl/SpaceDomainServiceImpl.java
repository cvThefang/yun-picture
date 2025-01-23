package com.thefang.yunpicture.domain.space.service.impl;


import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thefang.yunpicture.domain.space.entity.Space;
import com.thefang.yunpicture.domain.space.repository.SpaceRepository;
import com.thefang.yunpicture.domain.space.service.SpaceDomainService;
import com.thefang.yunpicture.domain.space.valueobject.SpaceLevelEnum;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.infrastructure.exception.BusinessException;
import com.thefang.yunpicture.infrastructure.exception.ErrorCode;
import com.thefang.yunpicture.interfaces.dto.space.SpaceLevel;
import com.thefang.yunpicture.interfaces.dto.space.SpaceQueryRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Thefang
 * @description 针对表【space(空间)】的数据库操作Service实现
 * @createDate 2025-01-03 16:09:22
 */
@Service
public class SpaceDomainServiceImpl implements SpaceDomainService {

    @Resource
    private SpaceRepository spaceRepository;

    /**
     * 查询对象转换成  QueryWrapper 查询条件
     *
     * @param spaceQueryRequest 空间查询请求
     * @return QueryWrapper 查询条件
     */
    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        Integer spaceType = spaceQueryRequest.getSpaceType();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel), "spaceLevel", spaceLevel);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceType), "spaceType", spaceType);
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 根据空间级别，填充空间对象的容量和最大条数
     * 管理员在创建空间时，可以设置空间的容量最大值和最大条数，如果没有设置，则使用空间类别的默认值
     *
     * @param space 空间对象
     */
    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            // 只有在管理员没有设置空间的容量最大值时，才会设置默认值
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            // 只有在管理员没有设置空间的最大条数时，才会设置默认值
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }

    /**
     * 校验空间权限
     *
     * @param user  登录用户对象
     * @param space 空间对象
     */
    @Override
    public void checkSpaceAuth(User user, Space space) {
        // 仅本人或管理员可编辑
        if (!space.getUserId().equals(user.getId()) && !user.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "空间权限不足");
        }
    }

    /**
     * 列出空间级别
     *
     * @return 空间级别列表
     */
    @Override
    public List<SpaceLevel> listSpaceLevel() {
        return Arrays.stream(SpaceLevelEnum.values())
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()
                ))
                .collect(Collectors.toList());
    }
}




