/**
 * 题库管理 API 模块
 */
import request from './request'
import type { QuestionListResponse, CreateQuestionRequest, UpdateQuestionRequest, Question } from '@/types/question'

/**
 * 获取题目列表（分页）
 */
export function getQuestions(params: {
  current: number
  size: number
  type?: string
  keyword?: string
}) {
  return request.get<QuestionListResponse>('/admin/questions', { params })
}

/**
 * 创建题目
 */
export function createQuestion(data: CreateQuestionRequest) {
  return request.post<Question>('/admin/questions', data)
}

/**
 * 更新题目
 */
export function updateQuestion(id: number, data: UpdateQuestionRequest) {
  return request.put<Question>(`/admin/questions/${id}`, data)
}

/**
 * 删除题目
 */
export function deleteQuestion(id: number) {
  return request.delete(`/admin/questions/${id}`)
}

/**
 * 获取题目详情
 */
export function getQuestionDetail(id: number) {
  return request.get<Question>(`/admin/questions/${id}`)
}

/**
 * 获取题目统计信息
 */
export function getQuestionStats() {
  return request.get<Record<string, number>>('/admin/questions/stats')
}
