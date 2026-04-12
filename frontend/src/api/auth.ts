import request from '@/api/request'

/**
 * 用户注册请求接口
 */
export interface RegisterRequest {
  studentId: string
  username: string
  email: string
  password: string
  confirmPassword: string
}

/**
 * 用户登录请求接口
 */
export interface LoginRequest {
  email: string
  password: string
  rememberMe?: boolean
}

/**
 * 登录响应接口
 */
export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  userId: number
  username: string
  roles: string[]
}

/**
 * 刷新 Token 请求接口
 */
export interface RefreshTokenRequest {
  refreshToken: string
}

/**
 * 用户注册
 * @param data 注册请求数据
 * @returns 注册成功消息
 */
export const register = (data: RegisterRequest) => {
  return request.post<string>('/auth/register', data)
}

/**
 * 用户登录
 * @param data 登录请求数据
 * @returns 登录响应（包含 Token 和用户信息）
 */
export const login = (data: LoginRequest) => {
  return request.post<LoginResponse>('/auth/login', data)
}

/**
 * 刷新 Token
 * @param data 刷新 Token 请求数据
 * @returns 新的 Token 对
 */
export const refreshToken = (data: RefreshTokenRequest) => {
  return request.post<LoginResponse>('/auth/refresh', data)
}
