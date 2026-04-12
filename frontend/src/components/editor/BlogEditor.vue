<template>
  <div class="blog-editor">
    <!-- 标题输入区 -->
    <div class="editor-header">
      <input
        v-model="title"
        type="text"
        class="title-input"
        placeholder="请输入文章标题（5~100个字）"
        maxlength="100"
      />
      <div class="title-meta">
        <span class="char-count">{{ title.length }}/100</span>
      </div>
    </div>

    <!-- 工具栏 -->
    <div class="editor-toolbar">
      <button
        v-for="btn in toolbarButtons"
        :key="btn.action"
        class="toolbar-btn"
        :title="btn.tooltip"
        @click="btn.handler"
      >
        <span class="btn-icon">{{ btn.icon }}</span>
        <span class="btn-label">{{ btn.label }}</span>
      </button>
    </div>

    <!-- 左右分屏编辑区 -->
    <div class="editor-body">
      <!-- 左侧：Markdown 编辑区 -->
      <div class="editor-pane">
        <textarea
          ref="editorRef"
          v-model="markdownContent"
          class="markdown-editor"
          placeholder="# 开始编写 Markdown..."
          @input="handleInput"
        ></textarea>
      </div>

      <!-- 右侧：实时预览区 -->
      <div class="preview-pane">
        <div v-html="renderedHtml" class="markdown-preview"></div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div v-if="showFooter !== false" class="editor-footer">
      <div class="footer-left">
        <span class="word-count">共 {{ wordCount }} 字</span>
      </div>
      <div class="footer-right">
        <button class="btn-publish" @click="handlePublish">发布文章</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

const props = defineProps<{
  initialTitle?: string
  initialContent?: string
  showFooter?: boolean  // 是否显示底部操作栏，默认 true
}>()

const emit = defineEmits<{
  (e: 'submit', data: { title: string; content: string }): void
  (e: 'change', content: string): void  // 内容变化事件
  (e: 'update:title', title: string): void  // 标题变化事件
}>()

const title = ref(props.initialTitle || '')
const markdownContent = ref(props.initialContent || '')
const editorRef = ref<HTMLTextAreaElement>()

