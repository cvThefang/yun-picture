package com.thefang.yunpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.thefang.yunpicturebackend.model.dto.space.SpaceAddRequest;
import com.thefang.yunpicturebackend.model.dto.space.SpaceQueryRequest;
import com.thefang.yunpicturebackend.model.entity.Space;
import com.thefang.yunpicturebackend.model.entity.User;
import com.thefang.yunpicturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Thefang
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2025-01-03 16:09:22
 */
public interface SpaceService extends IService<Space> {

    /**
     * 创建空间
     *
     * @param spaceAddRequest 创建空间请求对象
     * @param loginUser       登录用户对象
     * @return 创建的空间 id
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 校验空间对象
     *
     * @param space 空间对象
     */
    void validSpace(Space space, boolean add);

    /**
     * 获取空间包装类（单条）
     *
     * @param space   空间对象
     * @param request 请求对象
     * @return 空间包装类对象
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取空间包装类（分页）
     *
     * @param spacePage 空间分页对象
     * @param request   请求对象
     * @return 空间包装类分页对象
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

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
    public void fillSpaceBySpaceLevel(Space space);

    /**
     * 校验空间权限
     *
     * @param user  登录用户对象
     * @param space 空间对象
     */
    void checkSpaceAuth(User user, Space space);

}
