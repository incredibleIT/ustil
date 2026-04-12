/**
 * 成员信息
 */
export interface MemberInfo {
  userId: number
  studentId: string
  username: string
  email: string
  role: string
  avatar: string | null
  status: number
  createdAt: string
}

/**
 * 成员列表响应（分页）
 */
export interface MemberListResponse {
  records: MemberInfo[]
  total: number
  current: number
  size: number
  pages: number
}

/**
 * 更新角色请求
 */
export interface UpdateUserRoleRequest {
  role: string
}

/**
 * 更新状态请求
 */
export interface UpdateUserStatusRequest {
  status: number
}
