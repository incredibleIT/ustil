import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { IReviewQueueItem, IReviewVote } from '@/types'

export const useReviewStore = defineStore('review', () => {
  // 预留实现
  const reviewQueue = ref<IReviewQueueItem[]>([])
  const myVotes = ref<IReviewVote[]>([])

  return {
    reviewQueue,
    myVotes
  }
})
