package com.syit.cpc.service;

import com.syit.cpc.dto.response.NotificationListResponse;

/**
 * 站内信通知服务接口
 */
public interface NotificationService {

    /**
     * 发送转正通过通知
     *
     * @param userId 用户ID
     * @param applicationId 转正申请ID
     * @param examScore 考试分数
     * @param projectScore 项目分数
     * @param totalScore 总分
     * @param reviewComment 评审意见
     */
    void sendPromotionApprovedNotification(Long userId, Long applicationId, 
                                           Integer examScore, Integer projectScore, 
                                           Integer totalScore, String reviewComment);

    /**
     * 发送转正拒绝通知
     *
     * @param userId 用户ID
     * @param applicationId 转正申请ID
     * @param examScore 考试分数
     * @param projectScore 项目分数
     * @param totalScore 总分
     * @param reviewComment 评审意见
     */
    void sendPromotionRejectedNotification(Long userId, Long applicationId, 
                                           Integer examScore, Integer projectScore, 
                                           Integer totalScore, String reviewComment);

    /**
     * 获取用户通知列表（分页）
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页通知列表
     */
    NotificationListResponse getUserNotifications(Long userId, int page, int size);

    /**
     * 获取未读通知数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 标记通知为已读
     *
     * @param userId 用户ID
     * @param notificationId 通知ID
     */
    void markAsRead(Long userId, Long notificationId);

    /**
     * 全部标记为已读
     *
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);

    /**
     * 删除通知
     *
     * @param userId 用户ID
     * @param notificationId 通知ID
     */
    void deleteNotification(Long userId, Long notificationId);
}
