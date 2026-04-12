<template>
  <div class="content-edit-container">
    <div class="page-header animate-fade-in-up">
      <h1>{{ pageTitle }}</h1>
      <el-button @click="handleCancel">取消</el-button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <el-card v-else class="edit-card animate-fade-in-up" shadow="never">
      <el-form ref="formRef" :model="formData" label-width="80px" class="edit-form">
        <!-- 标题 -->
        <el-form-item label="标题" required>
          <el-input
            v-model="formData.title"
            placeholder="请输入标题（5-100字符）"
            maxlength="100"
            show-word-limit
            :class="{ 'is-error': errors.title }"
          />
          <div v-if="errors.title" class="error-message">{{ errors.title }}</div>
        </el-form-item>

        <!-- 封面图 -->
        <el-form-item label="封面图">
          <el-upload
            class="cover-uploader"
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            :before-upload="beforeCoverUpload"
            :http-request="handleCoverUpload"
            accept="image/*"
            :disabled="uploadingCover"
          >
            <div v-if="uploadingCover" class="upload-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <p>上传中...</p>
            </div>
            <img v-else-if="formData.coverImage" :src="formData.coverImage" class="cover-image" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <p class="upload-tip">可选，建议尺寸 1200x630，最大 5MB</p>
        </el-form-item>

        <!-- 标签 -->
        <el-form-item label="标签">
          <div class="tags-container">
            <el-tag
              v-for="tag in formData.tags"
              :key="tag"
              closable
              :disable-transitions="false"
              @close="removeTag(tag)"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
            <el-input
              v-if="inputVisible"
              ref="inputRef"
              v-model="inputValue"
              class="tag-input"
              size="small"
              @keyup.enter="handleInputConfirm"
              @blur="handleInputConfirm"
              :maxlength="20"
            />
            <el-button
              v-else
              class="button-new-tag"
              size="small"
              @click="showInput"
              :disabled="formData.tags.length >= 5"
            >
              + 添加标签
            </el-button>
          </div>
          <p class="form-tip">最多5个标签，按回车添加，每个标签最多20字符</p>
          <div v-if="errors.tags" class="error-message">{{ errors.tags }}</div>
        </el-form-item>

        <!-- 摘要 -->
        <el-form-item label="摘要">
          <el-input
            v-model="formData.summary"
            type="textarea"
            :rows="3"
            placeholder="简要描述内容（最多200字符，可自动生成）"
            maxlength="200"
            show-word-limit
            :class="{ 'is-error': errors.summary }"
          />
          <div v-if="errors.summary" class="error-message">{{ errors.summary }}</div>
        </el-form-item>

        <!-- Markdown 编辑器 -->
        <el-form-item label="正文" required>
          <BlogEditor
            ref="editorRef"
            :initial-title="formData.title"
            :initial-content="formData.content"
            :show-footer="false"
            @change="handleContentChange"
            @update:title="(newTitle: string) => { formData.title = newTitle }"
          />
          <div v-if="errors.content" class="error-message">{{ errors.content }}</div>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting" size="large">
            保存修改
          </el-button>
          <el-button @click="handleCancel" size="large">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Loading } from '@element-plus/icons-vue'
import BlogEditor from '@/components/editor/BlogEditor.vue'
import { getContentById, updateContent, type UpdateContentRequest } from '@/api/content'
import { uploadImage } from '@/api/upload'

const router = useRouter()
const route = useRoute()

// 页面标题
const pageTitle = computed(() => {
  return formData.type === 'news' ? '编辑资讯' : '编辑博客'
})

// 表单数据
const formData = reactive({
  title: '',
  summary: '',
  content: '',
  type: 'blog' as 'news' | 'blog',
  coverImage: '',
  tags: [] as string[]
})

// 错误信息
const errors = reactive({
  title: '',
  summary: '',
  content: '',
  tags: ''
})

// 标签输入
const inputVisible = ref(false)
const inputValue = ref('')
const inputRef = ref()

