/**
 * 考试 API 模块
 */
import request from './request'
import type { PaperResponse } from '@/types/question'

export interface ExamResultResponse {
  examRecordId: number
  score: number
  maxScore: number
  passed: boolean
  singleChoiceScore: number
  multipleChoiceScore: number
  trueFalseScore: number
  attemptCount: number
  startedAt: string
  completedAt: string
  duration: number
  details: Array<{
    questionId: number
    questionText: string
    questionType: string
    userAnswer: string
    correctAnswer: string
    correct: boolean
    score: number
  }>
}

/**
 * 生成随机试卷
 */
export function generatePaper() {
  return request.get<PaperResponse>('/exam/generate-paper')
}

/**
 * 提交考试答案
 */
export function submitExam(data: {
  paperId: number
  startTime: string
  answers: Array<{ questionId: number; answer: string }>
}) {
  return request.post<ExamResultResponse>('/exam/submit', data)
}

/**
 * 获取考试结果
 */
export function getExamResult() {
  return request.get<ExamResultResponse>('/exam/result')
}
