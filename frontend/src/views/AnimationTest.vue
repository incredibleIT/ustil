<script setup lang="ts">
import { ref, onMounted } from 'vue'
import SkeletonScreen from '@/components/common/SkeletonScreen.vue'
import AppButton from '@/components/common/AppButton.vue'
import CelebrationEffect from '@/components/common/CelebrationEffect.vue'

const showSkeleton = ref(true)
const celebrate = ref(false)
const loadingStates = ref({
  card: true,
  list: true,
  profile: true,
  table: true
})

// 模拟数据加载
onMounted(() => {
  setTimeout(() => {
    showSkeleton.value = false
  }, 3000)
  
  // 分别控制不同骨架屏的显示
  setTimeout(() => {
    loadingStates.value.card = false
  }, 2000)
  
  setTimeout(() => {
    loadingStates.value.list = false
  }, 2500)
  
  setTimeout(() => {
    loadingStates.value.profile = false
  }, 3000)
  
  setTimeout(() => {
    loadingStates.value.table = false
  }, 3500)
})

const reloadPage = () => {
  window.location.reload()
}

const triggerCelebration = () => {
  celebrate.value = true
  // 3秒后重置，允许再次触发
  setTimeout(() => {
    celebrate.value = false
  }, 3000)
}
</script>

<template>
  <div class="min-h-screen bg-neutral-50 py-12">
    <div class="max-w-content mx-auto px-4 sm:px-6 lg:px-8">
      <!-- 页面标题 -->
      <div class="mb-12 text-center animate-fade-in">
        <h1 class="text-4xl font-bold text-text-primary mb-4">动画系统测试页面</h1>
        <p class="text-lg text-text-secondary">测试滚动渐入、页面过渡和骨架屏效果</p>
        <AppButton variant="primary" size="md" @click="reloadPage" class="mt-4">
          重新加载页面
        </AppButton>
      </div>

      <!-- 1. 滚动渐入动画测试 -->
      <section class="mb-16">
        <h2 class="text-2xl font-bold text-text-primary mb-6">1. 滚动渐入动画</h2>
        <p class="text-text-secondary mb-4">向下滚动查看卡片渐入效果</p>
        
        <div class="space-y-4">
          <div
            v-for="i in 10"
            :key="i"
            class="scroll-test-item bg-surface rounded-xl p-6 shadow-card"
            :style="{ opacity: 0, transform: 'translateY(20px)' }"
          >
            <h3 class="text-lg font-semibold text-text-primary mb-2">卡片 {{ i }}</h3>
            <p class="text-text-secondary">这个卡片会在滚动到视口时淡入显示</p>
          </div>
        </div>
      </section>

      <!-- 2. 骨架屏测试 -->
      <section class="mb-16">
        <h2 class="text-2xl font-bold text-text-primary mb-6">2. Skeleton Screen 骨架屏</h2>
        <p class="text-text-secondary mb-6">骨架屏会在 2-3.5 秒后自动切换为真实内容</p>

        <!-- 卡片骨架屏 -->
        <div class="mb-8">
          <h3 class="text-lg font-semibold text-text-primary mb-4">卡片类型</h3>
          <SkeletonScreen v-if="loadingStates.card" type="card" :count="2" />
          <div v-else class="space-y-4">
            <div v-for="i in 2" :key="i" class="bg-surface rounded-xl p-6 shadow-card">
              <div class="w-full h-48 bg-gradient-to-br from-primary-100 to-primary-200 rounded-lg mb-4 flex items-center justify-center">
                <span class="text-primary-600 font-medium">封面图 {{ i }}</span>
              </div>
              <h4 class="text-xl font-semibold text-text-primary mb-2">文章标题 {{ i }}</h4>
              <p class="text-text-secondary mb-4">这是文章的摘要内容，展示了真实的卡片数据...</p>
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <div class="w-8 h-8 bg-primary-500 rounded-full flex items-center justify-center text-white text-sm">A</div>
                  <span class="text-sm text-text-secondary">作者 {{ i }}</span>
                </div>
                <span class="text-sm text-text-secondary">2026-04-08</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 列表骨架屏 -->
        <div class="mb-8">
          <h3 class="text-lg font-semibold text-text-primary mb-4">列表类型</h3>
          <SkeletonScreen v-if="loadingStates.list" type="list" :count="3" />
          <div v-else class="space-y-3">
            <div v-for="i in 3" :key="i" class="bg-surface rounded-lg p-4 shadow-card flex items-center gap-4">
              <div class="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center text-primary-600 font-bold">
                {{ i }}
              </div>
              <div class="flex-1">
                <h4 class="font-semibold text-text-primary">列表项 {{ i }}</h4>
                <p class="text-sm text-text-secondary">这是列表项的描述信息</p>
              </div>
              <AppButton variant="outline" size="sm">操作</AppButton>
            </div>
          </div>
        </div>

        <!-- 个人资料骨架屏 -->
        <div class="mb-8">
          <h3 class="text-lg font-semibold text-text-primary mb-4">个人资料类型</h3>
          <SkeletonScreen v-if="loadingStates.profile" type="profile" />
          <div v-else class="bg-surface rounded-xl p-8 shadow-card">
            <div class="flex items-center gap-6 mb-8">
              <div class="w-24 h-24 bg-gradient-to-br from-primary-400 to-primary-600 rounded-full flex items-center justify-center text-white text-3xl font-bold">
                U
              </div>
              <div>
                <h3 class="text-2xl font-bold text-text-primary mb-2">用户名</h3>
                <p class="text-text-secondary">user@example.com</p>
              </div>
            </div>
            <div class="space-y-4">
              <div class="flex items-center gap-4">
                <span class="text-text-secondary w-24">姓名：</span>
                <span class="text-text-primary">张三</span>
              </div>
              <div class="flex items-center gap-4">
                <span class="text-text-secondary w-24">角色：</span>
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-role-member/10 text-role-member">正式成员</span>
              </div>
              <div class="flex items-center gap-4">
                <span class="text-text-secondary w-24">加入时间：</span>
                <span class="text-text-primary">2026-01-01</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 表格骨架屏 -->
        <div>
          <h3 class="text-lg font-semibold text-text-primary mb-4">表格类型</h3>
          <SkeletonScreen v-if="loadingStates.table" type="table" :count="4" />
          <div v-else class="bg-surface rounded-xl shadow-card overflow-hidden">
            <div class="bg-neutral-100 px-6 py-4 border-b border-border">
              <div class="grid grid-cols-4 gap-4 text-sm font-medium text-text-secondary">
                <div>ID</div>
                <div>名称</div>
                <div>状态</div>
                <div>操作</div>
              </div>
            </div>
            <div class="divide-y divide-border">
              <div v-for="i in 4" :key="i" class="px-6 py-4">
                <div class="grid grid-cols-4 gap-4 text-sm">
                  <div class="text-text-secondary">{{ i }}</div>
                  <div class="text-text-primary">项目 {{ i }}</div>
                  <div>
                    <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-status-published/10 text-status-published">已完成</span>
                  </div>
                  <div>
                    <AppButton variant="ghost" size="sm">查看</AppButton>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 3. 页面过渡动画说明 -->
      <section class="mb-16">
        <h2 class="text-2xl font-bold text-text-primary mb-4">3. 页面过渡动画</h2>
        <p class="text-text-secondary mb-4">点击下面的链接切换到其他页面，观察淡入淡出效果</p>
        <div class="flex flex-wrap gap-4">
          <router-link to="/" class="text-primary-600 hover:text-primary-700 font-medium">
            → 返回首页
          </router-link>
          <router-link to="/login" class="text-primary-600 hover:text-primary-700 font-medium">
            → 登录页面
          </router-link>
          <router-link to="/register" class="text-primary-600 hover:text-primary-700 font-medium">
            → 注册页面
          </router-link>
        </div>
      </section>

      <!-- 4. 性能监控提示 -->
      <section class="bg-primary-50 rounded-xl p-6 border border-primary-200">
        <h3 class="text-lg font-semibold text-primary-900 mb-2">💡 性能提示</h3>
        <ul class="space-y-2 text-primary-800">
          <li>• 打开浏览器开发者工具（F12）</li>
          <li>• 切换到 Performance 标签</li>
          <li>• 录制滚动和页面切换过程</li>
          <li>• 检查帧率是否保持在 60fps</li>
          <li>• 观察是否有布局抖动（Layout Shift）</li>
        </ul>
      </section>
      
      <!-- 庆祝动画测试 -->
      <section class="mb-12">
        <h2 class="text-2xl font-bold text-text-primary mb-6">🎉 庆祝动画测试</h2>
        <div class="bg-surface rounded-xl p-8 shadow-card">
          <p class="text-text-secondary mb-4">
            点击按钮触发彩带庆祝动画，用于考试完成、成就解锁等场景。
          </p>
          <AppButton @click="triggerCelebration" variant="primary">
            🎊 触发庆祝动画
          </AppButton>
          <CelebrationEffect :trigger="celebrate" />
        </div>
      </section>
    </div>
  </div>
</template>

<script lang="ts">
// 为滚动测试项添加观察
import { observeAllScrollAnimations } from '@/utils/scrollAnimation'

export default {
  mounted() {
    observeAllScrollAnimations('.scroll-test-item', {
      threshold: 0.1,
      animationClass: 'animate-fade-in-up'
    })
  }
}
</script>
