package com.syit.cpc.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目响应 DTO
 */
@Data
public class QuestionResponse {

    private Long id;

    /**
     * 题目内容
     */
    private String questionText;

    /**
     * 题型
     */
    private String questionType;

    /**
     * 选项列表
     */
    private List<String> options;

    /**
     * 正确答案（仅管理员可见）
     */
    private String correctAnswer;

    /**
     * 分值
     */
    private Integer score;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