// 上传状态
const uploadingCover = ref(false)

// 提交状态
const submitting = ref(false)

// 加载状态
const loading = ref(false)

// 编辑器引用
const editorRef = ref()

// 防抖定时器
let summaryDebounceTimer: number | null = null

/**
 * 加载内容数据
 */
async function loadContent() {
  const contentId = Number(route.params.id)
  
  if (!contentId) {
    ElMessage.error('无效的内容 ID')
    router.push('/profile')
    return
  }
  
  loading.value = true
  try {
    const content = await getContentById(contentId)
    
    // 填充表单
    formData.title = content.title
    formData.summary = content.summary || ''
    formData.content = content.content
    formData.type = content.type
    formData.coverImage = content.coverImage || ''
    formData.tags = content.tags || []
  } catch (error: unknown) {
    console.error('Load content failed:', error)
    const message = error instanceof Error ? error.message : '加载内容失败'
    ElMessage.error(message)
    router.push('/profile')
  } finally {
    loading.value = false
  }
}

/**
 * 处理内容变化
 */
function handleContentChange(newContent: string) {
  formData.content = newContent
  
  // 清除之前的定时器
  if (summaryDebounceTimer) {
    clearTimeout(summaryDebounceTimer)
  }
  
  // 如果用户没有手动编辑摘要，则自动生成
  if (!formData.summary.trim()) {
    summaryDebounceTimer = window.setTimeout(() => {
      formData.summary = generateSummary(newContent)
    }, 500)
  }
}

/**
 * 生成摘要
 */
