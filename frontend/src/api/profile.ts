import request from './request'

export interface UserProfile {
  userId: number
  studentId: string
  username: string
  email: string
  role: string
  avatar: string
  bio: string
  status: number
  createdAt: string
}

export interface UpdateProfileData {
  username?: string
  bio?: string
}

export interface ChangePasswordData {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

/**
 * 获取当前用户个人信息
 */
export function getProfile(): Promise<UserProfile> {
  return request.get<UserProfile>('/profile')
}

/**
 * 修改个人信息
 */
export function updateProfile(data: UpdateProfileData): Promise<void> {
  return request.put<void>('/profile', data)
}

/**
 * 修改密码
 */
export function changePassword(data: ChangePasswordData): Promise<void> {
  return request.put<void>('/profile/password', data)
}
