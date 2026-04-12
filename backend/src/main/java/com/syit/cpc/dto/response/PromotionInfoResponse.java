package com.syit.cpc.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 转正流程说明响应 DTO
 */
@Data
public class PromotionInfoResponse {

    /**
     * 流程标题
     */
    private String title;

    /**
     * 流程描述
     */
    private String description;

    /**
     * 步骤列表
     */
    private List<PromotionStep> steps;

    /**
     * 评分规则
     */
    private ScoringRules scoringRules;

    @Data
    public static class PromotionStep {
        /**
         * 步骤序号
         */
        private Integer step;

        /**
         * 步骤标题
         */
        private String title;

        /**
         * 步骤描述
         */
        private String description;

        /**
         * 预计时间
         */
        private String estimatedTime;
    }

    @Data
    public static class ScoringRules {
        /**
         * 答题权重 (0.6)
         */
        private Double examWeight;

        /**
         * 项目权重 (0.4)
         */
        private Double projectWeight;

        /**
         * 及格分数 (70)
         */
        private Integer passingScore;
    }
}
