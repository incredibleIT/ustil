export interface IContent {
  id: number
  title: string
  summary: string
  content: string
  type: 'news' | 'blog'
  status: 'draft' | 'pending' | 'published' | 'rejected'
  authorId: number
  authorName: string
  coverImage?: string
  tags?: string[]
  viewCount: number
  createdAt: string
  updatedAt: string
}

export interface IContentQuery {
  page?: number
  size?: number
  type?: 'news' | 'blog'
  status?: string
}
