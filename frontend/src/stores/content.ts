import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { IContent } from '@/types'

export const useContentStore = defineStore('content', () => {
  // 预留实现
  const contents = ref<IContent[]>([])
  const currentContent = ref<IContent | null>(null)

  return {
    contents,
    currentContent
  }
})
