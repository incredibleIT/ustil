<template>
  <div style="padding: 40px;">
    <h1>Markdown 编辑器测试（完整版）</h1>
    <div style="margin-top: 20px;">
      <BlogEditor @submit="handleSubmit" />
    </div>
  </div>
</template>

<script setup lang="ts">
import BlogEditor from '@/components/editor/BlogEditor.vue'

function handleSubmit(data: any) {
  console.log('Submitted:', data)
  alert('发布成功！')
}
</script>