// 渲染后的 HTML
const renderedHtml = computed(() => {
  if (!markdownContent.value.trim()) {
    return '<p style="color: #999; text-align: center; padding: 40px;">开始编辑以查看预览效果</p>'
  }
  
  try {
    // 使用 marked.parse 同步解析
    const html = marked.parse(markdownContent.value, { async: false })
    
    // 确保返回的是字符串
    if (typeof html !== 'string') {
      console.error('marked.parse returned non-string:', html)
      return '<p style="color: red;">解析错误</p>'
    }
    
    console.log('Raw HTML:', html)
    
    // 手动处理代码块高亮
    // marked v18 格式: <pre><code class="language-javascript">code</code></pre>
    const processedHtml = html.replace(
      /<pre><code(?:\s+class="language-([\w-]+)")?>([\s\S]*?)<\/code><\/pre>/g,
      (match, lang, code) => {
        const language = lang || ''
        console.log('Found code block, lang:', language)
        
        let highlighted = code
        
        // 解码 HTML 实体
        const decoded = code
          .replace(/&amp;/g, '&')
          .replace(/&lt;/g, '<')
          .replace(/&gt;/g, '>')
          .replace(/&quot;/g, '"')
          .replace(/&#39;/g, "'")
        
        // 应用语法高亮
        if (language && hljs.getLanguage(language)) {
          try {
            const result = hljs.highlight(decoded, { language })
            highlighted = result.value
            console.log('Highlighted successfully')
          } catch (e) {
            console.error('Highlight error:', e)
            highlighted = code
          }
        } else if (language) {
          console.log('Language not supported:', language)
        }
        
        return `<pre><code class="hljs language-${language}">${highlighted}</code></pre>`
      }
    )
    
    console.log('Processed HTML sample:', processedHtml.substring(0, 500))
    return processedHtml
  } catch (error) {
    console.error('Markdown parse error:', error)
    return '<p style="color: red;">解析错误</p>'
  }
})

// 字数统计
const wordCount = computed(() => {
  return markdownContent.value.trim().length
})

// 工具栏按钮
const toolbarButtons = [
  {
    action: 'h1',
    icon: 'H1',
    label: '标题1',
    tooltip: '插入一级标题',
    handler: () => insertMarkdown('# ')
  },
  {
    action: 'h2',
    icon: 'H2',
    label: '标题2',
    tooltip: '插入二级标题',
    handler: () => insertMarkdown('## ')
  },
  {
    action: 'bold',
    icon: 'B',
    label: '加粗',
    tooltip: '加粗 (Ctrl+B)',
    handler: () => insertMarkdown('**', '**')
  },
  {
    action: 'italic',
    icon: 'I',
    label: '斜体',
    tooltip: '斜体 (Ctrl+I)',
    handler: () => insertMarkdown('*', '*')
  },
  {
    action: 'list',
    icon: '•',
    label: '列表',
    tooltip: '插入无序列表',
    handler: () => insertMarkdown('- ')
  },
  {
    action: 'code',
    icon: '</>',
    label: '代码',
    tooltip: '插入代码块',
    handler: () => insertMarkdown('\n```\n', '\n```\n')
  },
  {
    action: 'quote',
    icon: '"',
    label: '引用',
    tooltip: '插入引用',
    handler: () => insertMarkdown('> ')
  },
  {
    action: 'link',
    icon: '🔗',
    label: '链接',
    tooltip: '插入链接',
    handler: () => insertMarkdown('[', '](url)')
  },
  {
    action: 'image',
    icon: '🖼️',
    label: '图片',
    tooltip: '插入图片',
    handler: () => insertMarkdown('![alt](', ')')
  }
]

// 插入 Markdown 语法
function insertMarkdown(before: string, after: string = '') {
  if (!editorRef.value) return
  
  const textarea = editorRef.value
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = markdownContent.value.substring(start, end)
  
  const newText = markdownContent.value.substring(0, start) + 
                  before + selectedText + after + 
                  markdownContent.value.substring(end)
  
  markdownContent.value = newText
  
  // 恢复光标位置
  setTimeout(() => {
    textarea.focus()
    textarea.setSelectionRange(
      start + before.length,
      end + before.length
    )
  }, 0)
}

// 输入处理
function handleInput() {
  // 触发内容变化事件
  emit('change', markdownContent.value)
}

// 监听标题变化，emit 给父组件
watch(title, (newTitle) => {
  emit('update:title', newTitle)
})

// 发布文章
function handlePublish() {
  if (title.value.length < 5) {
    ElMessage.warning('标题至少需要 5 个字')
    return
  }

  if (!markdownContent.value.trim()) {
    ElMessage.warning('请输入文章内容')
    return
  }

  emit('submit', {
    title: title.value,
    content: markdownContent.value
  })

  ElMessage.success('发布成功！')
}

onMounted(() => {
  // 聚焦编辑器
  if (editorRef.value) {
    editorRef.value.focus()
  }
})
</script>

<style>
/* 全局样式 - highlight.js 主题 */
@import 'highlight.js/styles/github.css';
</style>

<style scoped>
.blog-editor {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.editor-header {
  padding: 24px 32px 16px;
  border-bottom: 1px solid #e8e8e8;
}

.title-input {
  width: 100%;
  font-size: 24px;
  font-weight: 600;
  border: none;
  outline: none;
  padding: 8px 0;
  color: #333;
}

.title-input::placeholder {
  color: #999;
}

.title-meta {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

.editor-toolbar {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px 16px;
  background-color: #fafafa;
  border-bottom: 1px solid #e8e8e8;
  flex-wrap: wrap;
}

.toolbar-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border: 1px solid transparent;
  border-radius: 4px;
  background-color: transparent;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  color: #333;
}

.toolbar-btn:hover {
  background-color: #e8e8e8;
  border-color: #d9d9d9;
}

.toolbar-btn.active {
  background-color: #e6f7ff;
  border-color: #1890ff;
  color: #1890ff;
}

.btn-icon {
  font-weight: 600;
}

.btn-label {
  font-size: 12px;
}

.editor-body {
  flex: 1;
  display: flex;
  gap: 0;
  overflow: hidden;
}

.editor-pane,
.preview-pane {
  flex: 1;
  height: 100%;
  overflow-y: auto;
}

.editor-pane {
  border-right: 1px solid #e8e8e8;
}

.markdown-editor {
  width: 100%;
  height: 100%;
  padding: 24px;
  border: none;
  outline: none;
  resize: none;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.8;
  color: #333;
  background-color: #fafafa;
}

.markdown-editor::placeholder {
  color: #999;
}

.preview-pane {
  background-color: #fff;
  padding: 24px;
}

.markdown-preview {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
}

.markdown-preview :deep(h1) {
  font-size: 28px;
  font-weight: 600;
  margin: 24px 0 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #eaecef;
}

.markdown-preview :deep(h2) {
  font-size: 24px;
  font-weight: 600;
  margin: 20px 0 12px;
  padding-bottom: 6px;
  border-bottom: 1px solid #eaecef;
}

.markdown-preview :deep(h3) {
  font-size: 20px;
  font-weight: 600;
  margin: 16px 0 8px;
}

.markdown-preview :deep(p) {
  margin: 12px 0;
}

.markdown-preview :deep(pre) {
  background-color: #f6f8fa;
  border-radius: 6px;
  padding: 16px;
  overflow-x: auto;
  margin: 16px 0;
}

.markdown-preview :deep(code) {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  background-color: rgba(27, 31, 35, 0.05);
  padding: 2px 6px;
  border-radius: 3px;
}

.markdown-preview :deep(pre code) {
  background-color: transparent;
  padding: 0;
}

.markdown-preview :deep(blockquote) {
  border-left: 4px solid #1890ff;
  padding-left: 16px;
  margin: 16px 0;
  color: #666;
  background-color: #f9f9f9;
  padding: 12px 16px;
}

.markdown-preview :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 16px 0;
}

.markdown-preview :deep(a) {
  color: #1890ff;
  text-decoration: none;
}

.markdown-preview :deep(a:hover) {
  text-decoration: underline;
}

.markdown-preview :deep(ul),
.markdown-preview :deep(ol) {
  margin: 16px 0;
  padding-left: 32px;
}

.markdown-preview :deep(ul) {
  list-style-type: disc;
}

.markdown-preview :deep(ol) {
  list-style-type: decimal;
}

.markdown-preview :deep(li) {
  margin: 8px 0;
  line-height: 1.8;
}

.markdown-preview :deep(ul li)::marker {
  color: #666;
}

.markdown-preview :deep(ol li)::marker {
  color: #666;
  font-weight: 500;
}

.editor-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-top: 1px solid #e8e8e8;
  background-color: #fafafa;
}

.footer-left {
  display: flex;
  gap: 16px;
}

.word-count {
  font-size: 14px;
  color: #666;
}

.footer-right {
  display: flex;
  gap: 12px;
}

.btn-publish {
  padding: 8px 20px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  background-color: #fc5531;
  color: #fff;
}

.btn-publish:hover {
  background-color: #e64a28;
}
</style>
