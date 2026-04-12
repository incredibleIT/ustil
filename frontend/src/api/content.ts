import request from './request'

/**
 * 发布内容请求接口
 */
export interface PublishContentRequest {
  title: string          // 标题（必填，5-100字符）
  summary?: string       // 摘要（可选，最多200字符）
  content: string        // Markdown 内容（必填）
  type: 'news' | 'blog'  // 类型：news-资讯, blog-博客
  coverImage?: string    // 封面图 URL（可选）
  tags?: string[]        // 标签数组（可选）
}

/**
 * 发布内容响应接口
 */
export interface PublishContentResponse {
  code: number           // 200 成功
  message: string
  data: {
    id: number           // 内容 ID
    status: string       // pending-审核中
  }
}

/**
 * 用户内容列表项接口
 */
export interface UserContentItem {
  id: number             // 内容 ID
  title: string          // 标题
  summary?: string       // 摘要
  type: 'news' | 'blog'  // 类型
  coverImage?: string    // 封面图 URL
  status: string         // pending-审核中, published-已发布, rejected-已拒绝
  createdAt: string      // 创建时间
}

/**
 * 发布内容
 * @param data 发布请求数据
 * @returns 发布结果
 */
export async function publishContent(
  data: PublishContentRequest
): Promise<PublishContentResponse> {
  return request.post('/content', data)
}

/**
 * 获取我的内容列表
 * @returns 内容列表
 */
export async function getMyContents(): Promise<UserContentItem[]> {
  return request.get('/content/my')
}

/**
 * 获取所有内容列表（管理员）
 * @returns 所有内容列表
 */
export async function getAllContents(): Promise<UserContentItem[]> {
  return request.get('/content/admin/all')
}

/**
 * 获取单个内容详情
 * @param id 内容 ID
 * @returns 内容详情
 */
export async function getContentById(id: number): Promise<UserContentItem> {
  return request.get(`/content/${id}`)
}

/**
 * 更新内容请求接口
 */
export interface UpdateContentRequest {
  title?: string
  summary?: string
  content?: string
  coverImage?: string
  tags?: string[]
}

/**
 * 更新内容
 * @param id 内容 ID
 * @param data 更新数据
 * @returns 更新结果
 */
export async function updateContent(
  id: number,
  data: UpdateContentRequest
): Promise<PublishContentResponse> {
  return request.put(`/content/${id}`, data)
}

/**
 * 删除内容
 * @param id 内容 ID
 * @param reason 删除原因（管理员可选）
 * @returns 删除结果
 */
export async function deleteContent(
  id: number,
  reason?: string
): Promise<void> {
  return request.delete(`/content/${id}`, {
    data: reason ? { reason } : undefined
  })
}
