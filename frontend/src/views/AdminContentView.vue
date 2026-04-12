<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllContents, deleteContent, type UserContentItem } from '@/api/content'
import { Delete, Edit } from '@element-plus/icons-vue'

const router = useRouter()
const contents = ref<UserContentItem[]>([])
const loading = ref(false)

// 加载所有内容
async function loadContents() {
  loading.value = true
  try {
    const data = await getAllContents()
    contents.value = data
  } catch (error: any) {
    console.error('加载内容列表失败:', error)
    ElMessage.error(error.message || '加载内容列表失败')
  } finally {
    loading.value = false
  }
}

// 格式化日期
function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取状态文本
function getStatusText(status: string): string {
  const statusMap: Record<string, string> = {
    pending: '待审核',
    published: '已发布',
    rejected: '已拒绝'
  }
  return statusMap[status] || status
}

// 获取状态类型（用于 el-tag）
function getStatusType(status: string): 'success' | 'warning' | 'danger' | 'info' {
  const typeMap: Record<string, 'success' | 'warning' | 'danger' | 'info'> = {
    pending: 'warning',
    published: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

// 编辑内容
function handleEdit(id: number) {
  router.push(`/content/${id}/edit`)
}

// 删除内容
async function handleDelete(id: number, title: string) {
  try {
    // 管理员删除必须填写原因
    const { value: reason } = await ElMessageBox.prompt(
      `确定要删除内容 "${title}" 吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入删除原因（必填）',
        inputValidator: (value) => {
          if (!value || value.trim() === '') {
            return '删除原因不能为空'
          }
          return true
        },
        type: 'warning'
      }
    )

    await deleteContent(id, reason)
    ElMessage.success('删除成功')
    await loadContents() // 重新加载列表
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadContents()
})
</script>

<template>
  <div class="admin-content-view">
    <div class="page-header">
      <h1>内容管理</h1>
      <p class="subtitle">管理所有用户发布的内容</p>
    </div>

    <!-- 加载状态 -->
    <el-skeleton :loading="loading" animated>
      <template #template>
        <div v-for="i in 5" :key="i" class="skeleton-item">
          <el-skeleton-item variant="rect" style="width: 100%; height: 120px" />
        </div>
      </template>

      <template #default>
        <!-- 空状态 -->
        <el-empty v-if="contents.length === 0" description="暂无内容" />

        <!-- 内容列表 -->
        <div v-else class="content-list">
          <el-card v-for="item in contents" :key="item.id" class="content-card" shadow="hover">
            <div class="content-info">
              <div class="content-header">
                <h3 class="content-title">{{ item.title }}</h3>
                <el-tag :type="getStatusType(item.status)" size="small">
                  {{ getStatusText(item.status) }}
                </el-tag>
              </div>

              <p v-if="item.summary" class="content-summary">
                {{ item.summary }}
              </p>

              <div class="content-meta">
                <span class="meta-item">
                  <el-icon><InfoFilled /></el-icon>
                  {{ item.type === 'news' ? '资讯' : '博客' }}
                </span>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(item.createdAt) }}
                </span>
              </div>
            </div>

            <div class="content-actions">
              <el-button 
                type="primary" 
                size="small" 
                :icon="Edit"
                @click="handleEdit(item.id)"
              >
                编辑
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                :icon="Delete"
                @click="handleDelete(item.id, item.title)"
              >
                删除
              </el-button>
            </div>
          </el-card>
        </div>
      </template>
    </el-skeleton>
  </div>
</template>

<style scoped>
.admin-content-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.skeleton-item {
  margin-bottom: 16px;
}

.content-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.content-card {
  transition: all 0.3s ease;
}

.content-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.content-info {
  flex: 1;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.content-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  flex: 1;
}

.content-summary {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0 0 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.content-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--text-tertiary);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.content-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .admin-content-view {
    padding: 20px 16px;
  }

  .content-card {
    flex-direction: column;
  }

  .content-actions {
    width: 100%;
    justify-content: flex-end;
    margin-top: 12px;
  }
}
</style>