function generateSummary(text: string): string {
  let plainText = text
    .replace(/```[\s\S]*?```/g, '') // 移除代码块
    .replace(/`[^`]+`/g, '') // 移除行内代码
    .replace(/!\[[^\]]*\]\([^)]+\)/g, '') // 移除图片
    .replace(/\[([^\]]+)\]\([^)]+\)/g, '$1') // 移除链接保留文本
    .replace(/^#+\s*/gm, '') // 移除标题标记
    .replace(/[*_]{1,3}/g, '') // 移除粗体/斜体
    .replace(/^>\s*/gm, '') // 移除引用标记
    .replace(/^\s*[-*+]\s+/gm, '') // 移除无序列表
    .replace(/^\s*\d+\.\s+/gm, '') // 移除有序列表
    .replace(/^\s*[-*_]{3,}\s*$/gm, '') // 移除水平线
    .replace(/\n+/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
  
  return plainText.substring(0, 200)
}

/**
 * 显示标签输入框
 */
function showInput() {
  inputVisible.value = true
  setTimeout(() => {
    inputRef.value?.focus()
  }, 0)
}

/**
 * 确认添加标签
 */
function handleInputConfirm() {
  const value = inputValue.value.trim()
  
  if (value) {
    // 验证标签长度
    if (value.length > 20) {
      ElMessage.warning('标签长度不能超过20字符')
      return
    }
    
    // 检查是否已存在
    if (formData.tags.includes(value)) {
      ElMessage.warning('标签已存在')
      return
    }
    
    // 检查数量限制
    if (formData.tags.length >= 5) {
      ElMessage.warning('最多只能添加5个标签')
      return
    }
    
    formData.tags.push(value)
    errors.tags = ''
  }
  
  inputVisible.value = false
  inputValue.value = ''
}

/**
 * 删除标签
 */
function removeTag(tag: string) {
  formData.tags = formData.tags.filter(t => t !== tag)
}

/**
 * 封面图上传前验证
 */
function beforeCoverUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

/**
 * 处理封面图上传
 */
async function handleCoverUpload(options: any) {
  const { file } = options
  uploadingCover.value = true
  
  try {
    const url = await uploadImage(file)
    formData.coverImage = url
    ElMessage.success('上传成功')
  } catch (error: unknown) {
    console.error('Upload failed:', error)
    const message = error instanceof Error ? error.message : '上传失败'
    ElMessage.error(message)
  } finally {
    uploadingCover.value = false
  }
}

/**
 * 验证表单
 */
function validateForm(): boolean {
  let isValid = true
  
  // 重置错误
  errors.title = ''
  errors.summary = ''
  errors.content = ''
  errors.tags = ''
  
  // 验证标题
  if (!formData.title.trim()) {
    errors.title = '标题不能为空'
    isValid = false
  } else if (formData.title.length < 5) {
    errors.title = '标题至少5个字符'
    isValid = false
  } else if (formData.title.length > 100) {
    errors.title = '标题最多100个字符'
    isValid = false
  }
  
  // 验证摘要
  if (formData.summary.length > 200) {
    errors.summary = '摘要最多200个字符'
    isValid = false
  }
  
  // 验证内容
  if (!formData.content.trim()) {
    errors.content = '内容不能为空'
    isValid = false
  } else if (formData.content.length < 10) {
    errors.content = '内容至少10个字符'
    isValid = false
  }
  
  // 验证标签
  if (formData.tags.some(tag => tag.length > 20)) {
    errors.tags = '单个标签长度不能超过20字符'
    isValid = false
  }
  
  return isValid
}

/**
 * 提交表单
 */
async function handleSubmit() {
  if (!validateForm()) {
    ElMessage.warning('请修正表单错误')
    return
  }
  
  submitting.value = true
  
  try {
    const contentId = Number(route.params.id)
    
    const updateData: UpdateContentRequest = {
      title: formData.title,
      summary: formData.summary || undefined,
      content: formData.content,
      coverImage: formData.coverImage || undefined,
      tags: formData.tags.length > 0 ? formData.tags : undefined
    }
    
    await updateContent(contentId, updateData)
    
    ElMessage.success('更新成功，内容重新进入审核')
    router.push('/profile')
  } catch (error: unknown) {
    console.error('Update failed:', error)
    const message = error instanceof Error ? error.message : '更新失败，请重试'
    ElMessage.error(message)
  } finally {
    submitting.value = false
  }
}

/**
 * 取消
 */
function handleCancel() {
  router.push('/profile')
}

// 组件挂载时加载数据
onMounted(() => {
  loadContent()
})
</script>

<style scoped>
.content-edit-container {
  min-height: calc(100vh - 64px);
  padding: var(--spacing-xl);
  background-color: var(--color-bg-secondary);
}

.page-header {
  max-width: 1200px;
  margin: 0 auto var(--spacing-xl);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h1 {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin: 0;
}

.edit-card {
  max-width: 1200px;
  margin: 0 auto;
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
}

.edit-form {
  padding: var(--spacing-lg) 0;
}

/* 正文编辑器全宽 */
.edit-form :deep(.el-form-item__content) {
  width: 100%;
}

/* 确保编辑器组件占满容器 */
.edit-form :deep(.blog-editor) {
  width: 100%;
  max-width: none;
}

/* 封面图上传 */
.cover-uploader {
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--transition-base);
  width: 300px;
  height: 168px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-uploader:hover {
  border-color: var(--color-primary);
}

.cover-uploader-icon {
  font-size: 28px;
  color: var(--color-text-tertiary);
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  color: var(--color-text-secondary);
}

.upload-tip {
  margin-top: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

/* 标签容器 */
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  align-items: center;
}

.tag-item {
  margin: 0;
}

.tag-input {
  width: 120px;
}

.button-new-tag {
  height: 24px;
  line-height: 22px;
  padding: 0 8px;
}

.form-tip {
  margin-top: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

/* 加载状态 */
.loading-container {
  padding: var(--spacing-xl);
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
}

/* 错误提示 */
.error-message {
  color: var(--color-danger);
  font-size: var(--font-size-sm);
  margin-top: var(--spacing-xs);
}

.is-error :deep(.el-input__wrapper),
.is-error :deep(.el-textarea__wrapper) {
  box-shadow: 0 0 0 1px var(--color-danger) inset;
}
</style>
