<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useNotificationStore } from '@/stores/notification'
import { Bell } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()

const isScrolled = ref(false)

// 检查是否为管理员
const isAdmin = computed(() => {
  return authStore.user?.roles.includes('ROLE_ADMIN') || false
})

// 检查是否为预备成员
const isProbation = computed(() => {
  return authStore.user?.roles.includes('ROLE_PROBATION') || false
})

// 监听滚动事件
const handleScroll = () => {
  isScrolled.value = window.scrollY > 10
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  if (authStore.isAuthenticated) {
    notificationStore.fetchUnreadCount()
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const handleLogout = () => {
  authStore.logout()
  router.push('/')
}
</script>

<template>
  <nav
    class="sticky top-0 z-50 bg-surface/95 backdrop-blur-sm border-b border-border transition-shadow duration-200"
    :class="{ 'shadow-nav-scrolled': isScrolled, 'shadow-nav': !isScrolled }"
  >
    <div class="max-w-content mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <!-- Logo -->
        <div class="flex-shrink-0 cursor-pointer" @click="router.push('/')">
          <h1 class="text-xl font-bold text-primary-600">SYIT-CPC</h1>
        </div>

        <!-- 导航链接 -->
        <div class="hidden md:flex items-center space-x-8">
          <a
            @click="router.push('/')"
            class="text-text-secondary hover:text-primary-600 cursor-pointer transition-colors duration-200 font-medium"
          >
            首页
          </a>
          <a
            v-if="authStore.isAuthenticated"
            @click="router.push('/profile')"
            class="text-text-secondary hover:text-primary-600 cursor-pointer transition-colors duration-200 font-medium"
          >
            个人中心
          </a>
          <a
            v-if="authStore.isAuthenticated"
            @click="router.push('/news/publish')"
            class="text-text-secondary hover:text-primary-600 cursor-pointer transition-colors duration-200 font-medium"
          >
            发布资讯
          </a>
          <a
            v-if="authStore.isAuthenticated"
            @click="router.push('/blog/publish')"
            class="text-text-secondary hover:text-primary-600 cursor-pointer transition-colors duration-200 font-medium"
          >
            发布博客
          </a>
          <a
            v-if="isAdmin"
            @click="router.push('/admin/members')"
            class="text-text-secondary hover:text-primary-600 cursor-pointer transition-colors duration-200 font-medium"
          >
            成员管理
          </a>
          <a
            v-if="isAdmin"
            @click="router.push('/admin/questions')"
            class="text-text-secondary hover:text-primary-600 cursor-pointer transition-colors duration-200 font-medium"
          >
            题库管理
          </a>
          <a
            v-if="isProbation"
            @click="router.push('/promotion')"
            class="text-text-secondary hover:text-primary-600 cursor-pointer transition-colors duration-200 font-medium"
          >
            转正考核
          </a>
        </div>

        <!-- 右侧操作区 -->
        <div class="flex items-center space-x-4">
          <!-- 通知中心 -->
          <el-badge
            v-if="authStore.isAuthenticated"
            :value="notificationStore.unreadCount"
            :hidden="notificationStore.unreadCount === 0"
            class="notification-badge"
          >
            <button
              @click="router.push('/notifications')"
              class="p-2 rounded-lg text-text-secondary hover:text-primary-600 hover:bg-primary-50 transition-all duration-200"
            >
              <Bell class="w-5 h-5" />
            </button>
          </el-badge>

          <!-- 登录状态 -->
          <template v-if="authStore.isAuthenticated">
            <!-- 角色徽章 -->
            <span
              v-if="authStore.user?.roles.includes('ROLE_PROBATION')"
              class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-role-probation/10 text-role-probation"
            >
              预备成员
            </span>
            <span
              v-else-if="authStore.user?.roles.includes('ROLE_MEMBER')"
              class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-role-member/10 text-role-member"
            >
              正式成员
            </span>
            <span
              v-else-if="authStore.user?.roles.includes('ROLE_ADMIN')"
              class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-role-admin/10 text-role-admin"
            >
              负责人
            </span>

            <!-- 退出按钮 -->
            <button
              @click="handleLogout"
              class="px-4 py-2 text-sm font-medium text-text-secondary hover:text-primary-600 hover:bg-primary-50 rounded-lg transition-all duration-200"
            >
              退出
            </button>
          </template>

          <template v-else>
            <button
              @click="router.push('/login')"
              class="px-4 py-2 text-sm font-medium text-text-secondary hover:text-primary-600 transition-colors duration-200"
            >
              登录
            </button>
            <button
              @click="router.push('/register')"
              class="px-4 py-2 text-sm font-medium text-white bg-primary-500 hover:bg-primary-600 rounded-lg shadow-card hover:shadow-card-hover transition-all duration-200"
            >
              注册
            </button>
          </template>
        </div>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.notification-badge {
  display: inline-block;
}
</style>
