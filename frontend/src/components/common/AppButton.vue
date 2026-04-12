<script setup lang="ts">
import { computed, useAttrs } from 'vue'

interface Props {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost'
  size?: 'sm' | 'md' | 'lg'
  loading?: boolean
  disabled?: boolean
  type?: 'button' | 'submit' | 'reset'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
  loading: false,
  disabled: false,
  type: 'button'
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

// 尺寸类名映射
const sizeClasses = computed(() => {
  const sizes = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-4 py-2 text-base',
    lg: 'px-6 py-3 text-lg'
  }
  return sizes[props.size]
})

// 检测是否有自定义样式（通过检查 $attrs.class）
const hasCustomStyle = computed(() => {
  // 如果传入了 class 且包含 bg- 或 text-，则认为有自定义样式
  const attrs = useAttrs()
  const customClass = attrs.class || ''
  return typeof customClass === 'string' && (customClass.includes('bg-') || customClass.includes('text-'))
})

// 变体类名映射
const variantClasses = computed(() => {
  const variants = {
    primary: 'bg-primary-500 text-white hover:bg-primary-600 shadow-card hover:shadow-card-hover',
    secondary: 'bg-surface text-text-primary border border-border hover:border-primary-300 shadow-card hover:shadow-card-hover',
    outline: 'bg-transparent text-primary-600 border-2 border-primary-500 hover:bg-primary-50',
    ghost: 'bg-transparent text-text-secondary hover:text-primary-600 hover:bg-primary-50'
  }
  return variants[props.variant]
})

const handleClick = (event: MouseEvent) => {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<template>
  <button
    :type="type"
    :disabled="disabled || loading"
    :class="[
      'inline-flex items-center justify-center font-medium rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2',
      sizeClasses,
      // variantClasses 作为基础样式，但允许外部 class 覆盖
      !hasCustomStyle ? variantClasses : '',
      {
        'opacity-50 cursor-not-allowed': disabled || loading,
        'cursor-pointer': !disabled && !loading
      }
    ]"
    @click="handleClick"
  >
    <!-- 加载状态 -->
    <svg
      v-if="loading"
      class="animate-spin -ml-1 mr-2 h-4 w-4"
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
      viewBox="0 0 24 24"
    >
      <circle
        class="opacity-25"
        cx="12"
        cy="12"
        r="10"
        stroke="currentColor"
        stroke-width="4"
      ></circle>
      <path
        class="opacity-75"
        fill="currentColor"
        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
      ></path>
    </svg>

    <!-- 按钮内容 -->
    <slot></slot>
  </button>
</template>

<style scoped>
/* 所有样式都使用 TailwindCSS 类 */
</style>
