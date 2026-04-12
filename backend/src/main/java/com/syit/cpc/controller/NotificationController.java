package com.syit.cpc.controller;

import com.syit.cpc.common.response.ApiResponse;
import com.syit.cpc.dto.response.NotificationListResponse;
import com.syit.cpc.security.SecurityUtils;
import com.syit.cpc.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 站内信通知控制器
 */
@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@Tag(name = "通知管理", description = "站内信通知相关接口")
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @Operation(summary = "获取通知列表")
    public ApiResponse<NotificationListResponse> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = securityUtils.getCurrentUserId();
        NotificationListResponse response = notificationService.getUserNotifications(userId, page, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "获取未读通知数量")
    public ApiResponse<Long> getUnreadCount() {
        Long userId = securityUtils.getCurrentUserId();
        Long count = notificationService.getUnreadCount(userId);
        return ApiResponse.success(count);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "标记通知为已读")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        notificationService.markAsRead(userId, id);
        return ApiResponse.success(null);
    }

    @PutMapping("/read-all")
    @Operation(summary = "全部标记为已读")
    public ApiResponse<Void> markAllAsRead() {
        Long userId = securityUtils.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知")
    public ApiResponse<Void> deleteNotification(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        notificationService.deleteNotification(userId, id);
        return ApiResponse.success(null);
    }
}
