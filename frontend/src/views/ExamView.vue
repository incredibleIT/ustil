<template>
  <div class="exam-container">
    <!-- 考试头部 -->
    <div class="exam-header animate-fade-in-up">
      <div class="exam-info">
        <h2>转正考试</h2>
        <div class="badge badge-info">总分: {{ paper.totalScore }} 分</div>
        <div class="badge badge-warning">时长: {{ paper.duration }} 分钟</div>
      </div>
      <div class="exam-timer" :class="{ 'timer-warning': remainingTime < 300 }">
        <el-icon><Clock /></el-icon>
        <span class="timer-text">
          {{ formatTime(remainingTime) }}
        </span>
      </div>
    </div>

    <!-- 答题区域 -->
    <div class="exam-content">
      <!-- 骨架屏加载状态 -->
      <SkeletonScreen v-if="loading" type="card" :count="3" />

      <template v-else>
        <el-alert
          title="请认真作答，提交后将无法修改"
          type="warning"
          :closable="false"
          class="mb-4"
        />

        <!-- 单选题 -->
        <div v-if="singleChoiceQuestions.length > 0" class="question-section animate-fade-in-up">
          <h3 class="section-title">一、单选题（共 {{ singleChoiceQuestions.length }} 题）</h3>
          <el-card
            v-for="(question, index) in singleChoiceQuestions"
            :key="question.id"
            class="question-card mb-4"
            :style="{ animationDelay: `${index * 50}ms` }"
          >
            <div class="question-header">
              <span class="question-number">{{ index + 1 }}.</span>
              <span class="question-text">{{ question.questionText }}</span>
              <div class="badge badge-primary">{{ question.score }} 分</div>
            </div>
            <el-radio-group v-model="answers[question.id]" class="options-group">
              <el-radio
                v-for="(option, optIndex) in question.options"
                :key="optIndex"
                :value="getOptionLetter(optIndex)"
                class="option-item"
              >
                {{ getOptionLetter(optIndex) }}. {{ option }}
              </el-radio>
            </el-radio-group>
          </el-card>
        </div>

        <!-- 多选题 -->
        <div v-if="multipleChoiceQuestions.length > 0" class="question-section animate-fade-in-up">
          <h3 class="section-title">二、多选题（共 {{ multipleChoiceQuestions.length }} 题）</h3>
          <el-card
            v-for="(question, index) in multipleChoiceQuestions"
            :key="question.id"
            class="question-card mb-4"
            :style="{ animationDelay: `${(singleChoiceQuestions.length + index) * 50}ms` }"
          >
            <div class="question-header">
              <span class="question-number">{{ singleChoiceQuestions.length + index + 1 }}.</span>
              <span class="question-text">{{ question.questionText }}</span>
              <div class="badge badge-success">{{ question.score }} 分</div>
            </div>
            <el-alert
              type="info"
              :closable="false"
              class="mb-2"
            >
              提示：多选、错选不得分
            </el-alert>
            <el-checkbox-group v-model="multipleAnswers[question.id]" class="options-group">
              <el-checkbox
                v-for="(option, optIndex) in question.options"
                :key="optIndex"
                :value="getOptionLetter(optIndex)"
                class="option-item"
              >
                {{ getOptionLetter(optIndex) }}. {{ option }}
              </el-checkbox>
            </el-checkbox-group>
          </el-card>
        </div>

        <!-- 判断题 -->
        <div v-if="trueFalseQuestions.length > 0" class="question-section animate-fade-in-up">
          <h3 class="section-title">三、判断题（共 {{ trueFalseQuestions.length }} 题）</h3>
          <el-card
            v-for="(question, index) in trueFalseQuestions"
            :key="question.id"
            class="question-card mb-4"
            :style="{ animationDelay: `${(singleChoiceQuestions.length + multipleChoiceQuestions.length + index) * 50}ms` }"
          >
            <div class="question-header">
              <span class="question-number">{{ singleChoiceQuestions.length + multipleChoiceQuestions.length + index + 1 }}.</span>
              <span class="question-text">{{ question.questionText }}</span>
              <div class="badge badge-warning">{{ question.score }} 分</div>
            </div>
            <el-radio-group v-model="answers[question.id]" class="options-group">
              <el-radio value="对" class="option-item">对</el-radio>
              <el-radio value="错" class="option-item">错</el-radio>
            </el-radio-group>
          </el-card>
        </div>

        <!-- 提交按钮 -->
        <div class="submit-section">
          <AppButton variant="primary" size="lg" @click="handleSubmit" :loading="submitting">
            提交试卷
          </AppButton>
          <AppButton variant="outline" size="lg" @click="handleCancel">
            取消
          </AppButton>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Clock } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { generatePaper, submitExam } from '@/api/exam'
