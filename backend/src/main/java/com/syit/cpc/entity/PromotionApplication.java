package com.syit.cpc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 转正申请实体类
 * 对应数据库表: promotion_applications
 */
@Data
@TableName("promotion_applications")
public class PromotionApplication {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 申请人ID
     */
    private Long userId;

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
     * 项目评分(0-100)
     */
    private Integer projectScore;

    /**
     * 考试分数(0-100)
     */
    private Integer examScore;

    /**
     * 总分数(0-100)
     */
    private Integer totalScore;

    /**
     * 状态: pending-待审核, approved-已通过, rejected-已拒绝
     */
    private String status;

    /**
     * 审核人ID
     */
    private Long reviewedBy;

    /**
     * 审核意见
     */
    private String reviewComment;

    /**
     * 评审完成时间
     */
    private LocalDateTime reviewedAt;

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
