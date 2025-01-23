package com.thefang.yunpicturebackend.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.thefang.yunpicturebackend.manager.auth.model.SpaceUserAuthConfig;
import com.thefang.yunpicturebackend.manager.auth.model.SpaceUserPermissionConstant;
import com.thefang.yunpicturebackend.manager.auth.model.SpaceUserRole;
import com.thefang.yunpicture.domain.space.entity.Space;
import com.thefang.yunpicture.domain.space.entity.SpaceUser;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.domain.space.valueobject.SpaceRoleEnum;
import com.thefang.yunpicture.domain.space.valueobject.SpaceTypeEnum;
import com.thefang.yunpicture.application.service.SpaceUserApplicationService;
import com.thefang.yunpicture.application.service.UserApplicationService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description 空间成员权限管理
 * @Author Thefang
 * @Create 2025/1/20
 */
@Component
public class SpaceUserAuthManager {

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private SpaceUserApplicationService spaceUserApplicationService;

    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    // 静态代码块初始化权限配置
    static {
        // 使用hutool工具类 读取配置文件
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        // 使用hutool工具类 将json字符串转换为SpaceUserAuthConfig对象
        // 注意：这里 SPACE_USER_AUTH_CONFIG 定义的是final 如果权限配置文件发生变化，需要重启项目
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * 根据成员角色获取权限列表
     *
     * @param spaceUserRole 成员角色
     * @return 权限列表
     */
    public List<String> getPermissionsByRole(String spaceUserRole) {
        // 校验参数
        if (StrUtil.isBlank(spaceUserRole)) {
            return new ArrayList<>();
        }
        SpaceUserRole role = SPACE_USER_AUTH_CONFIG.getRoles()
                .stream()
                .filter( r-> r.getKey().equals(spaceUserRole))
                .findFirst()
                .orElse(null);
        if (role == null) {
            return new ArrayList<>();
        }
        return role.getPermissions();
    }

    /**
     * 获取权限列表
     *
     * @param space 空间
     * @param loginUser 登录用户
     * @return 权限列表
     */
    public List<String> getPermissionList(Space space, User loginUser) {
        if (loginUser == null) {
            return new ArrayList<>();
        }
        // 管理员权限
        List<String> ADMIN_PERMISSIONS = getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // 公共图库
        if (space == null) {
            if (loginUser.isAdmin()) {
                return ADMIN_PERMISSIONS;
            }
            return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
        }
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(space.getSpaceType());
        if (spaceTypeEnum == null) {
            return new ArrayList<>();
        }
        // 根据空间获取对应的权限
        switch (spaceTypeEnum) {
            case PRIVATE:
                // 私有空间，仅本人或管理员有所有权限
                if (space.getUserId().equals(loginUser.getId()) || loginUser.isAdmin()) {
                    return ADMIN_PERMISSIONS;
                } else {
                    return new ArrayList<>();
                }
            case TEAM:
                // 团队空间，查询 SpaceUser 并获取角色和权限
                SpaceUser spaceUser = spaceUserApplicationService.lambdaQuery()
                        .eq(SpaceUser::getSpaceId, space.getId())
                        .eq(SpaceUser::getUserId, loginUser.getId())
                        .one();
                if (spaceUser == null) {
                    return new ArrayList<>();
                } else {
                    return getPermissionsByRole(spaceUser.getSpaceRole());
                }
        }
        return new ArrayList<>();
    }

}
