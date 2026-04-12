package com.syit.cpc.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 提交试卷请求 DTO
 */
@Data
public class SubmitExamRequest {

    /**
     * 试卷ID
     */
    @NotNull(message = "试卷ID不能为空")
    private Long paperId;

    /**
     * 考试开始时间（前端传入）
     */
    @NotNull(message = "考试开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 答案列表
     */
    @NotEmpty(message = "答案列表不能为空")
    @Valid
    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        /**
         * 题目ID
         */
        @NotNull(message = "题目ID不能为空")
        private Long questionId;

        /**
         * 用户答案（单选/判断为单个字母，多选题为排序后的字母串，未作答为空字符串）
         */
        private String answer;
    }
}
