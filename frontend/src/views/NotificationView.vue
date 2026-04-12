<template>
  <div class="notification-view">
    <div class="header animate-fade-in-up">
      <h2>我的通知</h2>
      <AppButton 
        variant="primary" 
        :disabled="unreadCount === 0"
        @click="handleMarkAllAsRead"
      >
        全部标记为已读
      </AppButton>
    </div>

    <!-- 骨架屏加载状态 -->
    <SkeletonScreen v-if="loading" type="list" :count="5" />

    <div v-else class="notification-list">
      <el-empty v-if="notifications.length === 0" description="暂无通知" />
      
      <div v-else class="list">
        <div
          v-for="(item, index) in notifications"
          :key="item.id"
          :class="['notification-item', { 'is-unread': item.isRead === 0 }]"
          :style="{ animationDelay: `${index * 50}ms` }"
          class="animate-fade-in-up"
          @click="handleViewDetail(item)"
        >
          <div class="notification-header">
            <span class="title">{{ item.title }}</span>
            <span class="time">{{ formatTime(item.createdAt) }}</span>
          </div>
          
          <div class="notification-content">
            {{ item.content.substring(0, 100) }}{{ item.content.length > 100 ? '...' : '' }}
          </div>
          
          <div class="notification-footer">
            <div class="badge" :class="getBadgeClass(item.type)">
              {{ getBadgeText(item.type) }}
            </div>
            
            <div class="actions">
              <AppButton 
                v-if="item.isRead === 0"
                variant="outline"
                size="sm"
                @click.stop="handleMarkAsRead(item.id)"
              >
                标记已读
              </AppButton>
              <AppButton 
                variant="danger"
                size="sm"
                @click.stop="handleDelete(item.id)"
              >
                删除
              </AppButton>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-pagination
      v-if="total > pageSize"
      class="pagination"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next"
      @current-change="handlePageChange"
    />

    <!-- 通知详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      :title="selectedNotification?.title || '通知详情'"
      width="600px"
      class="notification-dialog"
    >
      <div v-if="selectedNotification" class="detail-content">
        <div class="detail-header">
          <div class="badge" :class="getBadgeClass(selectedNotification.type)">
            {{ getBadgeText(selectedNotification.type) }}
          </div>
          <span class="time">{{ formatTime(selectedNotification.createdAt) }}</span>
        </div>
        
        <div class="detail-body">
          <pre>{{ selectedNotification.content }}</pre>
        </div>
        
        <div class="detail-footer">
          <AppButton variant="primary" @click="handleViewPromotionStatus">
            查看转正状态
          </AppButton>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useNotificationStore } from '@/stores/notification'
import { NotificationType } from '@/types/notification'
import type { NotificationItem } from '@/types/notification'
import AppButton from '@/components/common/AppButton.vue'
import SkeletonScreen from '@/components/common/SkeletonScreen.vue'

const router = useRouter()
const notificationStore = useNotificationStore()

const loading = computed(() => notificationStore.loading)
const notifications = computed(() => notificationStore.notifications)
const unreadCount = computed(() => notificationStore.unreadCount)
const total = computed(() => notificationStore.total)
const currentPage = computed(() => notificationStore.currentPage)
const pageSize = computed(() => notificationStore.pageSize)

const detailVisible = ref(false)
const selectedNotification = ref<NotificationItem | null>(null)

/**
 * 格式化时间
 */
