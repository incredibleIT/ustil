/**
 * 题库 TypeScript 类型定义
 */

/** 题型枚举 */
export type QuestionType = 'single_choice' | 'multiple_choice' | 'true_false' | 'short_answer'

/** 题型标签映射 */
export const QuestionTypeLabels: Record<QuestionType, string> = {
  single_choice: '单选题',
  multiple_choice: '多选题',
  true_false: '判断题',
  short_answer: '简答题'
}

/** 题目接口 */
export interface Question {
  id: number
  questionText: string
  questionType: QuestionType
  options: string[]
  correctAnswer: string
  score: number
  createdAt: string
  updatedAt: string
}

/** 题目列表响应 */
export interface QuestionListResponse {
  total: number
  pages: number
  current: number
  size: number
  records: Question[]
}

/** 创建题目请求 */
export interface CreateQuestionRequest {
  questionText: string
  questionType: QuestionType
  options?: string[]
  correctAnswer?: string
  score: number
}

/** 更新题目请求 */
export interface UpdateQuestionRequest {
  questionText: string
  questionType: QuestionType
  options?: string[]
  correctAnswer?: string
  score: number
}

/** 试卷题目项（不包含答案） */
export interface PaperQuestionItem {
  id: number
  questionText: string
  questionType: QuestionType
  options: string[]
  score: number
}

/** 试卷响应 */
export interface PaperResponse {
  paperId: number
  questions: PaperQuestionItem[]
  totalScore: number
  duration: number
}
