package com.syit.cpc.common.exception;

import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
public enum ErrorCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 系统内部错误
     */
    INTERNAL_ERROR(500, "系统内部错误"),

    /**
     * 业务错误
     */
    BUSINESS_ERROR(1000, "业务错误"),

    /**
     * 用户已存在
     */
    USER_ALREADY_EXISTS(1001, "用户已存在"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1002, "用户不存在"),

    /**
     * 密码错误
     */
    PASSWORD_ERROR(1003, "密码错误"),

    /**
     * 账号已禁用
     */
    ACCOUNT_DISABLED(1004, "账号已禁用"),

    /**
     * 邮箱已存在
     */
    EMAIL_ALREADY_EXISTS(1005, "该邮箱已被注册"),

    /**
     * 学号已存在
     */
    STUDENT_NO_ALREADY_EXISTS(1006, "该学号已被注册"),

    /**
     * 密码不匹配
     */
    PASSWORD_NOT_MATCH(1007, "两次输入的密码不一致"),

    /**
     * 登录失败（邮箱或密码错误）
     */
    LOGIN_FAILED(1008, "邮箱或密码错误"),

    /**
     * 账号已锁定
     */
    ACCOUNT_LOCKED(1009, "账号已锁定，请在15分钟后重试"),

    /**
     * 账号未激活
     */
    ACCOUNT_NOT_ACTIVATED(1010, "账号未激活，请联系管理员"),

    /**
     * Refresh Token 无效或已过期
     */
    REFRESH_TOKEN_INVALID(1011, "Refresh Token无效或已过期"),

    /**
     * 旧密码错误
     */
    OLD_PASSWORD_ERROR(1012, "旧密码错误"),

    /**
     * 两次密码不一致
     */
    PASSWORD_NOT_MATCH_CONFIRM(1013, "两次输入的密码不一致"),

    /**
     * 无效的角色值
     */
    INVALID_ROLE(1014, "无效的角色值"),

    /**
     * 用户状态更新失败
     */
    USER_STATUS_UPDATE_FAILED(1015, "用户状态更新失败"),

    /**
     * 转正申请相关
     */
    REGISTRATION_TOO_RECENT(2001, "注册时间不足30天，还需等待X天"),
    PROMOTION_APPLICATION_EXISTS(2002, "已有未完成的转正申请，请完成或联系负责人"),
    PROMOTION_APPLICATION_NOT_FOUND(2003, "转正申请不存在"),
    PROMOTION_STATUS_INVALID(2004, "转正状态无效"),
    PROJECT_ALREADY_SUBMITTED(2005, "项目已提交，不可重复提交"),
    INVALID_PROJECT_SUBMISSION_STATUS(2006, "当前状态不允许提交项目，请先完成答题"),
    PROJECT_NOT_PENDING_REVIEW(2007, "该项目不在评审状态"),

    /**
     * 题库管理相关
     */
    QUESTION_NOT_FOUND(3001, "题目不存在"),
    QUESTION_TYPE_INVALID(3002, "题型无效"),
    ANSWER_FORMAT_INVALID(3003, "答案格式错误"),
    OPTIONS_COUNT_INVALID(3004, "选项数量不符合要求"),
    INSUFFICIENT_QUESTIONS(3005, "题库题目不足，无法生成试卷");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
