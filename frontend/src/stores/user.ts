import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 预留实现
  const userId = ref<number | null>(null)
  const username = ref('')
  const email = ref('')
  const role = ref('')

  return {
    userId,
    username,
    email,
    role
  }
})
