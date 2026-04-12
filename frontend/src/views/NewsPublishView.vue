<template>
  <div class="news-publish-page">
    <h1>发布资讯</h1>
    
    <!-- 封面图上传 -->
    <div class="cover-upload">
      <el-upload
        class="cover-uploader"
        action="#"
        :show-file-list="false"
        :http-request="handleCoverUpload"
        :before-upload="beforeCoverUpload"
        :disabled="uploadingCover"
      >
        <div v-if="uploadingCover" class="upload-loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <p>上传中...</p>
        </div>
        <img v-else-if="coverImage" :src="coverImage" class="cover-image" />
        <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
      </el-upload>
      <p class="upload-tip">支持 jpg/png/gif/webp，最大 5MB</p>
    </div>
    
    <!-- 标题输入 -->
    <el-input
      v-model="title"
      placeholder="请输入资讯标题（5-100个字）"
      maxlength="100"
      show-word-limit
      class="title-input"
    />
    
    <!-- 摘要输入 -->
    <el-input
      v-model="summary"
      type="textarea"
      :rows="3"
      placeholder="摘要（选填，最多200字，不填则自动生成）"
      maxlength="200"
      show-word-limit
      class="summary-input"
    />
    
    <!-- Markdown 编辑器 -->
    <BlogEditor
      ref="editorRef"
      :initial-content="content"
      :show-footer="false"
      @change="handleContentChange"
    />
    
    <!-- 发布按钮 -->
    <div class="actions">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        发布资讯
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Loading } from '@element-plus/icons-vue'
import BlogEditor from '@/components/editor/BlogEditor.vue'
import { publishContent } from '@/api/content'
import { uploadImage } from '@/api/upload'

const router = useRouter()
const editorRef = ref()

const title = ref('')
const summary = ref('')
const content = ref('')
const coverImage = ref('')
const submitting = ref(false)
const uploadingCover = ref(false)

// 防抖定时器
let summaryDebounceTimer: number | null = null

/**
 * 自动生成摘要
 * @param text Markdown 文本
 * @returns 纯文本摘要（前 200 字符）
 */
function generateSummary(text: string): string {
  let plainText = text
    // 移除代码块
    .replace(/```[\s\S]*?```/g, '')
    // 移除行内代码
    .replace(/`[^`]+`/g, '')
    // 移除图片
    .replace(/!\[[^\]]*\]\([^)]+\)/g, '')
    // 移除链接，保留文本
    .replace(/\[([^\]]+)\]\([^)]+\)/g, '$1')
    // 移除标题标记
    .replace(/^#+\s*/gm, '')
    // 移除粗体/斜体
    .replace(/[*_]{1,3}/g, '')
    // 移除引用标记
    .replace(/^>\s*/gm, '')
    // 移除列表标记
    .replace(/^\s*[-*+]\s+/gm, '')
    .replace(/^\s*\d+\.\s+/gm, '')
    // 移除水平线
    .replace(/^\s*[-*_]{3,}\s*$/gm, '')
    // 合并多余空白
    .replace(/\n+/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
  
  return plainText.substring(0, 200)
}

/**
 * 监听内容变化，自动生成摘要（带防抖）
 * @param newContent 新的内容
 */
function handleContentChange(newContent: string) {
  content.value = newContent
  
  // 清除之前的定时器
  if (summaryDebounceTimer) {
    clearTimeout(summaryDebounceTimer)
  }
  
  // 如果用户没有手动编辑摘要，则延迟 500ms 后自动生成
  if (!summary.value.trim()) {
    summaryDebounceTimer = window.setTimeout(() => {
      summary.value = generateSummary(newContent)
    }, 500)
  }
}

/**
 * 封面图上传前验证
 * @param file 文件对象
 * @returns 是否允许上传
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
 * @param options 上传选项
 */
async function handleCoverUpload(options: { file: File }) {
  const { file } = options
  uploadingCover.value = true
  
  try {
    const url = await uploadImage(file)
    coverImage.value = url
    ElMessage.success('封面图上传成功')
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '上传失败'
    ElMessage.error(message)
  } finally {
    uploadingCover.value = false
  }
}

/**
 * 提交发布
 */
async function handleSubmit() {
  // 表单验证
  if (title.value.length < 5 || title.value.length > 100) {
    ElMessage.warning('标题长度为 5-100 个字符')
    return
  }
  if (!content.value.trim()) {
    ElMessage.warning('请输入内容')
    return
  }
  if (summary.value.length > 200) {
    ElMessage.warning('摘要最多 200 个字符')
    return
  }
  
  submitting.value = true
  
  try {
    const response = await publishContent({
      title: title.value,
      summary: summary.value || generateSummary(content.value),
      content: content.value,
      type: 'news',
      coverImage: coverImage.value || undefined
    })
    
    // 响应拦截器已处理成功/失败，能到达这里说明成功
    ElMessage.success('发布成功，等待审核')
    router.push('/profile') // 跳转到个人中心
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
.news-publish-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

h1 {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-lg);
}

.cover-upload {
  margin-bottom: 24px;
}

.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 300px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
  transition: all 0.3s;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.upload-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #409eff;
}

.upload-loading p {
  font-size: 12px;
  margin: 0;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.title-input {
  margin-bottom: 16px;
}

.summary-input {
  margin-bottom: 24px;
}

.actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