import type { PaperResponse, PaperQuestionItem } from '@/types/question'
import AppButton from '@/components/common/AppButton.vue'
import SkeletonScreen from '@/components/common/SkeletonScreen.vue'

const router = useRouter()

// 数据
const loading = ref(false)
const submitting = ref(false)
const paper = ref<PaperResponse>({
  paperId: 0,
  questions: [],
  totalScore: 0,
  duration: 60
})

// 计时器
const remainingTime = ref(0)
let timer: number | null = null

// 考试开始时间
const examStartTime = ref<string>('')

// 答案存储
const answers = reactive<Record<number, string>>({})
const multipleAnswers = reactive<Record<number, string[]>>({})

// 按题型分类
const singleChoiceQuestions = computed(() =>
  paper.value.questions.filter(q => q.questionType === 'single_choice')
)

const multipleChoiceQuestions = computed(() =>
  paper.value.questions.filter(q => q.questionType === 'multiple_choice')
)

const trueFalseQuestions = computed(() =>
  paper.value.questions.filter(q => q.questionType === 'true_false')
)

// 格式化时间
function formatTime(seconds: number): string {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
}

// 获取选项字母
function getOptionLetter(index: number): string {
  return String.fromCharCode(65 + index)
}

// 初始化多选题答案
function initMultipleAnswers() {
  multipleChoiceQuestions.value.forEach(q => {
    if (!multipleAnswers[q.id]) {
      multipleAnswers[q.id] = []
    }
  })
}

// 初始化多选题答案
function initMultipleAnswers() {
  multipleChoiceQuestions.value.forEach(q => {
    if (!multipleAnswers[q.id]) {
      multipleAnswers[q.id] = []
    }
  })
}

