<template>
  <div class="promotion-container">
    <div class="promotion-header">
      <h1>转正考核</h1>
    </div>

    <!-- 非预备成员提示 -->
    <el-alert
      v-if="!isProbation"
      title="您已是正式成员，无需转正"
      type="success"
      :closable="false"
      class="mb-6"
    />

    <!-- 预备成员内容 -->
    <template v-else>
      <!-- 转正流程说明 -->
      <el-card class="mb-6">
        <template #header>
          <div class="card-header">
            <span class="text-lg font-bold">{{ promotionInfo?.title || '转正考核流程' }}</span>
          </div>
        </template>

        <p class="text-gray-600 mb-4">{{ promotionInfo?.description }}</p>

        <!-- 步骤条 -->
        <el-steps :active="currentStep" finish-status="success" class="mb-6">
          <el-step
            v-for="step in promotionInfo?.steps"
            :key="step.step"
            :title="step.title"
            :description="step.estimatedTime"
          />
        </el-steps>

        <!-- 考核标准 -->
        <el-descriptions title="考核标准" :column="3" border>
          <el-descriptions-item label="答题权重">
            <el-tag type="primary">{{ examWeightPercent }}%</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="项目权重">
            <el-tag type="success">{{ projectWeightPercent }}%</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="及格分数">
            <el-tag type="warning">{{ passingScore }} 分</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-alert class="mt-4" type="info" :closable="false">
          <template #title>
            计算公式：总分 = 答题分数 × {{ examWeightPercent }}% + 项目分数 × {{ projectWeightPercent }}%
          </template>
        </el-alert>
      </el-card>

      <!-- 转正状态与操作 -->
      <el-card v-loading="loading">
        <template #header>
          <div class="card-header">
            <span class="text-lg font-bold">当前状态</span>
          </div>
        </template>

        <!-- 未申请 -->
        <div v-if="!promotionStatus?.hasApplication" class="text-center py-8">
          <el-empty description="您还没有提交转正申请" />
          <div v-if="promotionStatus?.canApply" class="mt-4">
            <AppButton variant="primary" size="lg" @click="handleApply">
              开始转正
            </AppButton>
          </div>
          <div v-else class="mt-4 text-gray-500">
            <el-alert
              :title="`注册时间不足30天，还需等待 ${30 - (promotionStatus?.registrationDays || 0)} 天`"
              type="warning"
              :closable="false"
            />
          </div>
        </div>

        <!-- 已有申请 - 根据状态显示不同内容 -->
        <div v-else>
          <!-- 待答题 -->
          <div v-if="promotionStatus?.application?.status === 'pending_exam'" class="text-center py-8">
            <el-result icon="info" title="待答题" sub-title="您可以开始答题了">
              <template #extra>
                <AppButton variant="primary" size="lg" @click="$router.push({ name: 'exam' })">
                  开始答题
                </AppButton>
              </template>
            </el-result>
          </div>

          <!-- 答题中 -->
          <div v-else-if="promotionStatus?.application?.status === 'exam_in_progress'" class="text-center py-8">
            <el-result icon="warning" title="答题中" sub-title="请在规定时间内完成答题">
              <template #extra>
                <AppButton variant="primary" size="lg" @click="$router.push({ name: 'exam' })">
                  继续答题
                </AppButton>
              </template>
            </el-result>
          </div>

          <!-- 答题未通过 -->
          <div v-else-if="promotionStatus?.application?.status === 'exam_failed'" class="text-center py-8">
            <el-result icon="warning" title="答题未通过" :sub-title="`答题分数：${promotionStatus.application.examScore} 分，未达到及格线`">
              <template #extra>
                <AppButton variant="primary" size="lg" @click="$router.push({ name: 'exam' })">
                  重新答题
                </AppButton>
              </template>
            </el-result>
          </div>

          <!-- 待提交项目 -->
          <div v-else-if="promotionStatus?.application?.status === 'pending_project'" class="text-center py-8">
            <el-result icon="success" title="待提交项目" :sub-title="`答题分数：${promotionStatus.application.examScore} 分`">
              <template #extra>
                <AppButton variant="primary" size="lg" @click="$router.push({ name: 'projectSubmit' })">
                  提交项目
                </AppButton>
              </template>
            </el-result>
          </div>

          <!-- 评审中 -->
          <div v-else-if="promotionStatus?.application?.status === 'project_reviewing'" class="text-center py-8">
            <el-result icon="info" title="评审中" sub-title="负责人正在评审您的项目，预计3-5天">
              <template #extra>
                <el-tag type="warning" size="large">请耐心等待</el-tag>
              </template>
            </el-result>
          </div>

          <!-- 已通过 -->
          <div v-else-if="promotionStatus?.application?.status === 'approved'" class="text-center py-8">
            <el-result icon="success" title="恭喜转正！" sub-title="您已成为正式成员">
              <template #extra>
                <AppButton variant="success" size="lg" @click="$router.push({ name: 'home' })">
                  返回首页
                </AppButton>
              </template>
            </el-result>
            <div class="mt-4">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="答题分数">{{ promotionStatus.application.examScore }} 分</el-descriptions-item>
                <el-descriptions-item label="项目分数">{{ promotionStatus.application.projectScore }} 分</el-descriptions-item>
                <el-descriptions-item label="总分">{{ promotionStatus.application.totalScore }} 分</el-descriptions-item>
                <el-descriptions-item label="通过时间">{{ formatDate(promotionStatus.application.createdAt) }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </div>

          <!-- 已拒绝 -->
          <div v-else-if="promotionStatus?.application?.status === 'rejected'" class="text-center py-8">
            <el-result icon="error" title="转正未通过" :sub-title="promotionStatus.application.reviewComment || '很遗憾，您的转正申请未通过'">
              <template #extra>
                <div v-if="promotionStatus.canApply">
                  <AppButton variant="primary" size="lg" @click="handleApply">
                    重新申请
                  </AppButton>
                </div>
                <div v-else class="text-gray-500 mt-2">
                  暂不符合重新申请条件，请联系负责人
                </div>
              </template>
            </el-result>
            <div class="mt-4">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="答题分数">{{ promotionStatus.application.examScore }} 分</el-descriptions-item>
                <el-descriptions-item label="项目分数">{{ promotionStatus.application.projectScore }} 分</el-descriptions-item>
                <el-descriptions-item label="总分">{{ promotionStatus.application.totalScore }} 分</el-descriptions-item>
                <el-descriptions-item label="申请时间">{{ formatDate(promotionStatus.application.createdAt) }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </div>
      </el-card>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { getPromotionInfo, getPromotionStatus, submitPromotion } from '@/api/promotion'
import type { PromotionInfoResponse, PromotionStatusResponse } from '@/types/promotion'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppButton from '@/components/common/AppButton.vue'

const authStore = useAuthStore()

// 判断是否为预备成员
const isProbation = computed(() => {
  const roles = authStore.user?.roles ?? []
  return roles.includes('ROLE_PROBATION')
})

// 数据
const loading = ref(false)
const promotionInfo = ref<PromotionInfoResponse | null>(null)
const promotionStatus = ref<PromotionStatusResponse | null>(null)

// 计算属性：分数权重百分比
const examWeightPercent = computed(() => (promotionInfo.value?.scoringRules.examWeight ?? 0) * 100)
const projectWeightPercent = computed(() => (promotionInfo.value?.scoringRules.projectWeight ?? 0) * 100)
const passingScore = computed(() => promotionInfo.value?.scoringRules.passingScore ?? 0)

// 当前步骤（根据状态计算）
const currentStep = computed(() => {
  if (!promotionStatus.value?.hasApplication || !promotionStatus.value.application) {
    return 0
  }
  const status = promotionStatus.value.application.status
  const stepMap: Record<string, number> = {
    'pending_exam': 1,
    'exam_in_progress': 2,
    'pending_project': 3,
    'project_reviewing': 4,
    'approved': 5,
    'rejected': 5,
    'exam_failed': 2  // 考试未通过需要重新答题
  }
  return stepMap[status] || 0
})

// 加载转正流程说明
async function loadPromotionInfo() {
  try {
    promotionInfo.value = await getPromotionInfo()
  } catch (error) {
    console.error('加载转正流程说明失败:', error)
  }
}

// 加载转正状态
async function loadPromotionStatus() {
  loading.value = true
  try {
    promotionStatus.value = await getPromotionStatus()
  } catch (error) {
    console.error('加载转正状态失败:', error)
    ElMessage.error('加载转正状态失败')
  } finally {
    loading.value = false
  }
}

// 提交转正申请
async function handleApply() {
  try {
    await ElMessageBox.confirm(
      '确定要提交转正申请吗？提交后将开始答题考核。',
      '确认申请',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    loading.value = true
    await submitPromotion()
    ElMessage.success('申请成功，可以开始答题')
    
    // 重新加载状态
    await loadPromotionStatus()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('提交转正申请失败:', error)
      // 错误消息已由 request.ts 拦截器处理
    }
  } finally {
    loading.value = false
  }
}

// 格式化日期
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

// 页面加载时获取数据
onMounted(async () => {
  await Promise.all([
    loadPromotionInfo(),
    loadPromotionStatus()
  ])
})
</script>

<style scoped>
.promotion-container {
  max-width: var(--max-width-content);
  margin: 0 auto;
  padding: var(--spacing-2xl) var(--spacing-base);
}

.promotion-header {
  margin-bottom: var(--spacing-xl);
}

.promotion-header h1 {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.mb-6 {
  margin-bottom: var(--spacing-xl);
}

.mt-4 {
  margin-top: var(--spacing-lg);
}

.py-8 {
  padding-top: var(--spacing-3xl);
  padding-bottom: var(--spacing-3xl);
}

.text-center {
  text-align: center;
}

/* Descriptions 样式优化 */
:deep(.el-descriptions__label) {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-secondary);
}

:deep(.el-descriptions__content) {
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

/* Steps 样式优化 */
:deep(.el-step__title) {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

:deep(.el-step__description) {
  color: var(--color-text-tertiary);
  font-size: var(--font-size-sm);
}

/* Result 样式优化 */
:deep(.el-result__title) {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

:deep(.el-result__subtitle) {
  color: var(--color-text-secondary);
  font-size: var(--font-size-base);
}

/* 响应式 */
@media (max-width: 768px) {
  .promotion-container {
    padding: var(--spacing-xl) var(--spacing-sm);
  }

  .promotion-header h1 {
    font-size: var(--font-size-2xl);
  }
}
</style>
