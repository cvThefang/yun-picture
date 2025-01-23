package com.thefang.yunpicturebackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * @Description 用户角色枚举类
 * @Author Thefang
 * @Create 2024/12/11
 */
@Getter
public enum UserRoleEnum {

    USER("普通用户", "user"),
    ADMIN("管理员", "admin");

    /**
     * 角色的文本描述，用于展示给用户看的角色名称。
     */
    private final String text;

    /**
     * 角色的唯一标识符，通常用于内部逻辑判断或与外部系统交互时的角色标识。
     */
    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举对象
     *
     * @param value value 值
     * @return 枚举对象
     * tips: 优化技巧：如果枚举类非常多 用for循环遍历查找的时间会随着枚举类的数量的增加而增加，
     * 可以考虑存到Map中，逻辑：从顺序查找变为直接查找。
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.getValue().equals(value)) {
                return userRoleEnum;
            }
        }
        return null;
    }

}
