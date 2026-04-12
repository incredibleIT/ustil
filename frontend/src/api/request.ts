import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

// 自定义响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 创建自定义请求类型
interface CustomAxiosInstance {
  get<T = any>(url: string, config?: any): Promise<T>
  post<T = any>(url: string, data?: any, config?: any): Promise<T>
  put<T = any>(url: string, data?: any, config?: any): Promise<T>
  delete<T = any>(url: string, config?: any): Promise<T>
  interceptors: typeof axios.interceptors
}

// 扩展 AxiosRequestConfig 添加 _retry 标记
declare module 'axios' {
  export interface AxiosRequestConfig {
    _retry?: boolean
  }
}

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
}) as unknown as CustomAxiosInstance

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 添加 token 到请求头
    const authStore = useAuthStore()
    const token = authStore.token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 统一响应格式处理
    if (res.code === 200) {
      // 成功响应，返回 data 数据
      return res.data
    } else {
      // 业务错误，显示错误消息
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  async (error) => {
    // HTTP 错误处理
    if (error.response) {
      const status = error.response.status
      const data = error.response.data
      const originalRequest = error.config

      switch (status) {
        case 401:
          // 未授权，尝试刷新 Token
          if (!originalRequest._retry) {
            originalRequest._retry = true
            try {
              const authStore = useAuthStore()
              // 如果有 refresh token，尝试刷新
              if (authStore.refreshToken) {
                await authStore.refreshAccessToken()
                // 重试原请求
                originalRequest.headers.Authorization = `Bearer ${authStore.token}`
                return axios(originalRequest).then(res => res.data.data)
              }
            } catch (refreshError) {
              // 刷新失败，清除 token 并跳转登录
              const authStore = useAuthStore()
              authStore.logout()
              ElMessage.error('登录已过期，请重新登录')
              router.push('/login')
            }
          } else {
            // 已经重试过，直接登出
            const authStore = useAuthStore()
            authStore.logout()
            ElMessage.error('登录已过期，请重新登录')
            router.push('/login')
          }
          break
        case 403:
          ElMessage.error('没有权限执行此操作')
          break
        case 404:
          ElMessage.error(data?.message || '请求的资源不存在')
          break
        case 500:
          ElMessage.error('系统繁忙，请稍后重试')
          break
        default:
          ElMessage.error(data?.message || '请求失败')
      }
    } else if (error.request) {
      // 请求发送失败
      ElMessage.error('网络连接失败，请检查网络')
    } else {
      // 其他错误
      ElMessage.error(error.message || '请求失败')
    }

    return Promise.reject(error)
  }
)

export default request
