<script setup lang="ts">
import confetti from 'canvas-confetti'

interface Props {
  // 触发条件
  trigger?: boolean
  
  // 动画配置
  particleCount?: number     // 粒子数量（默认 80，推荐 50-100）
  spread?: number           // 扩散角度（默认 70）
  startVelocity?: number    // 初始速度（默认 30）
  gravity?: number          // 重力（默认 1.2，略大于 1 让粒子更快落下）
  decay?: number            // 衰减率（默认 0.9，控制粒子消失速度）
  scalar?: number           // 缩放比例（默认 1，移动端可设为 0.75）
  colors?: string[]         // 颜色数组（推荐使用柔和的品牌色）
  shapes?: ('square' | 'circle')[]  // 形状（默认 ['square', 'circle']）
  useRibbon?: boolean       // 是否使用飘带效果（默认 true）
}

const props = withDefaults(defineProps<Props>(), {
  trigger: false,
  particleCount: 80,        // 降低到 80，更轻盈
  spread: 70,
  startVelocity: 30,
  gravity: 1.2,             // 增加重力，让粒子更快落下
  decay: 0.9,               // 添加衰减，粒子逐渐消失
  scalar: 1,
  colors: () => [
    '#60A5FA',  // 蓝色（主色调）
    '#34D399',  // 绿色
    '#FBBF24',  // 黄色
    '#F87171',  // 红色
    '#A78BFA',  // 紫色
    '#F472B6'   // 粉色
  ],
  shapes: () => ['square', 'circle'],
  useRibbon: true           // 默认启用飘带效果
})

// 检测是否支持 prefers-reduced-motion
const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches

// 检测设备类型
const isMobile = /iPhone|iPad|iPod|Android/i.test(navigator.userAgent)

// 监听 trigger 变化，触发动画
watch(
  () => props.trigger,
  (newVal) => {
    if (newVal && !prefersReducedMotion) {
      fireConfetti()
    }
  }
)

// 发射彩带 - 采用现代简约风格
const fireConfetti = () => {
  const canvas = document.createElement('canvas')
  canvas.style.position = 'fixed'
  canvas.style.top = '0'
  canvas.style.left = '0'
  canvas.style.width = '100%'
  canvas.style.height = '100%'
  canvas.style.pointerEvents = 'none'
  canvas.style.zIndex = '9999'
  document.body.appendChild(canvas)

  const myConfetti = confetti.create(canvas, {
    resize: true,
    useWorker: true
  })

  // 移动端减少粒子数量
  const actualParticleCount = isMobile ? Math.floor(props.particleCount * 0.6) : props.particleCount
  const actualScalar = isMobile ? 0.75 : props.scalar

  // 单次爆发式发射（更符合现代 UX）
  // 左侧发射 - 包含普通粒子和飘带
  myConfetti({
    particleCount: actualParticleCount / 2,
    angle: 60,
    spread: props.spread,
    origin: { x: 0, y: 0.6 },  // 从左下角发射
    colors: props.colors,
    shapes: props.shapes,
    startVelocity: props.startVelocity,
    gravity: props.gravity,
    decay: props.decay,
    scalar: actualScalar,
    ticks: 200  // 粒子存活时间
  })

  // 右侧发射 - 包含普通粒子和飘带
  myConfetti({
    particleCount: actualParticleCount / 2,
    angle: 120,
    spread: props.spread,
    origin: { x: 1, y: 0.6 },  // 从右下角发射
    colors: props.colors,
    shapes: props.shapes,
    startVelocity: props.startVelocity,
    gravity: props.gravity,
    decay: props.decay,
    scalar: actualScalar,
    ticks: 200
  })

  // 如果启用飘带效果，额外发射一些长条形飘带
  if (props.useRibbon) {
    const ribbonCount = isMobile ? 15 : 25  // 飘带数量
    
    // 左侧飘带 - 更高、更远、更持久
    setTimeout(() => {
      myConfetti({
        particleCount: ribbonCount,
        angle: 55,
        spread: 60,
        origin: { x: 0, y: 0.7 },
        colors: props.colors,
        shapes: ['star'],  // 使用星形模拟飘带效果
        startVelocity: 35,  // 更高的初速度
        gravity: 0.8,       // 更小的重力，飘得更远
        decay: 0.92,        // 更慢的衰减，持续更久
        scalar: actualScalar * 1.2,  // 稍大一点
        ticks: 300  // 更长的存活时间
      })
    }, 100)

    // 右侧飘带 - 更高、更远、更持久
    setTimeout(() => {
      myConfetti({
        particleCount: ribbonCount,
        angle: 125,
        spread: 60,
        origin: { x: 1, y: 0.7 },
        colors: props.colors,
        shapes: ['star'],
        startVelocity: 35,
        gravity: 0.8,
        decay: 0.92,
        scalar: actualScalar * 1.2,
        ticks: 300
      })
    }, 100)
  }

  // 动画结束后移除 canvas（根据是否有飘带调整时间）
  const cleanupDelay = props.useRibbon ? 4000 : 3000
  setTimeout(() => {
    canvas.remove()
  }, cleanupDelay)
}

// 暴露方法供外部调用
defineExpose({
  fire: fireConfetti
})
</script>

<template>
  <!-- 这是一个纯逻辑组件，不需要渲染任何内容 -->
  <slot />
</template>

<style scoped>
/* 无样式 */
</style>
