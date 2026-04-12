<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  title: string
  excerpt?: string
  author?: string
  date?: string
  status?: 'draft' | 'pending' | 'published' | 'rejected'
  role?: 'probation' | 'member' | 'admin'
  coverImage?: string
  tags?: string[]
}

const props = withDefaults(defineProps<Props>(), {
  excerpt: '',
  author: '',
  date: '',
  status: undefined,
  role: undefined,
  coverImage: '',
  tags: () => []
})

// 状态颜色映射
const statusConfig = computed(() => {
  const config = {
    draft: { label: '草稿', class: 'bg-status-draft/10 text-status-draft' },
    pending: { label: '待审核', class: 'bg-status-pending/10 text-status-pending' },
    published: { label: '已发布', class: 'bg-status-published/10 text-status-published' },
    rejected: { label: '已拒绝', class: 'bg-status-rejected/10 text-status-rejected' },
  }
  return props.status ? config[props.status] : null
})

// 角色颜色映射
const roleConfig = computed(() => {
  const config = {
    probation: { label: '预备成员', class: 'bg-role-probation/10 text-role-probation' },
    member: { label: '正式成员', class: 'bg-role-member/10 text-role-member' },
    admin: { label: '负责人', class: 'bg-role-admin/10 text-role-admin' },
  }
  return props.role ? config[props.role] : null
})

const emit = defineEmits<{
  click: []
}>()

const handleClick = () => {
  emit('click')
}
</script>

<template>
  <article
    class="group bg-surface rounded-xl shadow-card hover:shadow-card-hover transition-all duration-200 cursor-pointer overflow-hidden animate-fade-in"
    @click="handleClick"
  >
    <!-- 封面图（可选） -->
    <div v-if="coverImage" class="relative h-48 overflow-hidden">
      <img
        :src="coverImage"
        :alt="title"
        class="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-300"
      />
      <div class="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
    </div>

    <!-- 内容区 -->
    <div class="p-6">
      <!-- 标题 -->
      <h3 class="text-xl font-semibold text-text-primary mb-2 group-hover:text-primary-600 transition-colors duration-200 line-clamp-2">
        {{ title }}
      </h3>

      <!-- 摘要 -->
      <p v-if="excerpt" class="text-text-secondary text-sm line-clamp-2 mb-4 leading-relaxed">
        {{ excerpt }}
      </p>

      <!-- 标签 -->
      <div v-if="tags.length > 0" class="flex flex-wrap gap-2 mb-4">
        <span
          v-for="tag in tags"
          :key="tag"
          class="px-2 py-1 text-xs font-medium bg-primary-50 text-primary-600 rounded-md"
        >
          {{ tag }}
        </span>
      </div>

      <!-- 底部信息 -->
      <div class="flex items-center justify-between pt-4 border-t border-border">
        <!-- 作者和日期 -->
        <div class="flex items-center space-x-3">
          <!-- 角色徽章 -->
          <span
            v-if="roleConfig"
            :class="['inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium', roleConfig.class]"
          >
            {{ roleConfig.label }}
          </span>

          <!-- 状态标签 -->
          <span
            v-if="statusConfig"
            :class="['inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium', statusConfig.class]"
          >
            {{ statusConfig.label }}
          </span>

          <span v-if="author" class="text-xs text-text-tertiary">
            {{ author }}
          </span>
        </div>

        <!-- 日期 -->
        <time v-if="date" class="text-xs text-text-tertiary">
          {{ date }}
        </time>
      </div>
    </div>
  </article>
</template>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
