/**
 * 转正申请信息（对应后端 PromotionStatusResponse.ApplicationInfo）
 */
export interface PromotionApplication {
  id: number
  status: string
  createdAt: string
  examScore: number | null
  projectScore: number | null
  totalScore: number | null
  reviewComment: string | null
}

/**
 * 转正流程步骤
 */
export interface PromotionStep {
  step: number
  title: string
  description: string
  estimatedTime: string
}

/**
 * 评分规则
 */
export interface ScoringRules {
  examWeight: number
  projectWeight: number
  passingScore: number
}

/**
 * 转正流程说明响应
 */
export interface PromotionInfoResponse {
  title: string
  description: string
  steps: PromotionStep[]
  scoringRules: ScoringRules
}

/**
 * 转正状态响应
 * 注意：application 字段在未申请时为 undefined
 */
export interface PromotionStatusResponse {
  hasApplication: boolean
  canApply: boolean
  registrationDays: number
  application?: PromotionApplication
}

/**
 * 评审列表响应
 */
export interface PromotionReviewListResponse {
  total: number
  pages: number
  current: number
  size: number
  records: ReviewItem[]
}

export interface ReviewItem {
  applicationId: number
  applicantName: string
  projectName: string
  projectDescription: string
  projectUrl: string | null
  examScore: number | null
  submittedAt: string
  status: string
}

/**
 * 评审项目请求
 */
export interface ReviewProjectRequest {
  applicationId: number
  projectScore: number
  reviewComment?: string
}
