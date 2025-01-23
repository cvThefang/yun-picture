package com.thefang.yunpicture.domain.space.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.thefang.yunpicture.domain.space.valueobject.SpaceLevelEnum;
import com.thefang.yunpicture.domain.space.valueobject.SpaceTypeEnum;
import com.thefang.yunpicture.infrastructure.exception.BusinessException;
import com.thefang.yunpicture.infrastructure.exception.ErrorCode;
import com.thefang.yunpicture.infrastructure.exception.ThrowUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 空间
 *
 * @TableName space
 */
@TableName(value = "space")
@Data
public class Space implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间类型：0-私有 1-团队
     */
    private Integer spaceType;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;

    /**
     * 当前空间下图片的总大小
     */
    private Long totalSize;

    /**
     * 当前空间下的图片数量
     */
    private Long totalCount;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 校验空间
     */
    public void validSpace(boolean add){
        ThrowUtils.throwIf(this == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String spaceName = this.getSpaceName();
        Integer spaceLevel = this.getSpaceLevel();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        Integer spaceType = this.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(spaceType);
        // 要创建数据时，校验必填项 空间级别校验 空间类型校验
        if (add) {
            if (StrUtil.isBlank(spaceName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            }
            if (spaceLevel == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不能为空");
            }
            if (spaceType == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间类型不能为空");
            }
        }
        // 修改数据时，对空间级别进行校验
        if (spaceLevel != null && spaceLevelEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不存在");
        }
        // 校验空间名称长度不能超过30
        if (StrUtil.isNotBlank(spaceName) && spaceName.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }
        // 修改数据时，对空间类别进行校验
        if (spaceType != null && spaceTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间类别不存在");
        }
    }
}