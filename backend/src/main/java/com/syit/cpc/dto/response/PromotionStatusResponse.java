package com.syit.cpc.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 转正状态响应 DTO
 */
@Data
public class PromotionStatusResponse {

    /**
     * 是否已有申请
     */
    private Boolean hasApplication;

    /**
     * 是否可以申请
     */
    private Boolean canApply;

    /**
     * 注册天数
     */
    private Long registrationDays;

    /**
     * 申请记录（如果有）
     */
    private ApplicationInfo application;

    @Data
    public static class ApplicationInfo {
        /**
         * 申请ID
         */
        private Long id;

        /**
         * 状态: pending_exam-待答题, exam_in_progress-答题中, pending_project-待提交项目,
         * project_reviewing-评审中, approved-已通过, rejected-已拒绝
         */
        private String status;

        /**
         * 创建时间
         */
        private LocalDateTime createdAt;

        /**
         * 考试分数
         */
        private Integer examScore;

        /**
         * 项目分数
         */
        private Integer projectScore;

        /**
         * 总分数
         */
        private Integer totalScore;

        /**
         * 审核意见
         */
        private String reviewComment;
    }
}
