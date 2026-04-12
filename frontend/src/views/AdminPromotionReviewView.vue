<template>
  <div class="admin-review-container">
    <div class="admin-review-header animate-fade-in-up">
      <h1>转正项目评审</h1>
    </div>

    <el-card shadow="never" class="animate-fade-in-up">
      <!-- 筛选 -->
      <div class="filter-bar">
        <el-select v-model="filterStatus" placeholder="全部状态" clearable style="width: 200px" @change="loadReviews">
          <el-option label="待提交项目" value="pending_project" />
          <el-option label="评审中" value="project_reviewing" />
        </el-select>
      </div>

      <!-- 表格 -->
      <el-table :data="reviews" v-loading="loading" stripe>
        <el-table-column prop="applicantName" label="申请人" width="120" />
        <el-table-column prop="projectName" label="项目名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="examScore" label="考试分数" width="100" align="center" />
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <div class="badge" :class="row.status === 'pending_project' ? 'badge-warning' : 'badge-info'">
              {{ row.status === 'pending_project' ? '待提交项目' : '评审中' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.submittedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <AppButton variant="primary" size="sm" @click="openReviewDialog(row as ReviewItem)">
              评审
            </AppButton>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadReviews"
          @current-change="loadReviews"
        />
      </div>
    </el-card>

    <!-- 评审对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="项目评审"
      width="600px"
      @close="resetForm"
    >
      <div v-if="selectedItem" class="review-dialog-content">
        <!-- 项目信息展示 -->
        <el-descriptions title="项目信息" :column="1" border class="mb-4">
          <el-descriptions-item label="申请人">{{ selectedItem.applicantName }}</el-descriptions-item>
          <el-descriptions-item label="项目名称">{{ selectedItem.projectName }}</el-descriptions-item>
          <el-descriptions-item label="考试分数">{{ selectedItem.examScore }} 分</el-descriptions-item>
          <el-descriptions-item label="项目链接">
            <a v-if="selectedItem.projectUrl" :href="selectedItem.projectUrl" target="_blank">
              {{ selectedItem.projectUrl }}
            </a>
            <span v-else class="text-gray-400">未提供</span>
          </el-descriptions-item>
          <el-descriptions-item label="项目描述">
            <div class="project-description">{{ selectedItem.projectDescription }}</div>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 评审表单 -->
        <el-form
          ref="reviewFormRef"
          :model="reviewForm"
          :rules="reviewRules"
          label-width="100px"
        >
          <el-form-item label="项目评分" prop="projectScore">
            <el-input-number
              v-model="reviewForm.projectScore"
              :min="0"
              :max="100"
              :step="1"
              style="width: 200px"
            />
            <span class="ml-2 text-gray-500">分（0-100）</span>
          </el-form-item>

          <el-form-item label="评审意见" prop="reviewComment">
            <el-input
              v-model="reviewForm.reviewComment"
              type="textarea"
              placeholder="可选，填写评审意见和建议（最多500字符）"
              :rows="4"
              :maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-alert
              title="总分计算"
              type="info"
              :closable="false"
              :description="`总分 = 考试分数(${selectedItem.examScore}) × 60% + 项目分数(${reviewForm.projectScore}) × 40% = ${calculateTotal()} 分`"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <AppButton variant="outline" @click="dialogVisible = false">取消</AppButton>
        <AppButton variant="primary" :loading="submitting" @click="submitReview">
          提交评审
        </AppButton>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getPendingReviews, reviewProject } from '@/api/promotion'
import type { FormInstance, FormRules } from 'element-plus'
import type { ReviewItem } from '@/types/promotion'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppButton from '@/components/common/AppButton.vue'

const loading = ref(false)
const submitting = ref(false)
const reviews = ref<ReviewItem[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const filterStatus = ref('')

const dialogVisible = ref(false)
const selectedItem = ref<ReviewItem | null>(null)
const reviewFormRef = ref<FormInstance>()

const reviewForm = reactive({
  applicationId: 0,
  projectScore: 80,
  reviewComment: ''
})

const reviewRules = reactive<FormRules>({
  projectScore: [
    { required: true, message: '请输入项目评分', trigger: 'blur' }
  ]
})

async function loadReviews() {
  loading.value = true
  try {
    const result = await getPendingReviews(currentPage.value, pageSize.value, filterStatus.value)
    reviews.value = result.records
    total.value = result.total || 0
  } catch (error) {
    console.error('加载评审列表失败:', error)
    ElMessage.error('加载评审列表失败')
  } finally {
    loading.value = false
  }
}

function openReviewDialog(item: ReviewItem) {
  selectedItem.value = item
  reviewForm.applicationId = item.applicationId
  reviewForm.projectScore = 80
  reviewForm.reviewComment = ''
  dialogVisible.value = true
}

function calculateTotal(): number {
  if (!selectedItem.value || !selectedItem.value.examScore) return 0
  return Math.round(selectedItem.value.examScore * 0.6 + reviewForm.projectScore * 0.4)
}

function resetForm() {
  reviewFormRef.value?.resetFields()
  selectedItem.value = null
}

async function submitReview() {
  if (!reviewFormRef.value) return

  await reviewFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      await ElMessageBox.confirm(
        `确认提交评审？总分：${calculateTotal()} 分（${calculateTotal() >= 70 ? '通过' : '不通过'}）`,
        '确认评审',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'info' }
      )

      submitting.value = true
      await reviewProject(reviewForm)
      ElMessage.success('评审完成')
      dialogVisible.value = false
      await loadReviews()
    } catch (error: any) {
      if (error !== 'cancel') {
        console.error('提交评审失败:', error)
      }
    } finally {
      submitting.value = false
    }
  })
}

