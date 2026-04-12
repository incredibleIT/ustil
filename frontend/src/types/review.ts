export interface IReviewVote {
  id: number
  contentId: number
  contentTitle: string
  userId: number
  vote: 'approve' | 'reject'
  createdAt: string
}

export interface IReviewQueueItem {
  contentId: number
  title: string
  summary: string
  authorName: string
  createdAt: string
  approveCount: number
  rejectCount: number
  totalMembers: number
}
