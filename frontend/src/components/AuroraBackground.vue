<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const canvasRef = ref<HTMLCanvasElement | null>(null)
let animationId: number | null = null
let ctx: CanvasRenderingContext2D | null = null

// 流体 blob 配置
interface Blob {
  x: number
  y: number
  radius: number
  color: string
  vx: number
  vy: number
  phase: number
  speed: number
}

const blobs: Blob[] = []
const blobCount = 6

// 初始化流体 blob
function initBlobs() {
  if (!canvasRef.value) return
  
  const width = canvasRef.value.width
  const height = canvasRef.value.height
  
  // 现代科技配色方案
  const colors = [
    'rgba(139, 92, 246, 0.4)',   // 极光紫
    'rgba(14, 165, 233, 0.4)',   // 电蓝
    'rgba(236, 72, 153, 0.35)',  // 粉红
    'rgba(6, 182, 212, 0.35)',   // 青色
    'rgba(99, 102, 241, 0.3)',   // 靛蓝
    'rgba(244, 63, 94, 0.3)'     // 玫瑰红
  ]
  
  blobs.length = 0
  for (let i = 0; i < blobCount; i++) {
    blobs.push({
      x: Math.random() * width,
      y: Math.random() * height,
      radius: Math.random() * 200 + 150,
      color: colors[i % colors.length],
      vx: (Math.random() - 0.5) * 0.3,
      vy: (Math.random() - 0.5) * 0.3,
      phase: Math.random() * Math.PI * 2,
      speed: Math.random() * 0.002 + 0.001
    })
  }
}

// 绘制单个 blob（使用径向渐变实现柔和边缘）
function drawBlob(blob: Blob, time: number) {
  if (!ctx || !canvasRef.value) return
  
  // 动态半径（呼吸效果）
  const breatheRadius = blob.radius + Math.sin(time * blob.speed + blob.phase) * 30
  
  const gradient = ctx.createRadialGradient(
    blob.x, blob.y, 0,
    blob.x, blob.y, breatheRadius
  )
  
  // 提取颜色中的 RGB 值
  const rgbaMatch = blob.color.match(/rgba\((\d+),\s*(\d+),\s*(\d+),\s*([\d.]+)\)/)
  if (rgbaMatch) {
    const [, r, g, b, a] = rgbaMatch
    gradient.addColorStop(0, `rgba(${r}, ${g}, ${b}, ${parseFloat(a)})`)
    gradient.addColorStop(0.5, `rgba(${r}, ${g}, ${b}, ${parseFloat(a) * 0.5})`)
    gradient.addColorStop(1, `rgba(${r}, ${g}, ${b}, 0)`)
  }
  
  ctx.fillStyle = gradient
  ctx.beginPath()
  ctx.arc(blob.x, blob.y, breatheRadius, 0, Math.PI * 2)
  ctx.fill()
}

// 更新 blob 位置
function updateBlobs() {
  if (!canvasRef.value) return
  
  const width = canvasRef.value.width
  const height = canvasRef.value.height
  
  blobs.forEach(blob => {
    // 缓慢移动
    blob.x += blob.vx
    blob.y += blob.vy
    
    // 边界反弹
    if (blob.x < -blob.radius) blob.x = width + blob.radius
    if (blob.x > width + blob.radius) blob.x = -blob.radius
    if (blob.y < -blob.radius) blob.y = height + blob.radius
    if (blob.y > height + blob.radius) blob.y = -blob.radius
  })
}

// 主渲染循环
function render() {
  if (!ctx || !canvasRef.value) return
  
  const time = Date.now()
  
  // 清空画布
  ctx.clearRect(0, 0, canvasRef.value.width, canvasRef.value.height)
  
  // 设置混合模式为 screen（实现光效叠加）
  ctx.globalCompositeOperation = 'screen'
  
  // 绘制所有 blob
  blobs.forEach(blob => drawBlob(blob, time))
  
  // 重置混合模式
  ctx.globalCompositeOperation = 'source-over'
  
  // 添加噪点纹理（可选，增加质感）
  addNoiseTexture()
  
  animationId = requestAnimationFrame(render)
}

// 添加细微的噪点纹理
function addNoiseTexture() {
  if (!ctx || !canvasRef.value) return
  
  const imageData = ctx.getImageData(0, 0, canvasRef.value.width, canvasRef.value.height)
  const data = imageData.data
  
  for (let i = 0; i < data.length; i += 4) {
    const noise = (Math.random() - 0.5) * 10
    data[i] += noise     // R
    data[i + 1] += noise // G
    data[i + 2] += noise // B
  }
  
  ctx.putImageData(imageData, 0, 0)
}

// 调整画布大小
function resize() {
  if (!canvasRef.value) return
  
  const dpr = window.devicePixelRatio || 1
  const rect = canvasRef.value.getBoundingClientRect()
  
  canvasRef.value.width = rect.width * dpr
  canvasRef.value.height = rect.height * dpr
  
  ctx = canvasRef.value.getContext('2d')
  if (ctx) {
    ctx.scale(dpr, dpr)
  }
  
  initBlobs()
}

onMounted(() => {
  console.log('🌊 AuroraBackground mounted')
  
  resize()
  render()
  
  window.addEventListener('resize', resize)
})

onUnmounted(() => {
  if (animationId !== null) {
    cancelAnimationFrame(animationId)
  }
  window.removeEventListener('resize', resize)
  console.log('🌊 AuroraBackground unmounted')
})
</script>

<template>
  <div class="aurora-background">
    <!-- 基础深色背景 -->
    <div class="base-gradient"></div>
    
    <!-- Canvas 流体动画层 -->
    <canvas ref="canvasRef" class="fluid-canvas"></canvas>
    
    <!-- 内容插槽 -->
    <div class="content-layer">
      <slot></slot>
    </div>
  </div>
</template>

<style scoped>
.aurora-background {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

/* 基础渐变背景（深色科技感） */
.base-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    #0f0c29 0%,
    #302b63 50%,
    #24243e 100%
  );
  z-index: 0;
}

/* Canvas 流体层 */
.fluid-canvas {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  pointer-events: none;
  filter: blur(60px) contrast(1.2);
  will-change: transform;
}

/* 内容层 */
.content-layer {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
}
</style>
