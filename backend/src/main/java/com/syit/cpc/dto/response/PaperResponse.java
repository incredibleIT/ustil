package com.syit.cpc.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 试卷响应 DTO（不包含正确答案）
 */
@Data
public class PaperResponse {

    /**
     * 试卷ID（使用时间戳生成）
     */
    private Long paperId;

    /**
     * 题目列表（不包含正确答案）
     */
    private List<QuestionItem> questions;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 考试时长（分钟）
     */
    private Integer duration;

    @Data
    public static class QuestionItem {
        private Long id;
        private String questionText;
        private String questionType;
        private List<String> options;
        private Integer score;
    }
}
