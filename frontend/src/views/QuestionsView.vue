<template>
  <div class="questions-container">
    <div class="page-header">
      <h2 class="page-title">题库管理</h2>
      <AppButton variant="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加题目
      </AppButton>
    </div>

    <!-- 题目统计 -->
    <div class="stats-bar" v-if="questionStats">
      <div class="badge badge-info">总题数: {{ questionStats.total }}</div>
      <div class="badge badge-primary">单选题: {{ questionStats.single_choice }}</div>
      <div class="badge badge-success">多选题: {{ questionStats.multiple_choice }}</div>
      <div class="badge badge-warning">判断题: {{ questionStats.true_false }}</div>
      <div class="badge badge-danger">简答题: {{ questionStats.short_answer }}</div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-bar">
      <el-select
        v-model="searchForm.type"
        placeholder="题型筛选"
        clearable
        @change="handleSearch"
        class="filter-select"
      >
        <el-option label="全部" value="" />
        <el-option label="单选题" value="single_choice" />
        <el-option label="多选题" value="multiple_choice" />
        <el-option label="判断题" value="true_false" />
        <el-option label="简答题" value="short_answer" />
      </el-select>

      <el-input
        v-model="searchForm.keyword"
        placeholder="搜索题目内容"
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-button type="primary" @click="handleSearch" :loading="searching">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
    </div>

    <!-- 题目列表 -->
    <el-table :data="questionList" v-loading="loading" style="width: 100%" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="题目内容" min-width="300">
        <template #default="{ row }">
          <div class="question-text">{{ row.questionText }}</div>
        </template>
      </el-table-column>
      <el-table-column label="题型" width="100">
        <template #default="{ row }">
          <div class="badge" :class="getQuestionTypeBadgeClass(row.questionType)">
            {{ getQuestionTypeLabel(row.questionType) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="分值" width="80">
        <template #default="{ row }">
          {{ row.score }}分
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="180">
        <template #default="{ row }">
          <AppButton variant="outline" size="sm" @click="handleEdit(row)">编辑</AppButton>
          <AppButton variant="danger" size="sm" @click="handleDelete(row)">删除</AppButton>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无题目数据" />
      </template>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑题目对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑题目' : '添加题目'"
      width="700px"
      @close="resetForm"
    >
      <el-form :model="questionForm" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="题目内容" prop="questionText">
          <el-input
            v-model="questionForm.questionText"
            type="textarea"
            :rows="4"
            placeholder="请输入题目内容"
          />
        </el-form-item>

        <el-form-item label="题型" prop="questionType">
          <el-radio-group v-model="questionForm.questionType">
            <el-radio value="single_choice">单选题</el-radio>
            <el-radio value="multiple_choice">多选题</el-radio>
            <el-radio value="true_false">判断题</el-radio>
            <el-radio value="short_answer">简答题</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 选项列表（单选/多选） -->
        <template v-if="questionForm.questionType === 'single_choice' || questionForm.questionType === 'multiple_choice'">
          <el-form-item label="选项" prop="options">
            <div class="options-container">
              <div v-for="(option, index) in questionForm.options" :key="index" class="option-item">
                <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
                <el-input v-model="questionForm.options[index]" placeholder="请输入选项内容" />
              </div>
            </div>
          </el-form-item>

          <el-form-item label="正确答案" prop="correctAnswer">
            <el-checkbox-group v-if="questionForm.questionType === 'multiple_choice'" v-model="selectedAnswers">
              <el-checkbox v-for="(_, index) in questionForm.options" :key="index" :value="String.fromCharCode(65 + index)">
                {{ String.fromCharCode(65 + index) }}
              </el-checkbox>
            </el-checkbox-group>
            <el-radio-group v-else v-model="questionForm.correctAnswer">
              <el-radio v-for="(_, index) in questionForm.options" :key="index" :value="String.fromCharCode(65 + index)">
                {{ String.fromCharCode(65 + index) }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </template>

        <!-- 判断题 -->
        <template v-if="questionForm.questionType === 'true_false'">
          <el-form-item label="正确答案" prop="correctAnswer">
            <el-radio-group v-model="questionForm.correctAnswer">
              <el-radio value="对">对</el-radio>
              <el-radio value="错">错</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>

        <!-- 简答题 -->
        <template v-if="questionForm.questionType === 'short_answer'">
          <el-form-item label="评分标准">
            <el-input
              v-model="questionForm.correctAnswer"
              type="textarea"
              :rows="4"
              placeholder="请输入评分标准（可选）"
            />
          </el-form-item>
        </template>

        <el-form-item label="分值" prop="score">
          <el-input-number v-model="questionForm.score" :min="1" :max="100" />
        </el-form-item>
      </el-form>

      <template #footer>
        <AppButton variant="outline" @click="dialogVisible = false">取消</AppButton>
        <AppButton variant="primary" @click="confirmSubmit" :loading="submitting">确定</AppButton>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getQuestions, createQuestion, updateQuestion, deleteQuestion, getQuestionStats } from '@/api/question'
import type { Question, CreateQuestionRequest, QuestionType } from '@/types/question'
import AppButton from '@/components/common/AppButton.vue'

// 搜索表单
const searchForm = reactive({
  type: '',
  keyword: '',
})

// 题目列表
const questionList = ref<Question[]>([])
const loading = ref(false)
const searching = ref(false)

// 题目统计
const questionStats = ref({
  total: 0,
  single_choice: 0,
  multiple_choice: 0,
  true_false: 0,
  short_answer: 0,
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

// 题目表单
const questionForm = reactive({
  id: 0,
  questionText: '',
  questionType: 'single_choice' as QuestionType,
  options: ['', '', '', ''],
  correctAnswer: '',
  score: 10,
})

// 多选题选中的答案
const selectedAnswers = ref<string[]>([])

// 表单验证规则
const formRules: FormRules = {
  questionText: [
    { required: true, message: '请输入题目内容', trigger: 'blur' },
    { max: 2000, message: '题目内容不能超过2000字', trigger: 'blur' },
  ],
  questionType: [
    { required: true, message: '请选择题型', trigger: 'change' },
  ],
  score: [
    { required: true, message: '请输入分值', trigger: 'blur' },
  ],
}

// 根据题型获取初始选项
function getInitialOptionsByType(type: QuestionType): string[] {
  switch (type) {
    case 'single_choice':
    case 'multiple_choice':
      return ['', '', '', '']
    case 'true_false':
    case 'short_answer':
      return []
    default:
      return []
  }
}

// 监听题型变化，重置选项和答案
watch(() => questionForm.questionType, (newType) => {
  questionForm.options = getInitialOptionsByType(newType)
  questionForm.correctAnswer = ''
  selectedAnswers.value = []
})

// 监听多选题答案变化
watch(selectedAnswers, (newVal) => {
  // 使用扩展运算符创建新数组，避免原地排序修改原数组
  questionForm.correctAnswer = [...newVal].sort().join('')
})

/**
 * 获取题目统计信息
 */
async function fetchQuestionStats() {
  try {
    const stats = await getQuestionStats()
    questionStats.value = {
      total: stats.total || 0,
      single_choice: stats.single_choice || 0,
      multiple_choice: stats.multiple_choice || 0,
      true_false: stats.true_false || 0,
      short_answer: stats.short_answer || 0,
    }
  } catch (error: any) {
    console.error('获取题目统计失败:', error)
  }
}

/**
 * 获取题目列表
 */
async function fetchQuestionList() {
  loading.value = true
  try {
    const res = await getQuestions({
      current: pagination.current,
      size: pagination.size,
      type: searchForm.type || undefined,
      keyword: searchForm.keyword || undefined,
    })
    questionList.value = res.records
    pagination.total = res.total
  } catch (error: any) {
    ElMessage.error(error.message || '获取题目列表失败')
  } finally {
    loading.value = false
    searching.value = false
  }
}

/**
 * 从分页结果更新统计信息
 */
function updateStatsFromPage(records: Question[], total: number) {
  const stats = {
    total: total,
    single_choice: 0,
    multiple_choice: 0,
    true_false: 0,
    short_answer: 0,
  }
  
  records.forEach(q => {
    if (q.questionType === 'single_choice') stats.single_choice++
    else if (q.questionType === 'multiple_choice') stats.multiple_choice++
    else if (q.questionType === 'true_false') stats.true_false++
    else if (q.questionType === 'short_answer') stats.short_answer++
  })
  
  questionStats.value = stats
}

/**
 * 搜索
 */
function handleSearch() {
  pagination.current = 1
  searching.value = true
  // 搜索时重置统计信息
  questionStats.value = {
    total: 0,
    single_choice: 0,
    multiple_choice: 0,
    true_false: 0,
    short_answer: 0,
  }
  fetchQuestionList()
}

/**
 * 分页大小变化
 */
function handleSizeChange() {
  pagination.current = 1
  fetchQuestionList()
}

/**
 * 当前页变化
 */
function handleCurrentChange() {
  fetchQuestionList()
}

/**
 * 添加题目
 */
function handleAdd() {
  isEdit.value = false
  dialogVisible.value = true
}

/**
 * 编辑题目
 */
function handleEdit(row: Question) {
  isEdit.value = true
  questionForm.id = row.id
  questionForm.questionText = row.questionText
  questionForm.questionType = row.questionType
  questionForm.options = row.options ? [...row.options] : []
  questionForm.correctAnswer = row.correctAnswer
  questionForm.score = row.score

  // 如果是多选题，解析答案
  if (row.questionType === 'multiple_choice' && row.correctAnswer) {
    selectedAnswers.value = row.correctAnswer.split('')
  }

  dialogVisible.value = true
}

/**
 * 删除题目
 */
async function handleDelete(row: Question) {
  try {
    await ElMessageBox.confirm('确定要删除这道题目吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await deleteQuestion(row.id)
    ElMessage.success('题目已删除')
    fetchQuestionList()
    fetchQuestionStats()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除题目失败')
    }
  }
}

/**
 * 确认提交
 */
async function confirmSubmit() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    submitting.value = true
    
    // 验证选项
    if (questionForm.questionType === 'single_choice' || questionForm.questionType === 'multiple_choice') {
      if (questionForm.options.some(opt => !opt.trim())) {
        ElMessage.warning('请填写所有选项内容')
        return
      }
    }

    // 验证答案
    if (!questionForm.correctAnswer) {
      ElMessage.warning('请设置正确答案')
      return
    }

    const data: CreateQuestionRequest = {
      questionText: questionForm.questionText,
      questionType: questionForm.questionType,
      options: questionForm.options.filter(opt => opt.trim()),
      correctAnswer: questionForm.correctAnswer,
      score: questionForm.score,
    }

    if (isEdit.value) {
      await updateQuestion(questionForm.id, data)
      ElMessage.success('题目已更新')
    } else {
      await createQuestion(data)
      ElMessage.success('题目已创建')
    }

    dialogVisible.value = false
    fetchQuestionList()
    fetchQuestionStats()
  } catch (error: any) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    submitting.value = false
  }
}

/**
 * 重置表单
 */
function resetForm() {
  questionForm.id = 0
  questionForm.questionText = ''
  questionForm.questionType = 'single_choice'
  questionForm.options = ['', '', '', '']
  questionForm.correctAnswer = ''
  questionForm.score = 10
  selectedAnswers.value = []
  formRef.value?.resetFields()
}

/**
 * 获取题型标签类型
 */
function getQuestionTypeTag(type: QuestionType): 'primary' | 'success' | 'warning' | 'danger' | 'info' {
  switch (type) {
    case 'single_choice':
      return 'primary'
    case 'multiple_choice':
      return 'success'
    case 'true_false':
      return 'warning'
    case 'short_answer':
      return 'danger'
    default:
      return 'info'
  }
}

/**
 * 获取题型徽章类名
 */
function getQuestionTypeBadgeClass(type: QuestionType): string {
  const classMap: Record<QuestionType, string> = {
    single_choice: 'badge-primary',
    multiple_choice: 'badge-success',
    true_false: 'badge-warning',
    short_answer: 'badge-danger',
  }
  return classMap[type] || 'badge-info'
}

/**
 * 获取题型标签文本
 */
function getQuestionTypeLabel(type: QuestionType): string {
  const labels: Record<QuestionType, string> = {
    single_choice: '单选题',
    multiple_choice: '多选题',
    true_false: '判断题',
    short_answer: '简答题',
  }
  return labels[type] || type
}

/**
 * 格式化日期
 */
function formatDate(dateStr: string): string {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

onMounted(() => {
  fetchQuestionList()
  fetchQuestionStats()
})
</script>

<style scoped>
.questions-container {
  max-width: var(--max-width-wide);
  margin: 0 auto;
  padding: var(--spacing-2xl) var(--spacing-base);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.page-title {
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

.badge-info {
  background-color: var(--color-neutral-500);
}

.badge-primary {
  background-color: var(--color-role-member);
}

.badge-success {
  background-color: var(--color-status-published);
}

.badge-warning {
  background-color: var(--color-role-admin);
}

.badge-danger {
  background-color: var(--color-status-rejected);
}

.stats-bar {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
  flex-wrap: wrap;
}

.filter-bar {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
  align-items: center;
}

.filter-select {
  width: 150px;
}

.search-input {
  width: 300px;
}

.question-text {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.6;
  color: var(--color-text-primary);
}

.pagination-container {
  margin-top: var(--spacing-xl);
  display: flex;
  justify-content: flex-end;
}

.options-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  width: 100%;
}

.option-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.option-label {
  font-weight: var(--font-weight-semibold);
  min-width: 24px;
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
  .questions-container {
    padding: var(--spacing-xl) var(--spacing-sm);
  }

  .page-header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: flex-start;
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-select,
  .search-input {
    width: 100%;
  }

  .stats-bar {
    justify-content: center;
  }
}
</style>
