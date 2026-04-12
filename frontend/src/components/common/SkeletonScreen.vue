<script setup lang="ts">
interface Props {
  /** 骨架屏类型 */
  type?: 'card' | 'list' | 'profile' | 'table'
  /** 重复次数（用于 list 类型）*/
  count?: number
  /** 是否显示动画 */
  animated?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'card',
  count: 3,
  animated: true
})
</script>

<template>
  <!-- 卡片骨架屏 -->
  <div v-if="type === 'card'" class="space-y-4">
    <div
      v-for="i in count"
      :key="i"
      class="bg-surface rounded-xl p-6 shadow-card"
      :class="{ 'animate-pulse': animated }"
    >
      <!-- 封面图占位 -->
      <div class="w-full h-48 bg-neutral-200 rounded-lg mb-4"></div>
      
      <!-- 标题占位 -->
      <div class="h-6 bg-neutral-200 rounded w-3/4 mb-3"></div>
      
      <!-- 摘要占位 -->
      <div class="space-y-2 mb-4">
        <div class="h-4 bg-neutral-200 rounded w-full"></div>
        <div class="h-4 bg-neutral-200 rounded w-5/6"></div>
      </div>
      
      <!-- 底部信息占位 -->
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <div class="w-8 h-8 bg-neutral-200 rounded-full"></div>
          <div class="h-4 bg-neutral-200 rounded w-20"></div>
        </div>
        <div class="h-4 bg-neutral-200 rounded w-16"></div>
      </div>
    </div>
  </div>

  <!-- 列表骨架屏 -->
  <div v-else-if="type === 'list'" class="space-y-3">
    <div
      v-for="i in count"
      :key="i"
      class="bg-surface rounded-lg p-4 shadow-card flex items-center gap-4"
      :class="{ 'animate-pulse': animated }"
    >
      <!-- 图标/头像占位 -->
      <div class="w-12 h-12 bg-neutral-200 rounded-lg flex-shrink-0"></div>
      
      <!-- 内容占位 -->
      <div class="flex-1 space-y-2">
        <div class="h-5 bg-neutral-200 rounded w-3/4"></div>
        <div class="h-4 bg-neutral-200 rounded w-1/2"></div>
      </div>
      
      <!-- 右侧操作区占位 -->
      <div class="h-8 bg-neutral-200 rounded w-20 flex-shrink-0"></div>
    </div>
  </div>

  <!-- 个人资料骨架屏 -->
  <div v-else-if="type === 'profile'" class="bg-surface rounded-xl p-8 shadow-card" :class="{ 'animate-pulse': animated }">
    <!-- 头像占位 -->
    <div class="flex items-center gap-6 mb-8">
      <div class="w-24 h-24 bg-neutral-200 rounded-full"></div>
      <div class="space-y-3 flex-1">
        <div class="h-7 bg-neutral-200 rounded w-1/3"></div>
        <div class="h-5 bg-neutral-200 rounded w-1/4"></div>
      </div>
    </div>
    
    <!-- 信息字段占位 -->
    <div class="space-y-4">
      <div v-for="i in 4" :key="i" class="flex items-center gap-4">
        <div class="h-5 bg-neutral-200 rounded w-24"></div>
        <div class="h-5 bg-neutral-200 rounded flex-1"></div>
      </div>
    </div>
  </div>

  <!-- 表格骨架屏 -->
  <div v-else-if="type === 'table'" class="bg-surface rounded-xl shadow-card overflow-hidden" :class="{ 'animate-pulse': animated }">
    <!-- 表头占位 -->
    <div class="bg-neutral-100 px-6 py-4 border-b border-border">
      <div class="flex items-center gap-4">
        <div v-for="i in 4" :key="i" class="h-5 bg-neutral-200 rounded flex-1"></div>
      </div>
    </div>
    
    <!-- 表格行占位 -->
    <div class="divide-y divide-border">
      <div v-for="i in count" :key="i" class="px-6 py-4">
        <div class="flex items-center gap-4">
          <div v-for="j in 4" :key="j" class="h-5 bg-neutral-200 rounded flex-1"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 骨架屏闪烁动画 */
.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>