function formatDate(dateStr: string): string {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadReviews()
})
</script>

<style scoped>
.admin-review-container {
  max-width: var(--max-width-wide);
  margin: 0 auto;
  padding: var(--spacing-2xl) var(--spacing-base);
}

.admin-review-header {
  margin-bottom: var(--spacing-xl);
}

.admin-review-header h1 {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

/* 徽章样式 */
.badge {
  display: inline-block;
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: white;
}

.badge-warning {
  background-color: var(--color-role-admin);
}

.badge-info {
  background-color: var(--color-neutral-500);
}

.filter-bar {
  margin-bottom: var(--spacing-lg);
}

.pagination-container {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
}

.mb-4 {
  margin-bottom: var(--spacing-lg);
}

.ml-2 {
  margin-left: var(--spacing-sm);
}

.text-gray-400 {
  color: var(--color-neutral-400);
}

.text-gray-500 {
  color: var(--color-neutral-500);
}

.project-description {
  white-space: pre-wrap;
  word-break: break-word;
  max-height: 200px;
  overflow-y: auto;
  color: var(--color-text-primary);
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

:deep(.el-table th.el-table__cell) {
  background-color: var(--color-neutral-50);
  color: var(--color-text-primary);
  font-weight: var(--font-weight-semibold);
}

:deep(.el-table__row:hover) {
  background-color: var(--color-primary-50);
}

/* Descriptions 样式优化 */
:deep(.el-descriptions__label) {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-secondary);
}

:deep(.el-descriptions__content) {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

/* 对话框样式优化 */
:deep(.el-dialog__header) {
  border-bottom: 2px solid var(--color-neutral-100);
  padding-bottom: var(--spacing-md);
}

:deep(.el-dialog__body) {
  padding: var(--spacing-xl);
}

/* 响应式 */
@media (max-width: 768px) {
  .admin-review-container {
    padding: var(--spacing-xl) var(--spacing-sm);
  }

  .admin-review-header h1 {
    font-size: var(--font-size-2xl);
  }

  .filter-bar {
    width: 100%;
  }

  .filter-bar :deep(.el-select) {
    width: 100% !important;
  }
}
</style>
