package com.syit.cpc.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题库实体类
 * 对应数据库表: questions
 */
@Data
@TableName(value = "questions", autoResultMap = true)
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目内容
     */
    private String questionText;

    /**
     * 题型: single_choice-单选, multiple_choice-多选, true_false-判断, short_answer-简答
     */
    private String questionType;

    /**
     * 选项(JSON数组, 单选/多选时使用)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> options;

    /**
     * 正确答案（简答题时存储评分标准）
     */
    private String correctAnswer;

    /**
     * 分值
     */
    private Integer score;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
