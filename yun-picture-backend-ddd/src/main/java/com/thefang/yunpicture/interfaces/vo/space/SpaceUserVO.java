package com.thefang.yunpicture.interfaces.vo.space;

import com.thefang.yunpicture.interfaces.vo.user.UserVO;
import com.thefang.yunpicture.domain.space.entity.SpaceUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 空间用户的视图包装类
 * @Author Thefang
 * @Create 2025/1/20
 */
@Data
public class SpaceUserVO implements Serializable {

    private static final long serialVersionUID = 1318853561246207301L;

    /**
     * id
     */
    private Long id;

    /**
     * 空间 id
     */
    private Long spaceId;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户信息
     */
    private UserVO user;

    /**
     * 空间信息
     */
    private SpaceVO space;

    /**
     * 封装类转对象
     *
     * @param spaceUserVO 空间用户的视图包装类
     * @return 空间用户关联类
     */
    public static SpaceUser voToObj(SpaceUserVO spaceUserVO) {
        if (spaceUserVO == null) {
            return null;
        }
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserVO, spaceUser);
        return spaceUser;
    }

    /**
     * 对象转封装类
     *
     * @param spaceUser 空间用户关联类
     * @return 空间用户的视图包装类
     */
    public static SpaceUserVO objToVo(SpaceUser spaceUser) {
        if (spaceUser == null) {
            return null;
        }
        SpaceUserVO spaceUserVO = new SpaceUserVO();
        BeanUtils.copyProperties(spaceUser, spaceUserVO);
        return spaceUserVO;
    }
}

