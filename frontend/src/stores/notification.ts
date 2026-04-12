import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getNotifications, getUnreadCount, markAsRead, markAllAsRead, deleteNotification } from '@/api/notification'
import type { NotificationItem, NotificationListResponse } from '@/types/notification'

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref<NotificationItem[]>([])
  const unreadCount = ref(0)
  const loading = ref(false)
  const total = ref(0)
  const pages = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)

  /**
   * 获取未读通知数量
   */
  async function fetchUnreadCount() {
    try {
      const count = await getUnreadCount()
      unreadCount.value = count
    } catch (error) {
      console.error('获取未读通知数量失败:', error)
    }
  }

  /**
   * 获取通知列表
   */
  async function fetchNotifications(page?: number) {
    if (page) {
      currentPage.value = page
    }
    
    loading.value = true
    try {
      const response: NotificationListResponse = await getNotifications(currentPage.value, pageSize.value)
      notifications.value = response.records
      total.value = response.total
      pages.value = response.pages
    } catch (error) {
      console.error('获取通知列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  /**
   * 标记通知为已读
   */
  async function markNotificationAsRead(id: number) {
    try {
      await markAsRead(id)
      // 更新本地状态
      const notification = notifications.value.find(n => n.id === id)
      if (notification) {
        notification.isRead = 1
      }
      // 刷新未读计数
      await fetchUnreadCount()
    } catch (error) {
      console.error('标记已读失败:', error)
      throw error
    }
  }

  /**
   * 全部标记为已读
   */
  async function markAllNotificationsAsRead() {
    try {
      await markAllAsRead()
      // 更新本地状态
      notifications.value.forEach(n => {
        n.isRead = 1
      })
      unreadCount.value = 0
    } catch (error) {
      console.error('全部标记已读失败:', error)
      throw error
    }
  }

  /**
   * 删除通知
   */
  async function removeNotification(id: number) {
    try {
      await deleteNotification(id)
      // 更新本地状态
      const index = notifications.value.findIndex(n => n.id === id)
      if (index !== -1) {
        notifications.value.splice(index, 1)
      }
      // 刷新未读计数
      await fetchUnreadCount()
    } catch (error) {
      console.error('删除通知失败:', error)
      throw error
    }
  }

  return {
    notifications,
    unreadCount,
    loading,
    total,
    pages,
    currentPage,
    pageSize,
    fetchUnreadCount,
    fetchNotifications,
    markNotificationAsRead,
    markAllNotificationsAsRead,
    removeNotification,
  }
})
