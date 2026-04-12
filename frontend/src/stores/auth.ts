import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, refreshToken as refreshTokenApi } from '@/api/auth'

export interface UserInfo {
  userId: number
  username: string
  roles: string[]
}

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref('')
  const refreshToken = ref('')
  const user = ref<UserInfo | null>(null)

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const currentUser = computed(() => user.value)

  /**
   * 用户登录
   */
  async function login(email: string, password: string, rememberMe: boolean = false) {
    const response = await loginApi({ email, password, rememberMe })
    
    token.value = response.accessToken
    refreshToken.value = response.refreshToken
    user.value = {
      userId: response.userId,
      username: response.username,
      roles: response.roles
    }
    
    // 存储到本地
    if (rememberMe) {
      localStorage.setItem('accessToken', response.accessToken)
      localStorage.setItem('refreshToken', response.refreshToken)
      localStorage.setItem('user', JSON.stringify(user.value))
    } else {
      sessionStorage.setItem('accessToken', response.accessToken)
      sessionStorage.setItem('refreshToken', response.refreshToken)
      sessionStorage.setItem('user', JSON.stringify(user.value))
    }
    
    return response
  }

  /**
   * 刷新 Token
   */
  async function refreshAccessToken() {
    if (!refreshToken.value) {
      throw new Error('No refresh token available')
    }
    
    const response = await refreshTokenApi({ refreshToken: refreshToken.value })
    
    token.value = response.accessToken
    refreshToken.value = response.refreshToken
    
    // 更新本地存储
    const storage = localStorage.getItem('accessToken') ? localStorage : sessionStorage
    storage.setItem('accessToken', response.accessToken)
    storage.setItem('refreshToken', response.refreshToken)
    
    return response
  }

  /**
   * 登出
   */
  function logout() {
    token.value = ''
    refreshToken.value = ''
    user.value = null
    
    // 清除本地存储
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
    sessionStorage.removeItem('accessToken')
    sessionStorage.removeItem('refreshToken')
    sessionStorage.removeItem('user')
  }

  /**
   * 从本地存储恢复登录状态
   */
  function restoreFromStorage() {
    const accessToken = localStorage.getItem('accessToken') || sessionStorage.getItem('accessToken')
    const refresh = localStorage.getItem('refreshToken') || sessionStorage.getItem('refreshToken')
    const userStr = localStorage.getItem('user') || sessionStorage.getItem('user')
    
    if (accessToken && refresh) {
      token.value = accessToken
      refreshToken.value = refresh
      if (userStr) {
        try {
          user.value = JSON.parse(userStr)
        } catch (e) {
          console.error('Failed to parse user from storage', e)
        }
      }
    }
  }

  return {
    token,
    refreshToken,
    user,
    isAuthenticated,
    currentUser,
    login,
    logout,
    refreshAccessToken,
    restoreFromStorage
  }
})
