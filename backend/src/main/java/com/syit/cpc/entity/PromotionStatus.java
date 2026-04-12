package com.syit.cpc.entity;

import lombok.Getter;

/**
 * 转正申请状态枚举
 */
@Getter
public enum PromotionStatus {
    
    /**
     * 待答题
     */
    PENDING_EXAM("pending_exam", "待答题"),
    
    /**
     * 答题中
     */
    EXAM_IN_PROGRESS("exam_in_progress", "答题中"),
    
    /**
     * 答题未通过
     */
    EXAM_FAILED("exam_failed", "答题未通过"),
    
    /**
     * 待提交项目
     */
    PENDING_PROJECT("pending_project", "待提交项目"),
    
    /**
     * 评审中
     */
    PROJECT_REVIEWING("project_reviewing", "评审中"),
    
    /**
     * 已通过
     */
    APPROVED("approved", "已通过"),
    
    /**
     * 已拒绝
     */
    REJECTED("rejected", "已拒绝");
    
    private final String code;
    private final String description;
    
    PromotionStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根据 code 获取枚举
     *
     * @param code 状态码
     * @return 对应的枚举值，如果不存在则返回 null
     */
    public static PromotionStatus fromCode(String code) {
        for (PromotionStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
