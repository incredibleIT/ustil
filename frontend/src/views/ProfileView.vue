<template>
  <div class="profile-container">
    <div class="profile-header animate-fade-in-up">
      <h1>个人中心</h1>
    </div>

    <el-card class="profile-card animate-fade-in-up" shadow="never">
      <el-tabs v-model="activeTab" class="profile-tabs">
        <!-- 个人信息标签页 -->
        <el-tab-pane label="个人信息" name="info">
          <div class="tab-content">
            <!-- 头像区域 -->
            <div class="avatar-section">
              <el-avatar :size="100" :src="profileForm.avatar || defaultAvatar" class="avatar" />
              <div class="role-badge" :class="getRoleClass(profileForm.role)">
                {{ getRoleText(profileForm.role) }}
              </div>
            </div>

            <el-form
              ref="profileFormRef"
              :model="profileForm"
              :rules="profileRules"
              label-width="100px"
              class="profile-form"
            >
              <el-form-item label="学号">
                <el-input v-model="profileForm.studentId" disabled />
              </el-form-item>

              <el-form-item label="昵称" prop="username">
                <el-input v-model="profileForm.username" placeholder="请输入昵称" />
              </el-form-item>

              <el-form-item label="邮箱">
                <el-input v-model="profileForm.email" disabled />
              </el-form-item>

              <el-form-item label="个人简介" prop="bio">
                <el-input
                  v-model="profileForm.bio"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入个人简介"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="账号状态">
                <el-tag :type="profileForm.status === 1 ? 'success' : 'danger'">
                  {{ profileForm.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </el-form-item>

              <el-form-item label="加入时间">
                <span class="text-secondary">{{ formatDate(profileForm.createdAt) }}</span>
              </el-form-item>

              <el-form-item>
                <AppButton variant="primary" :loading="savingProfile" @click="handleUpdateProfile">
                  保存修改
                </AppButton>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 修改密码标签页 -->
        <el-tab-pane label="修改密码" name="password">
          <div class="tab-content">
            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="120px"
              class="profile-form"
            >
              <el-form-item label="旧密码" prop="oldPassword">
                <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入旧密码"
                  show-password
                />
              </el-form-item>

              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码（至少8位，包含字母和数字）"
                  show-password
                />
              </el-form-item>

              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                />
              </el-form-item>

              <el-form-item>
                <AppButton variant="primary" :loading="changingPassword" @click="handleChangePassword">
                  修改密码
                </AppButton>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 我的内容标签页 -->
        <el-tab-pane label="我的内容" name="content">
          <div class="tab-content">
            <!-- 管理员入口 -->
            <div v-if="authStore.user?.roles.includes('ROLE_ADMIN')" class="admin-entry">
              <el-alert
                title="管理员权限"
                type="info"
                :closable="false"
                show-icon
              >
                <template #default>
                  <div class="admin-entry-content">
                    <span>您可以管理所有内容，包括其他用户发布的内容。</span>
                    <AppButton variant="primary" @click="router.push('/admin/contents')">
                      管理所有内容
                    </AppButton>
                  </div>
                </template>
              </el-alert>
            </div>

            <div v-if="loadingContents" class="loading-container">
              <el-skeleton :rows="5" animated />
            </div>
            <div v-else-if="contents.length === 0" class="content-placeholder">
              <el-empty description="暂无发布内容">
                <AppButton variant="primary" @click="router.push('/news/publish')">
                  发布资讯
                </AppButton>
              </el-empty>
            </div>
            <div v-else class="content-list">
              <el-card
                v-for="item in contents"
                :key="item.id"
                class="content-item"
                shadow="hover"
              >
                <div class="content-header">
                  <h3 class="content-title">{{ item.title }}</h3>
                  <el-tag :type="getStatusType(item.status)" size="small">
                    {{ getStatusText(item.status) }}
                  </el-tag>
                </div>
                <p class="content-summary">{{ item.summary || '无摘要' }}</p>
                <div class="content-footer">
                  <span class="content-type">
                    {{ item.type === 'news' ? '资讯' : '博客' }}
                  </span>
                  <span class="content-date">{{ formatDate(item.createdAt) }}</span>
                </div>
                <!-- 操作按钮 -->
                <div class="content-actions">
                  <el-button 
                    size="small" 
                    @click="handleEdit(item.id)"
                    :disabled="item.status === 'rejected'"
                  >
                    编辑
                  </el-button>
                  <el-button 
                    size="small" 
                    type="danger" 
                    @click="handleDelete(item.id)"
                  >
                    删除
                  </el-button>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getProfile, updateProfile, changePassword } from '@/api/profile'
import type { UserProfile, UpdateProfileData, ChangePasswordData } from '@/api/profile'
import { getMyContents, deleteContent } from '@/api/content'
import type { UserContentItem } from '@/api/content'
import AppButton from '@/components/common/AppButton.vue'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('info')
const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const savingProfile = ref(false)
const changingPassword = ref(false)
const loadingContents = ref(false)
const contents = ref<UserContentItem[]>([])

// 个人信息表单
const profileForm = reactive<UserProfile>({
  userId: 0,
  studentId: '',
  username: '',
  email: '',
  role: '',
  avatar: '',
  bio: '',
  status: 1,
  createdAt: '',
})

const profileRules: FormRules = {
  username: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度必须在 2-20 个字符之间', trigger: 'blur' },
  ],
  bio: [
    { max: 200, message: '个人简介不能超过 200 个字符', trigger: 'blur' },
  ],
}

