package com.syit.cpc.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知项响应
 */
@Data
public class NotificationItemResponse {

    /**
     * 通知ID
     */
    private Long id;

    /**
     * 通知类型
     */
    private String type;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 是否已读: 0-未读, 1-已读
     */
    private Integer isRead;

    /**
     * 关联业务ID
     */
    private Long relatedId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 阅读时间
     */
    private LocalDateTime readAt;
}
