package com.syit.cpc.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 转正评审列表响应
 */
@Data
public class PromotionReviewListResponse {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 评审列表
     */
    private List<ReviewItem> records;

    @Data
    public static class ReviewItem {
        /**
         * 申请ID
         */
        private Long applicationId;

        /**
         * 申请人姓名
         */
        private String applicantName;

        /**
         * 项目名称
         */
        private String projectName;

        /**
         * 项目描述
         */
        private String projectDescription;

        /**
         * 项目链接
         */
        private String projectUrl;

        /**
         * 考试分数
         */
        private Integer examScore;

        /**
         * 提交时间
         */
        private LocalDateTime submittedAt;

        /**
         * 状态
         */
        private String status;
    }
}
