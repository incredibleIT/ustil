import request from '@/api/request'
import type { PromotionInfoResponse, PromotionStatusResponse, PromotionReviewListResponse, ReviewProjectRequest } from '@/types/promotion'

/**
 * 获取转正流程说明
 */
export function getPromotionInfo() {
  return request.get<PromotionInfoResponse>('/promotion/info')
}

/**
 * 获取当前转正状态
 */
export function getPromotionStatus() {
  return request.get<PromotionStatusResponse>('/promotion/status')
}

/**
 * 提交转正申请
 */
export function submitPromotion() {
  return request.post<PromotionStatusResponse>('/promotion/apply')
}

/**
 * 提交项目（预备成员）
 */
export function submitProject(data: {
  projectName: string
  projectDescription: string
  projectUrl?: string
}) {
  return request.post<PromotionStatusResponse>('/promotion/project', data)
}

/**
 * 获取待评审列表（负责人）
 */
export function getPendingReviews(page: number, size: number, status?: string) {
  return request.get<PromotionReviewListResponse>('/admin/promotion/pending', {
    params: { page, size, status }
  })
}

/**
 * 评审项目（负责人）
 */
export function reviewProject(data: ReviewProjectRequest) {
  return request.post<PromotionStatusResponse>('/admin/promotion/review', data)
}
