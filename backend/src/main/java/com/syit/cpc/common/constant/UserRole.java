package com.syit.cpc.common.constant;

import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {
    
    /**
     * 预备成员
     */
    ROLE_PROBATION("ROLE_PROBATION", "预备成员"),
    
    /**
     * 正式成员
     */
    ROLE_MEMBER("ROLE_MEMBER", "正式成员"),
    
    /**
     * 负责人
     */
    ROLE_ADMIN("ROLE_ADMIN", "负责人");
    
    private final String code;
    private final String name;
    
    UserRole(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    /**
     * 根据 code 获取角色枚举
     *
     * @param code 角色代码
     * @return 角色枚举，如果不存在则返回 null
     */
    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
    
    /**
     * 验证角色代码是否有效
     *
     * @param code 角色代码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}
