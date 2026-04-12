/**
 * 通知类型枚举
 */
export enum NotificationType {
  /** 转正通过 */
  PROMOTION_APPROVED = 'promotion_approved',
  /** 转正拒绝 */
  PROMOTION_REJECTED = 'promotion_rejected',
}

/**
 * 通知项接口
 */
export interface NotificationItem {
  id: number
  type: string
  title: string
  content: string
  isRead: number
  relatedId: number | null
  createdAt: string
  readAt: string | null
}

/**
 * 通知列表响应
 */
export interface NotificationListResponse {
  total: number
  pages: number
  current: number
  size: number
  records: NotificationItem[]
}