function formatTime(time: string): string {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  // 小于1小时
  if (diff < 3600000) {
    const minutes = Math.floor(diff / 60000)
    return minutes < 1 ? '刚刚' : `${minutes}分钟前`
  }
  
  // 小于24小时
  if (diff < 86400000) {
    const hours = Math.floor(diff / 3600000)
    return `${hours}小时前`
  }
  
  // 超过24小时
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

/**
 * 获取徽章样式类
 */
function getBadgeClass(type: NotificationType): string {
  return type === NotificationType.PROMOTION_APPROVED ? 'badge-success' : 'badge-danger'
}

/**
 * 获取徽章文本
 */
function getBadgeText(type: NotificationType): string {
  return type === NotificationType.PROMOTION_APPROVED ? '转正通过' : '转正未通过'
}

/**
 * 查看详情
 */
function handleViewDetail(item: NotificationItem) {
  selectedNotification.value = item
  detailVisible.value = true
  
  // 如果未读，标记为已读
  if (item.isRead === 0) {
    notificationStore.markNotificationAsRead(item.id)
  }
}

/**
 * 查看转正状态
 */
function handleViewPromotionStatus() {
  detailVisible.value = false
  router.push('/promotion')
}

/**
 * 标记已读
 */
async function handleMarkAsRead(id: number) {
  try {
    await notificationStore.markNotificationAsRead(id)
    ElMessage.success('已标记为已读')
  } catch {
    ElMessage.error('标记失败')
  }
}

/**
 * 全部标记已读
 */
async function handleMarkAllAsRead() {
  try {
    await ElMessageBox.confirm('确定要将所有通知标记为已读吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    await notificationStore.markAllNotificationsAsRead()
    ElMessage.success('已全部标记为已读')
  } catch {
    // 用户取消
  }
}

/**
 * 删除通知
 */
async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定要删除此通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    await notificationStore.removeNotification(id)
    ElMessage.success('删除成功')
  } catch {
    // 用户取消
  }
}

/**
 * 分页切换
 */
function handlePageChange(page: number) {
  notificationStore.fetchNotifications(page)
}

onMounted(() => {
  notificationStore.fetchNotifications()
  notificationStore.fetchUnreadCount()
})
</script>

<style scoped>
.notification-view {
  max-width: var(--max-width-content);
  margin: 0 auto;
  padding: var(--spacing-2xl) var(--spacing-base);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.header h2 {
  margin: 0;
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

.notification-list {
  min-height: 400px;
}

.list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.notification-item {
  padding: var(--spacing-lg);
  background: var(--color-surface);
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-base);
  animation: fadeInUp 0.6s ease-out forwards;
  opacity: 0;
}

.notification-item:hover {
  box-shadow: var(--shadow-card-hover);
  transform: translateY(-2px);
}

.notification-item.is-unread {
  border-left: 4px solid var(--color-primary-500);
  background: var(--color-primary-50);
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-sm);
}

.notification-header .title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.notification-header .time {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}

.notification-content {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin-bottom: var(--spacing-md);
}

.notification-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 徽章样式 */
.badge {
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  color: white;
}

.badge-success {
  background-color: var(--color-status-published);
}

.badge-danger {
  background-color: var(--color-status-rejected);
}

.actions {
  display: flex;
  gap: var(--spacing-sm);
}

.pagination {
  margin-top: var(--spacing-xl);
  display: flex;
  justify-content: center;
}

/* 对话框样式优化 */
.notification-dialog :deep(.el-dialog__header) {
  border-bottom: 2px solid var(--color-neutral-100);
  padding-bottom: var(--spacing-md);
}

.notification-dialog :deep(.el-dialog__body) {
  padding: var(--spacing-xl);
}

.detail-content {
  .detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: var(--spacing-lg);
    padding-bottom: var(--spacing-md);
    border-bottom: 2px solid var(--color-neutral-100);
    
    .time {
      font-size: var(--font-size-sm);
      color: var(--color-text-tertiary);
    }
  }
  
  .detail-body {
    pre {
      white-space: pre-wrap;
      word-wrap: break-word;
      font-family: inherit;
      font-size: var(--font-size-sm);
      line-height: 1.8;
      color: var(--color-text-primary);
      margin: 0;
      padding: var(--spacing-lg);
      background: var(--color-neutral-50);
      border-radius: var(--radius-md);
      border: 1px solid var(--color-border);
    }
  }
  
  .detail-footer {
    margin-top: var(--spacing-xl);
    padding-top: var(--spacing-lg);
    border-top: 2px solid var(--color-neutral-100);
    display: flex;
    justify-content: flex-end;
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .notification-view {
    padding: var(--spacing-xl) var(--spacing-sm);
  }

  .header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: flex-start;
  }

  .header h2 {
    font-size: var(--font-size-2xl);
  }

  .notification-footer {
    flex-direction: column;
    gap: var(--spacing-sm);
    align-items: flex-start;
  }

  .actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
