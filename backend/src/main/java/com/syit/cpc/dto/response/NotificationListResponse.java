package com.syit.cpc.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知列表响应
 */
@Data
public class NotificationListResponse {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 通知列表
     */
    private List<NotificationItemResponse> records;
}
