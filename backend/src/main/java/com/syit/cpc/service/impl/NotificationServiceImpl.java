package com.syit.cpc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syit.cpc.common.constant.NotificationType;
import com.syit.cpc.dto.response.NotificationItemResponse;
import com.syit.cpc.dto.response.NotificationListResponse;
import com.syit.cpc.entity.Notification;
import com.syit.cpc.mapper.NotificationMapper;
import com.syit.cpc.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 站内信通知服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendPromotionApprovedNotification(Long userId, Long applicationId,
                                                   Integer examScore, Integer projectScore,
                                                   Integer totalScore, String reviewComment) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(NotificationType.PROMOTION_APPROVED);
        notification.setTitle("恭喜转正！");
        
        StringBuilder content = new StringBuilder();
        content.append("恭喜您通过转正考核！\n");
        content.append("考试分数：").append(examScore).append(" 分\n");
        content.append("项目分数：").append(projectScore).append(" 分\n");
        content.append("总分：").append(totalScore).append(" 分\n");
        if (reviewComment != null && !reviewComment.isEmpty()) {
            content.append("评审意见：").append(reviewComment).append("\n\n");
        }
        content.append("您已成为正式成员，欢迎加入！");
        
        notification.setContent(content.toString());
        notification.setIsRead(0);
        notification.setRelatedId(applicationId);
        
        notificationMapper.insert(notification);
        log.info("发送转正通过通知: userId={}, applicationId={}", userId, applicationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendPromotionRejectedNotification(Long userId, Long applicationId,
                                                   Integer examScore, Integer projectScore,
                                                   Integer totalScore, String reviewComment) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(NotificationType.PROMOTION_REJECTED);
        notification.setTitle("转正申请未通过");
        
        StringBuilder content = new StringBuilder();
        content.append("很遗憾，您的转正申请未通过。\n");
        content.append("考试分数：").append(examScore).append(" 分\n");
        content.append("项目分数：").append(projectScore).append(" 分\n");
        content.append("总分：").append(totalScore).append(" 分\n");
        if (reviewComment != null && !reviewComment.isEmpty()) {
            content.append("评审意见：").append(reviewComment).append("\n\n");
        }
        content.append("您可以完善项目后重新申请转正。");
        
        notification.setContent(content.toString());
        notification.setIsRead(0);
        notification.setRelatedId(applicationId);
        
        notificationMapper.insert(notification);
        log.info("发送转正拒绝通知: userId={}, applicationId={}", userId, applicationId);
    }

    @Override
    public NotificationListResponse getUserNotifications(Long userId, int page, int size) {
        // 1. 构建查询条件
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .orderByDesc(Notification::getCreatedAt);

        // 2. 分页查询
        Page<Notification> pageParam = new Page<>(page, size);
        Page<Notification> resultPage = notificationMapper.selectPage(pageParam, wrapper);

        // 3. 转换为响应对象
        NotificationListResponse response = new NotificationListResponse();
        response.setTotal(resultPage.getTotal());
        response.setPages(resultPage.getPages());
        response.setCurrent(resultPage.getCurrent());
        response.setSize(resultPage.getSize());

        List<NotificationItemResponse> records = resultPage.getRecords().stream()
            .map(notification -> {
                NotificationItemResponse item = new NotificationItemResponse();
                BeanUtils.copyProperties(notification, item);
                return item;
            })
            .collect(Collectors.toList());

        response.setRecords(records);
        return response;
    }

    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getIsRead, 0);
        return notificationMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            log.warn("通知不存在: notificationId={}", notificationId);
            return;
        }

        // 验证通知属于当前用户
        if (!notification.getUserId().equals(userId)) {
            log.warn("无权操作此通知: userId={}, notificationId={}", userId, notificationId);
            return;
        }

        notification.setIsRead(1);
        notification.setReadAt(LocalDateTime.now());
        notificationMapper.updateById(notification);
        
        log.info("标记通知为已读: userId={}, notificationId={}", userId, notificationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getUserId, userId)
                    .eq(Notification::getIsRead, 0)
                    .set(Notification::getIsRead, 1)
                    .set(Notification::getReadAt, LocalDateTime.now());

        int updatedCount = notificationMapper.update(null, updateWrapper);
        
        if (updatedCount > 0) {
            log.info("全部标记为已读: userId={}, count={}", userId, updatedCount);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            log.warn("通知不存在: notificationId={}", notificationId);
            return;
        }

        // 验证通知属于当前用户
        if (!notification.getUserId().equals(userId)) {
            log.warn("无权删除此通知: userId={}, notificationId={}", userId, notificationId);
            return;
        }

        notificationMapper.deleteById(notificationId);
        log.info("删除通知: userId={}, notificationId={}", userId, notificationId);
    }
}
