<script setup lang="ts">
import { computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useNotificationStore } from '@/stores/notification'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import AppButton from '@/components/common/AppButton.vue'
import ContentCard from '@/components/content/ContentCard.vue'
import DynamicBackground from '@/components/DynamicBackground.vue'
import { Reading, Trophy, UserFilled, ArrowRight } from '@element-plus/icons-vue'
import { observeAllScrollAnimations } from '@/utils/scrollAnimation'

const router = useRouter()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()

// 尝试从本地存储恢复登录状态
authStore.restoreFromStorage()

// 获取未读通知数量
let cleanupScrollAnimation: (() => void) | null = null

onMounted(() => {
  console.log('🏠 HomeViewPOC mounted')
  
  if (authStore.isAuthenticated) {
    notificationStore.fetchUnreadCount()
  }
  
  // 延迟一点执行，确保 DOM 完全渲染
  setTimeout(() => {
    console.log('🔍 Setting up scroll animations...')
    
    // 为特色功能卡片添加滚动渐入动画
    const cleanupFeatures = observeAllScrollAnimations('.feature-card', {
      threshold: 0.2,
      animationClass: 'animate-fade-in-up'
    })
    
    // 为内容卡片添加滚动渐入动画
    const cleanupContent = observeAllScrollAnimations('.content-card', {
      threshold: 0.1,
      animationClass: 'animate-fade-in-up'
    })
    
    console.log('✅ Scroll animations set up')
    
    // 合并清理函数
    cleanupScrollAnimation = () => {
      cleanupFeatures()
      cleanupContent()
    }
  }, 100)
})

onUnmounted(() => {
  if (cleanupScrollAnimation) {
    cleanupScrollAnimation()
  }
})

// 检查是否为管理员
const isAdmin = computed(() => {
  return authStore.user?.roles.includes('ROLE_ADMIN') || false
})

// 检查是否为预备成员
const isProbation = computed(() => {
  return authStore.user?.roles.includes('ROLE_PROBATION') || false
})

// 示例内容卡片数据（后续会从 API 获取）
const sampleCards = [
  {
    id: 1,
    title: 'Vue 3 Composition API 最佳实践',
    excerpt: '深入探讨 Vue 3 Composition API 的使用技巧，包括 reactive、ref、computed 等核心概念的实际应用场景...',
    author: '张三',
    date: '2026-04-08',
    role: 'member' as const,
    status: 'published' as const,
    tags: ['Vue', '前端']
  },
  {
    id: 2,
    title: 'Spring Boot 微服务架构设计',
    excerpt: '从零开始构建微服务架构，学习服务发现、配置管理、负载均衡等核心组件的设计与实现...',
    author: '李四',
    date: '2026-04-07',
    role: 'admin' as const,
    status: 'published' as const,
    tags: ['Java', '后端']
  },
  {
    id: 3,
    title: '算法竞赛入门指南',
    excerpt: '为初学者准备的算法竞赛入门教程，涵盖基础数据结构、常用算法和解题思路...',
    author: '王五',
    date: '2026-04-06',
    role: 'probation' as const,
    status: 'pending' as const,
    tags: ['算法', '竞赛']
  }
]

const handleLogout = () => {
  authStore.logout()
  window.location.reload()
}
</script>

