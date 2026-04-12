<template>
  <div class="exam-result-container">
    <!-- 庆祝动画 -->
    <CelebrationEffect :trigger="showCelebration" />

    <el-card v-loading="loading" class="result-card animate-fade-in-up" shadow="never">
      <template #header>
        <div class="card-header">
          <h2>考试结果</h2>
          <AppButton variant="outline" @click="$router.push('/promotion')">
            返回转正流程
          </AppButton>
        </div>
      </template>

      <div v-if="examResult" class="result-content">
        <!-- 成绩总览卡片 -->
        <div 
          class="score-overview animate-fade-in-up" 
          :class="{ passed: examResult.passed, failed: !examResult.passed }"
        >
          <div class="score-circle">
            <div class="score-number">{{ examResult.score }}/{{ examResult.maxScore }}</div>
            <div class="score-label">得分/满分</div>
          </div>
          <div class="score-info">
            <p><strong>满分：</strong>{{ examResult.maxScore }}</p>
            <p><strong>结果：</strong>
              <div class="badge" :class="examResult.passed ? 'badge-success' : 'badge-danger'">
                {{ examResult.passed ? '通过' : '未通过' }}
              </div>
            </p>
            <p><strong>考试次数：</strong>第 {{ examResult.attemptCount }} 次</p>
            <p><strong>用时：</strong>{{ examResult.duration }} 分钟</p>
          </div>
        </div>

        <!-- 各题型得分 -->
        <el-divider>各题型得分</el-divider>
        <el-descriptions :column="3" border class="score-descriptions">
          <el-descriptions-item label="单选题得分">{{ examResult.singleChoiceScore }}</el-descriptions-item>
          <el-descriptions-item label="多选题得分">{{ examResult.multipleChoiceScore }}</el-descriptions-item>
          <el-descriptions-item label="判断题得分">{{ examResult.trueFalseScore }}</el-descriptions-item>
        </el-descriptions>

        <!-- 答题详情 -->
        <el-divider>答题详情</el-divider>
        <el-table :data="examResult.details" border stripe class="detail-table">
          <el-table-column prop="questionId" label="题号" width="80" align="center" />
          <el-table-column label="题型" width="120" align="center">
            <template #default="{ row }">
              <div class="badge" :class="getQuestionTypeBadgeClass(row.questionType)">
                {{ getQuestionTypeLabel(row.questionType) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="questionText" label="题目内容" min-width="200" />
          <el-table-column label="你的答案" width="100" align="center">
            <template #default="{ row }">
              <span :class="row.correct ? 'text-success' : 'text-danger'">
                {{ row.userAnswer }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="正确答案" width="100" align="center">
            <template #default="{ row }">
              <span class="text-success">{{ row.correctAnswer }}</span>
            </template>
          </el-table-column>
          <el-table-column label="结果" width="80" align="center">
            <template #default="{ row }">
              <el-icon v-if="row.correct" color="#10b981" size="20"><CircleCheck /></el-icon>
              <el-icon v-else color="#ef4444" size="20"><CircleClose /></el-icon>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="得分" width="80" align="center" />
        </el-table>

        <!-- 错题解析 -->
        <el-divider v-if="wrongQuestions.length > 0">错题解析</el-divider>
        <div v-if="wrongQuestions.length > 0" class="wrong-questions">
          <div
            v-for="(q, index) in wrongQuestions"
            :key="q.questionId"
            class="wrong-question-item animate-fade-in-up"
            :style="{ animationDelay: `${index * 50}ms` }"
          >
            <div class="wrong-question-header">
              <div class="badge badge-danger">
                {{ getQuestionTypeLabel(q.questionType) }} - 第 {{ q.questionId }} 题
              </div>
            </div>
            <div class="wrong-question-body">
              <p class="question-text">{{ q.questionText }}</p>
              <p><strong>你的答案：</strong><span class="text-danger">{{ q.userAnswer }}</span></p>
              <p><strong>正确答案：</strong><span class="text-success">{{ q.correctAnswer }}</span></p>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <AppButton
            v-if="!examResult.passed"
            variant="primary"
            size="lg"
            @click="retryExam"
          >
            重新答题
          </AppButton>
          <AppButton
            v-if="examResult.passed"
            variant="success"
            size="lg"
            @click="$router.push('/promotion')"
          >
            继续转正流程
          </AppButton>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { getExamResult } from '@/api/exam'
import type { ExamResultResponse } from '@/api/exam'
import AppButton from '@/components/common/AppButton.vue'
import CelebrationEffect from '@/components/common/CelebrationEffect.vue'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const examResult = ref<ExamResultResponse | null>(null)
const showCelebration = ref(false)

// 错题列表
const wrongQuestions = computed(() => {
  if (!examResult.value) return []
  return examResult.value.details.filter(d => !d.correct)
})

// 获取题型标签
const getQuestionTypeTag = (type: string) => {
  const tagMap: Record<string, string> = {
    single_choice: 'primary',
    multiple_choice: 'warning',
    true_false: 'success'
  }
  return tagMap[type] || 'info'
}

// 获取题型徽章类名
const getQuestionTypeBadgeClass = (type: string): string => {
  const classMap: Record<string, string> = {
    single_choice: 'badge-primary',
    multiple_choice: 'badge-warning',
    true_false: 'badge-success'
  }
  return classMap[type] || 'badge-info'
}

// 获取题型文字
const getQuestionTypeLabel = (type: string) => {
  const labelMap: Record<string, string> = {
    single_choice: '单选题',
    multiple_choice: '多选题',
    true_false: '判断题'
  }
  return labelMap[type] || type
}

// 加载考试结果
const loadExamResult = async () => {
  loading.value = true
  try {
    const result = await getExamResult()
    examResult.value = result
    
    // 如果通过，显示庆祝动画
    if (result.passed) {
      setTimeout(() => {
        showCelebration.value = true
      }, 500)
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载考试结果失败')
    router.push('/promotion')
  } finally {
    loading.value = false
  }
}

// 重新答题
const retryExam = () => {
  ElMessage.info('正在生成新试卷...')
  router.push('/exam')
}

onMounted(() => {
  loadExamResult()
})
</script>

<style scoped>
.exam-result-container {
  max-width: var(--max-width-wide);
  margin: var(--spacing-xl) auto;
  padding: 0 var(--spacing-base);
}

.result-card {
  min-height: 600px;
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: var(--font-size-3xl);
  color: var(--color-text-primary);
  font-weight: var(--font-weight-bold);
}

.result-content {
  padding: var(--spacing-xl) 0;
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

.badge-success {
  background-color: var(--color-status-published);
}

.badge-danger {
  background-color: var(--color-status-rejected);
}

.badge-primary {
  background-color: var(--color-role-member);
}

.badge-warning {
  background-color: var(--color-role-admin);
}

.badge-info {
  background-color: var(--color-neutral-500);
}

/* 文本颜色 */
.text-success {
  color: var(--color-status-published);
  font-weight: var(--font-weight-semibold);
}

.text-danger {
  color: var(--color-status-rejected);
  font-weight: var(--font-weight-semibold);
}

/* 成绩总览卡片 */
.score-overview {
  display: flex;
  gap: var(--spacing-3xl);
  padding: var(--spacing-2xl);
  margin-bottom: var(--spacing-2xl);
  border-radius: var(--radius-xl);
  background: var(--color-neutral-50);
  animation: fadeInUp 0.6s ease-out forwards;
  opacity: 0;
}

.score-overview.passed {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
}

.score-overview.failed {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
}

.score-circle {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  background: var(--color-surface);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  box-shadow: var(--shadow-card);
  transition: transform var(--transition-base);
}

.score-circle:hover {
  transform: scale(1.05);
}

.score-number {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  line-height: 1.2;
  font-family: var(--font-family-mono);
}

.score-label {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  margin-top: var(--spacing-sm);
}

.score-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: var(--spacing-lg);
  font-size: var(--font-size-base);
}

.score-info p {
  margin: 0;
}

/* Descriptions 样式优化 */
.score-descriptions :deep(.el-descriptions__label) {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-secondary);
}

.score-descriptions :deep(.el-descriptions__content) {
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

/* 答题详情表格 */
.detail-table {
  margin-top: var(--spacing-xl);
}

.detail-table :deep(.el-table__header th) {
  background-color: var(--color-neutral-50);
  color: var(--color-text-primary);
  font-weight: var(--font-weight-semibold);
}

.detail-table :deep(.el-table__row:hover) {
  background-color: var(--color-primary-50);
}

/* 错题解析 */
.wrong-questions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.wrong-question-item {
  padding: var(--spacing-lg);
  background: var(--color-status-rejected);
  background-opacity: 0.05;
  border: 1.5px solid var(--color-status-rejected);
  border-radius: var(--radius-lg);
  transition: all var(--transition-base);
  animation: fadeInUp 0.6s ease-out forwards;
  opacity: 0;
}

.wrong-question-item:hover {
  box-shadow: var(--shadow-card-hover);
  transform: translateY(-2px);
}

.wrong-question-header {
  margin-bottom: var(--spacing-md);
}

.wrong-question-body {
  padding-left: var(--spacing-md);
}

.question-text {
  margin: var(--spacing-sm) 0;
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  line-height: 1.6;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: var(--spacing-xl);
  margin-top: var(--spacing-3xl);
  padding-top: var(--spacing-xl);
  border-top: 2px solid var(--color-neutral-100);
}

/* 响应式 */
@media (max-width: 768px) {
  .exam-result-container {
    padding: 0 var(--spacing-sm);
  }

  .card-header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: flex-start;
  }

  .score-overview {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .score-circle {
    width: 150px;
    height: 150px;
  }

  .score-number {
    font-size: var(--font-size-3xl);
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons :deep(.app-button) {
    width: 100%;
  }
}
</style>
