package com.thefang.yunpicture.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.thefang.yunpicture.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.thefang.yunpicture.interfaces.dto.spaceuser.SpaceUserQueryRequest;
import com.thefang.yunpicture.domain.space.entity.SpaceUser;
import com.thefang.yunpicture.interfaces.vo.space.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Thefang
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service
 * @createDate 2025-01-20 15:18:25
 */
public interface SpaceUserApplicationService extends IService<SpaceUser> {

    /**
     * 创建空间成员
     *
     * @param spaceUserAddRequest 添加空间成员请求对象
     * @return 创建的空间成员 ID
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 校验空间成员
     *
     * @param spaceUser 空间成员对象
     * @param add       是否为创建时检验
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 获取空间成员包装类（单条）
     *
     * @param spaceUser 空间成员对象
     * @param request   请求对象
     * @return 空间成员包装类
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 获取空间成员包装类（列表）
     *
     * @param spaceUserList 空间成员对象列表
     * @return 空间成员包装类列表
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);

    /**
     * 获取查询对象
     *
     * @param spaceUserQueryRequest 查询请求对象
     * @return 查询对象
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

}
