package com.syit.cpc.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试结果响应 DTO
 */
@Data
public class ExamResultResponse {

    /**
     * 考试记录ID
     */
    private Long examRecordId;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 满分
     */
    private Integer maxScore;

    /**
     * 是否通过
     */
    private Boolean passed;

    /**
     * 单选题得分
     */
    private Integer singleChoiceScore;

    /**
     * 多选题得分
     */
    private Integer multipleChoiceScore;

    /**
     * 判断题得分
     */
    private Integer trueFalseScore;

    /**
     * 考试次数
     */
    private Integer attemptCount;

    /**
     * 开始时间
     */
    private LocalDateTime startedAt;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 用时（分钟）
     */
    private Integer duration;

    /**
     * 答题详情
     */
    private List<ExamDetailItem> details;

    @Data
    public static class ExamDetailItem {
        /**
         * 题目ID
         */
        private Long questionId;

        /**
         * 题目内容
         */
        private String questionText;

        /**
         * 题型
         */
        private String questionType;

        /**
         * 用户答案
         */
        private String userAnswer;

        /**
         * 正确答案
         */
        private String correctAnswer;

        /**
         * 是否正确
         */
        private Boolean correct;

        /**
         * 分值
         */
        private Integer score;
    }
}
