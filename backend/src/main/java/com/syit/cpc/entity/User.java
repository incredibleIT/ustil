package com.syit.cpc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表: users
 */
@Data
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 角色: ROLE_PROBATION-见习成员, ROLE_MEMBER-正式成员, ROLE_ADMIN-管理员
     */
    private String role;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;

    /**
     * 登录失败次数
     */
    private Integer loginFailCount;

    /**
     * 锁定截止时间
     */
    private LocalDateTime lockUntil;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