// 修改密码表单
const passwordForm = reactive<ChangePasswordData>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' },
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '密码长度至少8位', trigger: 'blur' },
    { pattern: /^(?=.*[A-Za-z])(?=.*\d).+$/, message: '密码必须包含字母和数字', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

// 获取用户信息
const fetchProfile = async () => {
  try {
    const data = await getProfile()
    Object.assign(profileForm, data)
  } catch (error: any) {
    ElMessage.error(error.message || '获取个人信息失败')
  }
}

// 更新个人信息
const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (!valid) return

    savingProfile.value = true
    try {
      const updateData: UpdateProfileData = {
        username: profileForm.username,
        bio: profileForm.bio,
      }
      await updateProfile(updateData)
      ElMessage.success('个人信息已更新')
      // 重新获取最新信息
      await fetchProfile()
    } catch (error: any) {
      ElMessage.error(error.message || '更新失败')
    } finally {
      savingProfile.value = false
    }
  })
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    changingPassword.value = true
    try {
      await changePassword(passwordForm)
      ElMessage.success('密码修改成功，请重新登录')
      // 退出登录
      authStore.logout()
      router.push('/login')
    } catch (error: any) {
      ElMessage.error(error.message || '修改密码失败')
    } finally {
      changingPassword.value = false
    }
  })
}

// 角色样式类映射
const getRoleClass = (role: string): string => {
  const map: Record<string, string> = {
    ROLE_PROBATION: 'role-probation',
    ROLE_MEMBER: 'role-member',
    ROLE_ADMIN: 'role-admin',
  }
  return map[role] || 'role-probation'
}

// 角色文本映射
const getRoleText = (role: string): string => {
  const map: Record<string, string> = {
    ROLE_PROBATION: '预备成员',
    ROLE_MEMBER: '正式成员',
    ROLE_ADMIN: '负责人',
  }
  return map[role] || role
}

// 格式化日期
const formatDate = (dateStr: string): string => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

// 获取内容列表
const fetchContents = async () => {
  loadingContents.value = true
  try {
    contents.value = await getMyContents()
  } catch (error: any) {
    ElMessage.error(error.message || '获取内容列表失败')
  } finally {
    loadingContents.value = false
  }
}

// 状态类型映射
const getStatusType = (status: string): 'warning' | 'success' | 'danger' => {
  const map: Record<string, 'warning' | 'success' | 'danger'> = {
    pending: 'warning',
    published: 'success',
    rejected: 'danger',
  }
  return map[status] || 'warning'
}

// 状态文本映射
const getStatusText = (status: string): string => {
  const map: Record<string, string> = {
    pending: '审核中',
    published: '已发布',
    rejected: '已拒绝',
  }
  return map[status] || status
}

// 编辑内容
const handleEdit = (id: number) => {
  console.log('handleEdit called with id:', id, 'type:', typeof id)
  if (!id) {
    ElMessage.error('无效的内容 ID')
    return
  }
  const path = `/content/${id}/edit`
  console.log('Navigating to:', path)
  router.push(path).catch(err => {
    console.error('Navigation failed:', err)
    ElMessage.error('跳转失败，请重试')
  })
}