<template>
  <div class="min-h-screen bg-neutral-50">
    <!-- 导航栏 -->
    <AppNavbar />

    <!-- Hero 区域 - 应用动态背景 -->
    <section class="relative overflow-hidden" style="min-height: 600px;">
      <!-- CSS 渐变流动背景（最底层） -->
      <div class="absolute inset-0 animated-gradient-bg"></div>
      
      <!-- Canvas 粒子系统（中间层） -->
      <DynamicBackground class="absolute inset-0">
        <div></div>
      </DynamicBackground>
      
      <!-- 内容层（最上层） -->
      <div class="relative z-10 max-w-content mx-auto px-4 sm:px-6 lg:px-8 py-24 md:py-32">
        <div class="text-center animate-fade-in">
          <h1 class="text-4xl md:text-5xl font-bold text-white mb-6 leading-tight drop-shadow-lg">
            沈阳工业大学<br/>计算机程序设计社团
          </h1>
          <p class="text-xl md:text-2xl text-primary-100 mb-12 animate-slide-up drop-shadow-md" style="animation-delay: 100ms">
            探索编程之美，创造无限可能
          </p>
          
          <!-- 操作按钮 -->
          <div v-if="!authStore.isAuthenticated" class="flex flex-col sm:flex-row gap-4 justify-center animate-slide-up" style="animation-delay: 200ms">
            <AppButton
              variant="primary"
              size="lg"
              @click="router.push('/register')"
              class="bg-white text-primary-600 hover:bg-primary-50 shadow-lg hover:shadow-xl"
            >
              加入我们
            </AppButton>
            <AppButton
              variant="outline"
              size="lg"
              @click="router.push('/login')"
              class="border-2 border-white text-white hover:bg-white/10"
            >
              登录
            </AppButton>
          </div>

          <div v-else class="flex flex-wrap gap-4 justify-center animate-slide-up" style="animation-delay: 200ms">
            <AppButton
              v-if="isAdmin"
              variant="primary"
              size="lg"
              @click="router.push('/admin/members')"
              class="bg-white/95 text-primary-600 hover:bg-white shadow-lg hover:shadow-xl"
            >
              成员管理
            </AppButton>
            <AppButton
              v-if="isAdmin"
              variant="primary"
              size="lg"
              @click="router.push('/admin/questions')"
              class="bg-white/95 text-primary-600 hover:bg-white shadow-lg hover:shadow-xl"
            >
              题库管理
            </AppButton>
            <AppButton
              v-if="isProbation"
              variant="primary"
              size="lg"
              @click="router.push('/promotion')"
              class="bg-white/95 text-primary-600 hover:bg-white shadow-lg hover:shadow-xl"
            >
              转正考核
            </AppButton>
            <AppButton
              variant="primary"
              size="lg"
              @click="router.push('/profile')"
              class="bg-white/95 text-primary-600 hover:bg-white shadow-lg hover:shadow-xl"
            >
              个人资料
            </AppButton>
          </div>
        </div>
      </div>
    </section>

    <!-- 特色功能区域 -->
    <section class="max-w-content mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- 技术分享 -->
        <div
          class="feature-card group bg-surface rounded-xl shadow-card hover:shadow-card-hover transition-all duration-200 p-8"
        >
          <div class="w-16 h-16 bg-primary-100 rounded-xl flex items-center justify-center mb-6 group-hover:scale-110 transition-transform duration-200">
            <Reading class="w-8 h-8 text-primary-600" />
          </div>
          <h3 class="text-xl font-semibold text-text-primary mb-3">技术分享</h3>
          <p class="text-text-secondary leading-relaxed">
            定期举办技术分享会，交流最新编程技术和开发经验，共同成长进步
          </p>
        </div>

        <!-- 竞赛培训 -->
        <div
          class="feature-card group bg-surface rounded-xl shadow-card hover:shadow-card-hover transition-all duration-200 p-8"
        >
          <div class="w-16 h-16 bg-green-100 rounded-xl flex items-center justify-center mb-6 group-hover:scale-110 transition-transform duration-200">
            <Trophy class="w-8 h-8 text-green-600" />
          </div>
          <h3 class="text-xl font-semibold text-text-primary mb-3">竞赛培训</h3>
          <p class="text-text-secondary leading-relaxed">
            组织算法竞赛培训，提升编程能力和解决问题的技巧，在竞赛中取得优异成绩
          </p>
        </div>

        <!-- 项目实战 -->
        <div
          class="feature-card group bg-surface rounded-xl shadow-card hover:shadow-card-hover transition-all duration-200 p-8"
        >
          <div class="w-16 h-16 bg-amber-100 rounded-xl flex items-center justify-center mb-6 group-hover:scale-110 transition-transform duration-200">
            <UserFilled class="w-8 h-8 text-amber-600" />
          </div>
          <h3 class="text-xl font-semibold text-text-primary mb-3">项目实战</h3>
          <p class="text-text-secondary leading-relaxed">
            参与实际项目开发，积累团队协作和工程实践经验，为职业发展打下坚实基础
          </p>
        </div>
      </div>
    </section>

    <!-- 最新内容区域 -->
    <section class="max-w-content mx-auto px-4 sm:px-6 lg:px-8 pb-16">
      <div class="flex items-center justify-between mb-8 animate-fade-in" style="animation-delay: 600ms">
        <h2 class="text-3xl font-bold text-text-primary">最新内容</h2>
        <button class="flex items-center gap-2 text-primary-600 hover:text-primary-700 font-medium transition-colors duration-200">
          查看全部
          <ArrowRight class="w-4 h-4" />
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <ContentCard
          v-for="(card, index) in sampleCards"
          :key="card.id"
          :title="card.title"
          :excerpt="card.excerpt"
          :author="card.author"
          :date="card.date"
          :role="card.role"
          :status="card.status"
          :tags="card.tags"
          @click="console.log('Card clicked:', card.id)"
          class="content-card"
          :style="{ transitionDelay: `${index * 100}ms` }"
        />
      </div>
    </section>
  </div>
</template>

<style scoped>
/* CSS 渐变流动动画 */
.animated-gradient-bg {
  background: linear-gradient(
    135deg, 
    #667eea 0%, 
    #764ba2 25%, 
    #f093fb 50%, 
    #764ba2 75%, 
    #667eea 100%
  );
  background-size: 300% 300%;
  animation: gradientFlow 12s ease-in-out infinite;
  will-change: background-position;
}

@keyframes gradientFlow {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

/* 移动端优化 */
@media (max-width: 768px) {
  .animated-gradient-bg {
    animation-duration: 20s; /* 减慢速度降低 CPU 占用 */
  }
}

/* 尊重用户偏好：禁用动画 */
@media (prefers-reduced-motion: reduce) {
  .animated-gradient-bg {
    animation: none;
    background-size: 100% 100%;
  }
}

/* 测试动画是否正常 */
.animate-fade-in {
  animation: fadeIn 0.8s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.animate-slide-up {
  animation: slideUp 0.8s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