// 加载试卷
async function loadPaper() {
  loading.value = true
  try {
    paper.value = await generatePaper()
    remainingTime.value = paper.value.duration * 60
    
    // 记录考试开始时间（本地时间格式，避免时区问题）
    const now = new Date()
    const year = now.getFullYear()
    const month = String(now.getMonth() + 1).padStart(2, '0')
    const day = String(now.getDate()).padStart(2, '0')
    const hours = String(now.getHours()).padStart(2, '0')
    const minutes = String(now.getMinutes()).padStart(2, '0')
    const seconds = String(now.getSeconds()).padStart(2, '0')
    examStartTime.value = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`
    
    // 初始化答案
    paper.value.questions.forEach(q => {
      if (q.questionType === 'multiple_choice') {
        multipleAnswers[q.id] = []
      } else {
        answers[q.id] = ''
      }
    })

    // 启动计时器
    startTimer()
  } catch (error: any) {
    ElMessage.error(error.message || '加载试卷失败')
    router.push('/promotion')
  } finally {
    loading.value = false
  }
}

// 启动计时器
function startTimer() {
  timer = window.setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else {
      // 时间到，自动提交
      ElMessage.warning('考试时间到，自动提交试卷')
      handleSubmit()
    }
  }, 1000)
}

// 提交试卷
async function handleSubmit() {
  try {
    await ElMessageBox.confirm(
      '确定要提交试卷吗？提交后将无法修改答案。',
      '确认提交',
      {
        confirmButtonText: '确定',
        cancelButtonText: '再检查一下',
        type: 'warning'
      }
    )

    submitting.value = true

    // 构建答案数据：提交所有题目，未作答的传空字符串
    const answerList: Array<{ questionId: number; answer: string }> = []

    // 单选题答案（包含未作答的）
    singleChoiceQuestions.value.forEach(q => {
      answerList.push({ questionId: q.id, answer: answers[q.id] || '' })
    })

    // 多选题答案（包含未作答的）
    multipleChoiceQuestions.value.forEach(q => {
      const ans = multipleAnswers[q.id] || []
      answerList.push({ questionId: q.id, answer: ans.length > 0 ? [...ans].sort().join('') : '' })
    })

    // 判断题答案（包含未作答的）
    trueFalseQuestions.value.forEach(q => {
      answerList.push({ questionId: q.id, answer: answers[q.id] || '' })
    })

    // 统计已答题数量
    const answeredCount = answerList.filter(a => a.answer && a.answer !== '').length

    // 验证是否至少答了一题
    if (answeredCount === 0) {
      ElMessage.warning('请至少回答一道题目后再提交')
      submitting.value = false
      return
    }

    // 调用提交接口（传入考试开始时间）
    const result = await submitExam({ 
      paperId: paper.value.paperId, 
      startTime: examStartTime.value,
      answers: answerList 
    })
    
    // 清理定时器
    if (timer) {
      clearInterval(timer)
      timer = null
    }
    
    ElMessage.success('试卷已提交！')
    
    // 跳转到结果页面
    router.push({ name: 'examResult', query: { recordId: result.examRecordId } })
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '提交失败')
    }
  } finally {
    submitting.value = false
  }
}

// 取消考试
async function handleCancel() {
  try {
    await ElMessageBox.confirm(
      '确定要取消考试吗？当前答案将不会保存。',
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '继续答题',
        type: 'warning'
      }
    )

    if (timer) {
      clearInterval(timer)
      timer = null
    }
    
    router.push('/promotion')
  } catch (error) {
    // 用户取消
  }
}

// 生命周期
onMounted(() => {
  loadPaper()
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})
</script>

<style scoped>
.exam-container {
  max-width: var(--max-width-wide);
  margin: 0 auto;
  padding: var(--spacing-2xl) var(--spacing-base);
  background: var(--color-background);
  min-height: 100vh;
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--color-surface);
  padding: var(--spacing-xl);
  border-radius: var(--radius-xl);
  margin-bottom: var(--spacing-xl);
  box-shadow: var(--shadow-card);
}

.exam-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.exam-info h2 {
  margin: 0;
  font-size: var(--font-size-3xl);
  color: var(--color-text-primary);
  font-weight: var(--font-weight-bold);
}

/* 徽章样式 */
.badge {
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: white;
}

.badge-info {
  background-color: var(--color-neutral-500);
}

.badge-warning {
  background-color: var(--color-role-admin);
}

.badge-primary {
  background-color: var(--color-role-member);
}

.badge-success {
  background-color: var(--color-status-published);
}

/* 计时器 */
.exam-timer {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--font-size-2xl);
  color: var(--color-role-member);
  padding: var(--spacing-sm) var(--spacing-lg);
  background: var(--color-primary-50);
  border-radius: var(--radius-lg);
  transition: all var(--transition-base);
}

.timer-text {
  font-weight: var(--font-weight-bold);
  font-family: var(--font-family-mono);
}

.timer-warning {
  color: var(--color-status-rejected);
  background: var(--color-status-rejected);
  background-opacity: 0.1;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.exam-content {
  background: var(--color-surface);
  padding: var(--spacing-2xl);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
}

.mb-4 {
  margin-bottom: var(--spacing-lg);
}

.question-section {
  margin-bottom: var(--spacing-3xl);
  animation: fadeInUp 0.6s ease-out forwards;
  opacity: 0;
}

.section-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-sm);
  border-bottom: 3px solid var(--color-role-member);
}

.question-card {
  transition: all var(--transition-base);
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-lg);
  animation: fadeInUp 0.6s ease-out forwards;
  opacity: 0;
}

.question-card:hover {
  box-shadow: var(--shadow-card-hover);
  transform: translateY(-2px);
}

.question-header {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
}

.question-number {
  font-weight: var(--font-weight-bold);
  color: var(--color-role-member);
  font-size: var(--font-size-lg);
  min-width: 30px;
}

.question-text {
  flex: 1;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
  line-height: 1.6;
}

.options-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding-left: 38px;
}

.option-item {
  margin: 0;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  transition: background var(--transition-base);
}

.option-item:hover {
  background: var(--color-neutral-50);
}

.submit-section {
  display: flex;
  justify-content: center;
  gap: var(--spacing-lg);
  margin-top: var(--spacing-3xl);
  padding-top: var(--spacing-xl);
  border-top: 2px solid var(--color-neutral-100);
}

/* 响应式 */
@media (max-width: 768px) {
  .exam-container {
    padding: var(--spacing-xl) var(--spacing-sm);
  }

  .exam-header {
    flex-direction: column;
    gap: var(--spacing-lg);
    align-items: flex-start;
  }

  .exam-info {
    flex-wrap: wrap;
  }

  .exam-info h2 {
    font-size: var(--font-size-2xl);
  }

  .exam-timer {
    width: 100%;
    justify-content: center;
  }

  .question-header {
    flex-direction: column;
  }

  .options-group {
    padding-left: 0;
  }

  .submit-section {
    flex-direction: column;
  }

  .submit-section :deep(.app-button) {
    width: 100%;
  }
}
</style>
