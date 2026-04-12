package com.syit.cpc.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 考试记录实体类
 * 对应数据库表: exam_records
 */
@Data
@TableName(value = "exam_records", autoResultMap = true)
public class ExamRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 试卷ID
     */
    private Long paperId;

    /**
     * 用户答案（JSON格式: {questionId: answer}）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> answers;

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
     * 考试次数
     */
    private Integer attemptCount;

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

