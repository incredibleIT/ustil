/**
 * 滚动渐入动画工具函数
 * 使用 Intersection Observer API 实现元素进入视口时的动画效果
 */

export interface ScrollAnimationOptions {
  /** 触发动画的阈值（0-1），默认 0.1 */
  threshold?: number
  /** 根元素的边距，默认 '0px' */
  rootMargin?: string
  /** 动画类名，默认 'animate-fade-in-up' */
  animationClass?: string
  /** 是否在动画完成后停止观察，默认 true */
  once?: boolean
}

/**
 * 为元素添加滚动渐入动画
 * @param element 目标元素
 * @param options 配置选项
 * @returns 清理函数，用于停止观察
 */
export function observeScrollAnimation(
  element: HTMLElement,
  options: ScrollAnimationOptions = {}
): () => void {
  const {
    threshold = 0.1,
    rootMargin = '0px',
    animationClass = 'animate-fade-in-up',
    once = true
  } = options

  // 如果元素已经有动画类，跳过
  if (element.classList.contains(animationClass)) {
    return () => {}
  }

  // 初始状态：隐藏并向下偏移
  element.style.opacity = '0'
  element.style.transform = 'translateY(20px)'
  element.style.transition = 'opacity 0.6s ease-out, transform 0.6s ease-out'

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          // 元素进入视口，添加动画类
          element.style.opacity = '1'
          element.style.transform = 'translateY(0)'
          element.classList.add(animationClass)

          // 如果是一次性动画，停止观察
          if (once) {
            observer.unobserve(element)
          }
        } else if (!once) {
          // 如果不是一次性动画，元素离开视口时重置
          element.style.opacity = '0'
          element.style.transform = 'translateY(20px)'
          element.classList.remove(animationClass)
        }
      })
    },
    {
      threshold,
      rootMargin
    }
  )

  observer.observe(element)

  // 返回清理函数
  return () => {
    observer.unobserve(element)
    observer.disconnect()
  }
}

/**
 * 批量为多个元素添加滚动渐入动画
 * @param selector CSS 选择器
 * @param options 配置选项
 * @returns 批量清理函数
 */
export function observeAllScrollAnimations(
  selector: string,
  options: ScrollAnimationOptions = {}
): () => void {
  const elements = document.querySelectorAll(selector)
  const cleanups: (() => void)[] = []

  elements.forEach((element) => {
    if (element instanceof HTMLElement) {
      const cleanup = observeScrollAnimation(element, options)
      cleanups.push(cleanup)
    }
  })

  // 返回批量清理函数
  return () => {
    cleanups.forEach((cleanup) => cleanup())
  }
}

/**
 * Vue 3 Composition API 组合式函数
 * 在组件中使用滚动渐入动画
 * 
 * @example
 * ```vue
 * <script setup>
 * import { ref, onMounted, onUnmounted } from 'vue'
 * import { useScrollAnimation } from '@/composables/useScrollAnimation'
 * 
 * const cardRef = ref<HTMLElement>()
 * const { observe, cleanup } = useScrollAnimation()
 * 
 * onMounted(() => {
 *   if (cardRef.value) {
 *     observe(cardRef.value)
 *   }
 * })
 * 
 * onUnmounted(() => {
 *   cleanup()
 * })
 * </script>
 * 
 * <template>
 *   <div ref="cardRef">内容</div>
 * </template>
 * ```
 */
export function useScrollAnimation(options: ScrollAnimationOptions = {}) {
  let cleanupFn: (() => void) | null = null

  const observe = (element: HTMLElement) => {
    // 先清理之前的观察
    if (cleanupFn) {
      cleanupFn()
    }
    cleanupFn = observeScrollAnimation(element, options)
  }

  const cleanup = () => {
    if (cleanupFn) {
      cleanupFn()
      cleanupFn = null
    }
  }

  return {
    observe,
    cleanup
  }
}
