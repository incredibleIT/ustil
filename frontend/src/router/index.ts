import { createRouter, createWebHistory } from 'vue-router'
import type { RouteLocationNormalized } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/admin/members',
      name: 'admin-members',
      component: () => import('../views/MembersView.vue'),
      meta: { requiresAuth: true, requiresRole: 'ROLE_ADMIN' },
    },
    {
      path: '/promotion',
      name: 'promotion',
      component: () => import('../views/PromotionView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/admin/questions',
      name: 'admin-questions',
      component: () => import('../views/QuestionsView.vue'),
      meta: { requiresAuth: true, requiresRole: 'ROLE_ADMIN' },
    },
    {
      path: '/exam',
      name: 'exam',
      component: () => import('../views/ExamView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/exam/result',
      name: 'examResult',
      component: () => import('../views/ExamResultView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/promotion/project/submit',
      name: 'projectSubmit',
      component: () => import('../views/ProjectSubmitView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/admin/promotion/review',
      name: 'admin-promotion-review',
      component: () => import('../views/AdminPromotionReviewView.vue'),
      meta: { requiresAuth: true, requiresRole: 'ROLE_ADMIN' },
    },
    {
      path: '/notifications',
      name: 'notifications',
      component: () => import('../views/NotificationView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/design-token-test',
      name: 'designTokenTest',
      component: () => import('../views/DesignTokenTest.vue'),
    },
    {
      path: '/animation-test',
      name: 'animationTest',
      component: () => import('../views/AnimationTest.vue'),
    },
    // POC 测试页面 - 动态背景效果
    {
      path: '/home-poc',
      name: 'homePOC',
      component: () => import('../views/HomeViewPOC.vue'),
    },
    // 高级 POC - GSAP 动画效果
    {
      path: '/home-poc-advanced',
      name: 'homePOCAdvanced',
      component: () => import('../views/HomeViewPOCAdvanced.vue'),
    },
    // 终极 POC - Aurora 背景 + GSAP 动画
    {
      path: '/home-ultimate',
      name: 'homeUltimate',
      component: () => import('../views/HomeViewUltimate.vue'),
    },
    // JIEJOE 风格 - Three.js 3D + Lenis 平滑滚动 + GSAP
    {
      path: '/home-jiejoe',
      name: 'homeJIEJOE',
      component: () => import('../views/HomeViewJIEJOE.vue'),
    },
    // POC - 粒子连线网络背景
    {
      path: '/home-particles',
      name: 'homeParticles',
      component: () => import('../views/HomeViewParticles.vue'),
    },
    // POC - 代码雨背景
    {
      path: '/home-code-rain',
      name: 'homeCodeRain',
      component: () => import('../views/HomeViewCodeRain.vue'),
    },
    // POC - 神经网络风格背景
    {
      path: '/home-neural-network',
      name: 'homeNeuralNetwork',
      component: () => import('../views/HomeViewNeuralNetwork.vue'),
    },
    // POC - 神经网络专业版（带权重可视化、激活函数等）
    {
      path: '/home-neural-network-pro',
      name: 'homeNeuralNetworkPro',
      component: () => import('../views/HomeViewNeuralNetworkPro.vue'),
    },
    // POC - 神经网络终极增强版（所有高优先级特效 + 文字逐字动画）
    {
      path: '/home-neural-network-ultimate',
      name: 'homeNeuralNetworkUltimate',
      component: () => import('../views/HomeViewNeuralNetworkUltimate.vue'),
    },
    // POC - 功能按钮滚动后动态展示方案探索
    {
      path: '/home-button-placement-poc',
      name: 'homeButtonPlacementPOC',
      component: () => import('../views/HomeViewButtonPlacementPOC.vue'),
    },
    // 测试 Markdown 编辑器
    {
      path: '/test-blog-editor',
      name: 'testBlogEditor',
      component: () => import('../views/SimpleTestView.vue'),
    },
    // 资讯发布
    {
      path: '/news/publish',
      name: 'newsPublish',
      component: () => import('../views/NewsPublishView.vue'),
      meta: { requiresAuth: true }, // 临时移除角色限制，仅需要认证
    },
    // 博客发布
    {
      path: '/blog/publish',
      name: 'blogPublish',
      component: () => import('../views/BlogPublishView.vue'),
      meta: { requiresAuth: true }, // 临时移除角色限制，仅需要认证
    },
    // 内容编辑
    {
      path: '/content/:id/edit',
      name: 'contentEdit',
      component: () => import('../views/ContentEditView.vue'),
      meta: { requiresAuth: true },
    },
    // 管理员内容管理
    {
      path: '/admin/contents',
      name: 'admin-contents',
      component: () => import('../views/AdminContentView.vue'),
      meta: { requiresAuth: true, requiresRole: 'ROLE_ADMIN' },
    },
  ],
})

// 路由守卫
router.beforeEach((to: RouteLocationNormalized, from: RouteLocationNormalized): any => {
  const authStore = useAuthStore()
  const isAuthenticated = authStore.isAuthenticated
  
  // 需要认证的路由
  if (to.meta.requiresAuth && !isAuthenticated) {
    ElMessage.warning('请先登录')
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  // 需要特定角色的路由
  if (to.meta.requiresRole && authStore.user) {
    const hasRole = authStore.user.roles.includes(to.meta.requiresRole as string)
    if (!hasRole) {
      ElMessage.error('权限不足，无法访问该页面')
      return { name: 'home' }
    }
  }
  
  // 已登录用户访问登录/注册页，重定向到首页
  if ((to.name === 'login' || to.name === 'register') && isAuthenticated) {
    return { name: 'home' }
  }
  
  // 继续导航
  return true
})

export default router
