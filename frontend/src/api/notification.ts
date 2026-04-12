import request from '@/api/request'
import type { NotificationListResponse } from '@/types/notification'

/**
 * 获取通知列表
 */
export function getNotifications(page: number, size: number) {
  return request.get<NotificationListResponse>('/notifications', {
    params: { page, size },
  })
}

/**
 * 获取未读通知数量
 */
export function getUnreadCount() {
  return request.get<number>('/notifications/unread-count')
}

/**
 * 标记通知为已读
 */
export function markAsRead(id: number) {
  return request.put<void>(`/notifications/${id}/read`)
}

/**
 * 全部标记为已读
 */
export function markAllAsRead() {
  return request.put<void>('/notifications/read-all')
}

/**
 * 删除通知
 */
export function deleteNotification(id: number) {
  return request.delete<void>(`/notifications/${id}`)
}
