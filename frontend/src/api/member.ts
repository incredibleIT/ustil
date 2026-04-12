import request from '@/api/request'
import type { MemberListResponse, UpdateUserRoleRequest, UpdateUserStatusRequest } from '@/types/member'

/**
 * 获取成员列表（分页）
 */
export function getMemberList(params: {
  current: number
  size: number
  role?: string
  keyword?: string
}) {
  return request.get<MemberListResponse>('/admin/members', {
    params,
  })
}

/**
 * 修改成员角色
 */
export function updateMemberRole(userId: number, data: UpdateUserRoleRequest) {
  return request.put<void>(`/admin/members/${userId}/role`, data)
}

/**
 * 更新成员状态（禁用/启用）
 */
export function updateMemberStatus(userId: number, data: UpdateUserStatusRequest) {
  return request.put<void>(`/admin/members/${userId}/status`, data)
}
