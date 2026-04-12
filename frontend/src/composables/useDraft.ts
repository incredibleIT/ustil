import { ref } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * 草稿管理组合式函数
 * @param key localStorage 键名
 */
export function useDraft(key: string = 'blog_draft') {
  const lastSaved = ref<number>(0)
  const hasDraft = ref<boolean>(false)

  /**
   * 加载草稿
   * @returns 是否成功加载草稿
   */
  function loadDraft(): { content: string; title: string } | null {
    const saved = localStorage.getItem(key)
    if (saved) {
      try {
        const data = JSON.parse(saved)
        if (data.content || data.title) {
          hasDraft.value = true
          lastSaved.value = data.timestamp || 0
          return {
            content: data.content || '',
            title: data.title || ''
          }
        }
      } catch (e) {
        console.error('Failed to parse draft:', e)
        localStorage.removeItem(key)
      }
    }
    return null
  }

  /**
   * 保存草稿
   * @param content 编辑器内容（HTML）
   * @param title 标题
   */
  function saveDraft(content: string, title: string = '') {
    const data = {
      content,
      title,
      timestamp: Date.now()
    }
    localStorage.setItem(key, JSON.stringify(data))
    lastSaved.value = data.timestamp
    hasDraft.value = true
  }

  /**
   * 启动自动保存（每 30 秒）
   * @param getContent 获取内容的函数
   * @param getTitle 获取标题的函数
   */
  function startAutoSave(getContent: () => string, getTitle: () => string = () => '') {
    setInterval(() => {
      const content = getContent()
      const title = getTitle()
      
      // 只有当有内容时才保存
      if (content.trim() || title.trim()) {
        saveDraft(content, title)
        ElMessage.success({
          message: '草稿已自动保存',
          duration: 2000,
          showClose: false
        })
      }
    }, 30000) // 30 秒
  }

  /**
   * 清除草稿
   */
  function clearDraft() {
    localStorage.removeItem(key)
    lastSaved.value = 0
    hasDraft.value = false
    ElMessage.success('草稿已清除')
  }

  /**
   * 格式化最后保存时间
   */
  function formatLastSavedTime(): string {
    if (!lastSaved.value) return ''
    
    const now = Date.now()
    const diff = now - lastSaved.value
    
    if (diff < 60000) {
      return '刚刚'
    } else if (diff < 3600000) {
      return `${Math.floor(diff / 60000)} 分钟前`
    } else if (diff < 86400000) {
      return `${Math.floor(diff / 3600000)} 小时前`
    } else {
      return new Date(lastSaved.value).toLocaleString('zh-CN')
    }
  }

  return {
    lastSaved,
    hasDraft,
    loadDraft,
    saveDraft,
    startAutoSave,
    clearDraft,
    formatLastSavedTime
  }
}
