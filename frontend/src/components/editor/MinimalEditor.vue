<template>
  <div class="minimal-editor">
    <h2>最小化编辑器测试</h2>
    <p v-if="!editor">编辑器加载中...</p>
    <div v-else>
      <editor-content :editor="editor" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount } from 'vue'
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'

const editor = useEditor({
  content: '<p>Hello World!</p>',
  extensions: [StarterKit],
})

onBeforeUnmount(() => {
  if (editor.value) {
    editor.value.destroy()
  }
})
</script>

<style scoped>
.minimal-editor {
  padding: 20px;
}
</style>
