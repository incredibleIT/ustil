package com.syit.cpc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审核投票实体类
 * 对应数据库表: review_votes
 */
@Data
@TableName("review_votes")
public class ReviewVote {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 投票用户ID
     */
    private Long userId;

    /**
     * 投票: approve-通过, reject-拒绝
     */
    private String vote;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
