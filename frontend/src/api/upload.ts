import axios from 'axios'

/**
 * 图片上传响应
 */
export interface UploadImageResponse {
  code: number
  message: string
  data: {
    url: string
    filename: string
  }
}

/**
 * 上传图片到服务器
 * @param file 图片文件
 * @param onProgress 上传进度回调
 * @returns 图片 URL
 */
export async function uploadImage(
  file: File,
  onProgress?: (percent: number) => void
): Promise<string> {
  // 验证文件类型
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    throw new Error('不支持的图片格式，仅支持 JPG、PNG、GIF、WebP')
  }

  // 验证文件大小（5MB）
  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    throw new Error('图片大小不能超过 5MB')
  }

  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await axios.post<UploadImageResponse>('/api/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (progressEvent.total && onProgress) {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          onProgress(percent)
        }
      }
    })

    if (response.data.code === 200) {
      return response.data.data.url
    } else {
      throw new Error(response.data.message || '上传失败')
    }
  } catch (error: any) {
    console.error('Image upload failed:', error)
    
    if (error.response) {
      // 服务器返回错误
      throw new Error(error.response.data?.message || '上传失败，请重试')
    } else if (error.request) {
      // 请求发送但没有收到响应
      throw new Error('网络连接失败，请检查网络')
    } else {
      // 其他错误
      throw new Error(error.message || '上传失败')
    }
  }
}

/**
 * 将文件转换为 Base64（临时方案，用于后端接口未完成时）
 * @param file 图片文件
 * @returns Base64 字符串
 */
export function fileToBase64(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result as string)
    reader.onerror = reject
    reader.readAsDataURL(file)
  })
}
