<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { ParticleBackground } from '@/utils/particleBackground'

const canvasRef = ref<HTMLCanvasElement | null>(null)
let particleSystem: ParticleBackground | null = null

onMounted(() => {
  if (canvasRef.value) {
    console.log('🎨 DynamicBackground mounted, canvas:', canvasRef.value)
    
    // 根据设备性能调整粒子数量
    const isMobile = window.innerWidth < 768
    const particleCount = isMobile ? 40 : 80
    
    console.log(`✨ Initializing particles: ${particleCount} count, mobile: ${isMobile}`)
    
    particleSystem = new ParticleBackground(canvasRef.value, {
      particleCount,
      particleColor: 'rgba(255, 255, 255, 0.6)',
      lineColor: 'rgba(255, 255, 255, 0.15)',
      speed: 0.5,
      connectionDistance: 120
    })
    
    particleSystem.start()
    console.log('✅ Particle system started')
  } else {
    console.error('❌ Canvas ref is null')
  }
})

onUnmounted(() => {
  if (particleSystem) {
    particleSystem.destroy()
  }
})
</script>

<template>
  <div class="dynamic-background">
    <canvas ref="canvasRef" class="particle-canvas"></canvas>
    <slot></slot>
  </div>
</template>

<style scoped>
.dynamic-background {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.particle-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1; /* 提高层级 */
  pointer-events: none; /* 允许点击穿透到下层内容 */
}
</style>
