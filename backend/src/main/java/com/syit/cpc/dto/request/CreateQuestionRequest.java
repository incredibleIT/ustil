package com.syit.cpc.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * 创建题目请求 DTO
 */
@Data
public class CreateQuestionRequest {

    /**
     * 题目内容
     */
    @NotBlank(message = "题目内容不能为空")
    @Size(max = 2000, message = "题目内容不能超过2000字")
    private String questionText;

    /**
     * 题型: single_choice/multiple_choice/true_false/short_answer
     */
    @NotBlank(message = "题型不能为空")
    @Pattern(regexp = "^(single_choice|multiple_choice|true_false|short_answer)$", message = "题型无效")
    private String questionType;

    /**
     * 选项列表（单选/多选/判断题需要）
     */
    private List<String> options;

    /**
     * 正确答案
     */
    private String correctAnswer;

    /**
     * 分值
     */
    @NotNull(message = "分值不能为空")
    @Min(value = 1, message = "分值至少为1")
    @Max(value = 100, message = "分值不能超过100")
    private Integer score;
}
