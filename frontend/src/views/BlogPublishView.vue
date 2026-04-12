<template>
  <div class="blog-publish-container">
    <div class="page-header animate-fade-in-up">
      <h1>发布博客</h1>
      <el-button @click="handleCancel">取消</el-button>
    </div>

    <el-card class="publish-card animate-fade-in-up" shadow="never">
      <el-form ref="formRef" :model="formData" label-width="80px" class="publish-form">
        <!-- 标题 -->
        <el-form-item label="标题" required>
          <el-input
            v-model="formData.title"
            placeholder="请输入博客标题（5-100字符）"
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
            placeholder="简要描述博客内容（最多200字符，可自动生成）"
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
            :initial-content="formData.content"
            :show-footer="false"
            @change="handleContentChange"
          />
          <div v-if="errors.content" class="error-message">{{ errors.content }}</div>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <div class="form-actions">
            <el-button @click="handleCancel">取消</el-button>
            <el-button type="primary" :loading="submitting" @click="handleSubmit">
              发布博客
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Loading } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import BlogEditor from '@/components/editor/BlogEditor.vue'
import { publishContent } from '@/api/content'
import { uploadImage } from '@/api/upload'

const router = useRouter()

// 表单引用
const formRef = ref<FormInstance>()
const editorRef = ref()
const inputRef = ref()

// 表单数据
const formData = reactive({
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  tags: [] as string[]
})

// 错误信息
const errors = reactive({
  title: '',
  content: '',
  summary: '',
  tags: ''
})

// 状态
const submitting = ref(false)
const uploadingCover = ref(false)
const inputVisible = ref(false)
const inputValue = ref('')

// 防抖定时器
let summaryDebounceTimer: number | null = null

/**
 * 生成摘要（从内容中提取纯文本前200字符）
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
 * 处理内容变化（带防抖的摘要生成）
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
 * 显示标签输入框
 */
function showInput() {
  inputVisible.value = true
  nextTick(() => {
    inputRef.value?.focus()
  })
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
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  return true
}

/**
 * 自定义封面图上传处理
 */
async function handleCoverUpload(options: { file: File }) {
  const { file } = options
  uploadingCover.value = true
  
  try {
    const url = await uploadImage(file)
    formData.coverImage = url
    ElMessage.success('封面图上传成功')
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '上传失败'
    ElMessage.error(message)
  } finally {
    uploadingCover.value = false
  }
}

/**
 * 表单验证
 */
function validateForm(): boolean {
  let isValid = true
  
  // 清空错误
  errors.title = ''
  errors.content = ''
  errors.summary = ''
  errors.tags = ''
  
  // 验证标题
  if (!formData.title.trim()) {
    errors.title = '请输入标题'
    isValid = false
  } else if (formData.title.length < 5) {
    errors.title = '标题至少5个字符'
    isValid = false
  } else if (formData.title.length > 100) {
    errors.title = '标题最多100个字符'
    isValid = false
  }
  
  // 验证内容
  if (!formData.content.trim()) {
    errors.content = '请输入正文内容'
    isValid = false
  }
  
  // 验证摘要
  if (formData.summary.length > 200) {
    errors.summary = '摘要最多200个字符'
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
 * 提交发布
 */
async function handleSubmit() {
  if (!validateForm()) {
    ElMessage.warning('请修正表单错误')
    return
  }
  
  submitting.value = true
  
  try {
    await publishContent({
      title: formData.title,
      summary: formData.summary || generateSummary(formData.content),
      content: formData.content,
      type: 'blog',
      coverImage: formData.coverImage || undefined,
      tags: formData.tags.length > 0 ? formData.tags : undefined
    })
    
    ElMessage.success('发布成功，等待审核')
    router.push('/profile')
  } catch (error: unknown) {
    console.error('Publish failed:', error)
    const message = error instanceof Error ? error.message : '发布失败，请重试'
    ElMessage.error(message)
  } finally {
    submitting.value = false
  }
}

/**
 * 取消
 */
function handleCancel() {
  router.back()
}
</script>

<style scoped>
.blog-publish-container {
  min-height: 100vh;
  padding: var(--spacing-3xl) var(--spacing-base);
  background-color: var(--color-background);
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
  color: var(--color-text-primary);
  margin: 0;
  font-weight: var(--font-weight-bold);
}

.publish-card {
  max-width: 1200px;
  margin: 0 auto;
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
}

.publish-form {
  padding: var(--spacing-lg) 0;
}

/* 正文编辑器全宽 */
.publish-form :deep(.el-form-item__content) {
  width: 100%;
}

/* 确保编辑器组件占满容器 */
.publish-form :deep(.blog-editor) {
  width: 100%;
  max-width: none;
}

/* 封面图上传 */
.cover-uploader {
  border: 2px dashed var(--color-border);
  border-radius: var(--radius-lg);
  width: 300px;
  height: 168px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--transition-base);
  overflow: hidden;
}

.cover-uploader:hover {
  border-color: var(--color-primary-500);
}

.upload-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  color: var(--color-text-secondary);
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-uploader-icon {
  font-size: 48px;
  color: var(--color-text-muted);
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
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}

.form-tip {
  margin-top: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

/* 错误提示 */
.error-message {
  color: var(--color-error);
  font-size: var(--font-size-sm);
  margin-top: var(--spacing-xs);
}

.is-error :deep(.el-input__wrapper),
.is-error :deep(.el-textarea__inner) {
  box-shadow: 0 0 0 1px var(--color-error) inset;
}

/* 操作按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--color-neutral-100);
}

/* 响应式 */
@media (max-width: 768px) {
  .blog-publish-container {
    padding: var(--spacing-xl) var(--spacing-sm);
  }

  .page-header h1 {
    font-size: var(--font-size-2xl);
  }

  .cover-uploader {
    width: 100%;
    max-width: 300px;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions .el-button {
    width: 100%;
  }
}
</style>
