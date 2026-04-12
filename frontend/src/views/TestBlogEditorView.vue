<template>
  <div class="test-blog-editor">
    <BlogEditor
      :initial-title="initialTitle"
      :initial-content="initialContent"
      @change="handleChange"
      @submit="handleSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import BlogEditor from '@/components/editor/BlogEditor.vue'

const initialTitle = ref('')
const initialContent = ref('')

function handleChange(data: { title: string; content: string }) {
  console.log('Content changed:', data)
}

async function handleSubmit(data: { title: string; content: string }) {
  console.log('Submit article:', data)
  
  // TODO: 调用后端 API 发布文章
  // await publishArticle(data)
  
  ElMessage.success(`文章《${data.title}》发布成功！`)
  
  // 清空表单
  initialTitle.value = ''
  initialContent.value = ''
}
</script>

<style scoped>
.test-blog-editor {
  min-height: 100vh;
  background-color: #f5f6f7;
  padding: 24px;
}
</style>