// 删除内容
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条内容吗？删除后将无法恢复。',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteContent(id)
    ElMessage.success('删除成功')
    
    // 刷新列表
    await fetchContents()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('Delete failed:', error)
      
      // 根据错误类型显示不同提示
      const errorMessage = error.message || '删除失败'
      if (errorMessage.includes('403') || errorMessage.includes('Forbidden')) {
        ElMessage.error('无权删除该内容')
      } else if (errorMessage.includes('404') || errorMessage.includes('not found')) {
        ElMessage.error('内容不存在')
      } else {
        ElMessage.error(errorMessage)
      }
    }
  }
}

onMounted(() => {
  fetchProfile()
  fetchContents()
})
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  padding: var(--spacing-3xl) var(--spacing-base);
  background-color: var(--color-background);
}

.profile-header {
  max-width: var(--max-width-content);
  margin: 0 auto var(--spacing-xl);
}

.profile-header h1 {
  font-size: var(--font-size-3xl);
  color: var(--color-text-primary);
  margin: 0;
  font-weight: var(--font-weight-bold);
}

.profile-card {
  max-width: var(--max-width-content);
  margin: 0 auto;
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
}

/* Tabs 样式优化 */
.profile-tabs :deep(.el-tabs__header) {
  margin-bottom: var(--spacing-xl);
  border-bottom: 2px solid var(--color-neutral-100);
}

.profile-tabs :deep(.el-tabs__item) {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-secondary);
  padding: 0 var(--spacing-xl);
  height: 48px;
  line-height: 48px;
  transition: all var(--transition-base);
}

.profile-tabs :deep(.el-tabs__item:hover) {
  color: var(--color-primary-600);
}

.profile-tabs :deep(.el-tabs__item.is-active) {
  color: var(--color-primary-600);
  font-weight: var(--font-weight-semibold);
}

.profile-tabs :deep(.el-tabs__active-bar) {
  height: 3px;
  background-color: var(--color-primary-600);
}

.tab-content {
  padding: var(--spacing-lg) 0;
}

/* 管理员入口 */
.admin-entry {
  margin-bottom: var(--spacing-xl);
}

.admin-entry-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
}

/* 头像区域 */
.avatar-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-2xl);
  padding-bottom: var(--spacing-xl);
  border-bottom: 1px solid var(--color-neutral-100);
}

.avatar {
  border: 3px solid var(--color-primary-100);
  box-shadow: var(--shadow-card);
}

/* 角色徽章 */
.role-badge {
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  transition: all var(--transition-base);
}

.role-probation {
  background-color: var(--color-role-probation);
  color: white;
}

.role-member {
  background-color: var(--color-role-member);
  color: white;
}

.role-admin {
  background-color: var(--color-role-admin);
  color: white;
}

.profile-form {
  max-width: 600px;
  margin: 0 auto;
}

/* 文本颜色 */
.text-secondary {
  color: var(--color-text-secondary);
}

.content-placeholder {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-container {
  padding: var(--spacing-xl);
}

.content-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.content-item {
  transition: all var(--transition-base);
  border-radius: var(--radius-lg);
}

.content-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-card);
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-sm);
}

.content-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin: 0;
  flex: 1;
  margin-right: var(--spacing-md);
}

.content-summary {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
  line-height: 1.6;
  margin: 0 0 var(--spacing-sm) 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.content-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-sm);
  border-top: 1px solid var(--color-neutral-100);
}

.content-type {
  font-size: var(--font-size-sm);
  color: var(--color-primary-600);
  font-weight: var(--font-weight-medium);
}

.content-date {
  font-size: var(--font-size-xs);
  color: var(--color-text-muted);
}

.content-actions {
  display: flex;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-sm);
  border-top: 1px solid var(--color-neutral-100);
}

/* 响应式 */
@media (max-width: 768px) {
  .profile-container {
    padding: var(--spacing-xl) var(--spacing-sm);
  }

  .profile-header h1 {
    font-size: var(--font-size-2xl);
  }

  .avatar-section {
    flex-direction: column;
    text-align: center;
  }

  .profile-form {
    max-width: 100%;
  }
}
</style>
