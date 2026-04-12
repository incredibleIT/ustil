<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import AuroraBackground from '@/components/AuroraBackground.vue'

// 注册 ScrollTrigger 插件
gsap.registerPlugin(ScrollTrigger)

const heroTitleRef = ref<HTMLHeadingElement | null>(null)
const heroSubtitleRef = ref<HTMLParagraphElement | null>(null)
const heroButtonsRef = ref<HTMLDivElement | null>(null)
const featuresSectionRef = ref<HTMLElement | null>(null)
const contentSectionRef = ref<HTMLElement | null>(null)

let mouseGlow: HTMLElement | null = null

onMounted(() => {
  console.log('🚀 HomeViewUltimate mounted')
  
  // 创建鼠标跟随光效（更柔和）
  createMouseGlow()
  
  // 执行 Hero 区域入场动画序列
  animateHeroEntrance()
  
  // 设置滚动触发动画
  setupScrollAnimations()
})

onUnmounted(() => {
  // 清理所有 ScrollTrigger 实例
  ScrollTrigger.getAll().forEach(trigger => trigger.kill())
  
  // 移除鼠标光效
  if (mouseGlow && mouseGlow.parentNode) {
    mouseGlow.parentNode.removeChild(mouseGlow)
  }
})

/**
 * 创建鼠标跟随光效（升级版 - 更大更柔和）
 */
function createMouseGlow() {
  mouseGlow = document.createElement('div')
  mouseGlow.className = 'mouse-glow-ultimate'
  document.body.appendChild(mouseGlow)
  
  let currentX = 0
  let currentY = 0
  let targetX = 0
  let targetY = 0
  
  const handleMouseMove = (e: MouseEvent) => {
    targetX = e.clientX
    targetY = e.clientY
  }
  
  // 使用 lerp 实现平滑跟随
  const animate = () => {
    currentX += (targetX - currentX) * 0.1
    currentY += (targetY - currentY) * 0.1
    
    if (mouseGlow) {
      mouseGlow.style.left = `${currentX}px`
      mouseGlow.style.top = `${currentY}px`
    }
    
    requestAnimationFrame(animate)
  }
  
  window.addEventListener('mousemove', handleMouseMove)
  animate()
}

/**
 * Hero 区域入场动画序列（增强版）
 */
function animateHeroEntrance() {
  const tl = gsap.timeline({ delay: 0.5 })
  
  // 1. 标题逐字淡入 + 3D 旋转 + 缩放效果
  if (heroTitleRef.value) {
    const titleText = heroTitleRef.value.textContent || ''
    heroTitleRef.value.innerHTML = ''
    
    // 将标题拆分为单个字符
    const chars = titleText.split('').map((char, index) => {
      const span = document.createElement('span')
      span.textContent = char === ' ' ? '\u00A0' : char
      span.className = 'title-char-ultimate'
      span.style.display = 'inline-block'
      span.style.opacity = '0'
      span.style.transform = 'translateY(80px) rotateX(90deg) scale(0.5)'
      heroTitleRef.value!.appendChild(span)
      return span
    })
    
    // 逐字动画（更夸张的效果）
    tl.to(chars, {
      opacity: 1,
      y: 0,
      rotateX: 0,
      scale: 1,
      duration: 1,
      stagger: 0.04,
      ease: 'elastic.out(1, 0.5)'
    }, 0)
  }
  
  // 2. 副标题淡入上移 + 模糊清除
  if (heroSubtitleRef.value) {
    tl.fromTo(heroSubtitleRef.value, 
      { opacity: 0, y: 40, filter: 'blur(10px)' },
      { opacity: 1, y: 0, filter: 'blur(0px)', duration: 0.8, ease: 'power2.out' },
      '-=0.6'
    )
  }
  
  // 3. 按钮组弹性出现 + 光晕效果
  if (heroButtonsRef.value) {
    tl.fromTo(heroButtonsRef.value,
      { opacity: 0, scale: 0.5, rotation: -10 },
      { opacity: 1, scale: 1, rotation: 0, duration: 0.8, ease: 'back.out(1.7)' },
      '-=0.4'
    )
  }
}

/**
 * 设置滚动触发动画（增强版）
 */
function setupScrollAnimations() {
  // Feature Cards 滚动触发（带视差效果）
  if (featuresSectionRef.value) {
    const featureCards = featuresSectionRef.value.querySelectorAll('.feature-card-ultimate')
    
    featureCards.forEach((card, index) => {
      gsap.fromTo(card,
        { 
          opacity: 0, 
          y: 80,
          scale: 0.85,
          rotateY: -15
        },
        {
          opacity: 1,
          y: 0,
          scale: 1,
          rotateY: 0,
          duration: 1,
          ease: 'power3.out',
          scrollTrigger: {
            trigger: card,
            start: 'top 85%',
            end: 'top 50%',
            toggleActions: 'play none none reverse'
          }
        }
      )
    })
  }
  
  // Content Cards 滚动触发（左右交替 + 3D 翻转）
  if (contentSectionRef.value) {
    const contentCards = contentSectionRef.value.querySelectorAll('.content-card-ultimate')
    
    contentCards.forEach((card, index) => {
      gsap.fromTo(card,
        { 
          opacity: 0, 
          x: index % 2 === 0 ? -100 : 100,
          rotateY: index % 2 === 0 ? -25 : 25,
          z: -100
        },
        {
          opacity: 1,
          x: 0,
          rotateY: 0,
          z: 0,
          duration: 1,
          ease: 'power3.out',
          scrollTrigger: {
            trigger: card,
            start: 'top 85%',
            end: 'top 50%',
            toggleActions: 'play none none reverse'
          }
        }
      )
    })
  }
}
</script>

<template>
  <div class="home-ultimate min-h-screen overflow-hidden">
    <!-- Hero 区域 - 极光背景 + GSAP 动画 -->
    <AuroraBackground class="relative" style="min-height: 800px;">
      <div class="max-w-content mx-auto px-4 sm:px-6 lg:px-8 py-32 md:py-48">
        <div class="text-center">
          <!-- 主标题 - GSAP 逐字动画 -->
          <h1 
            ref="heroTitleRef"
            class="text-5xl md:text-7xl font-bold text-white mb-8 leading-tight perspective-2000"
            style="transform-style: preserve-3d; text-shadow: 0 0 40px rgba(139, 92, 246, 0.5);"
          >
            沈阳工业大学计算机程序设计社团
          </h1>
          
          <!-- 副标题 -->
          <p 
            ref="heroSubtitleRef"
            class="text-xl md:text-2xl text-white/90 mb-16 max-w-3xl mx-auto font-light tracking-wide"
            style="text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);"
          >
            培养创新思维 · 提升编程能力 · 打造技术社区
          </p>
          
          <!-- CTA 按钮组 -->
          <div 
            ref="heroButtonsRef"
            class="flex flex-col sm:flex-row gap-6 justify-center items-center"
          >
            <button class="cta-button-ultimate cta-primary-ultimate group">
              <span>立即加入</span>
              <svg class="w-5 h-5 ml-2 transform group-hover:translate-x-2 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6" />
              </svg>
            </button>
            
            <button class="cta-button-ultimate cta-secondary-ultimate">
              <span>了解更多</span>
            </button>
          </div>
        </div>
      </div>
      
      <!-- 底部波浪装饰（半透明） -->
      <div class="absolute bottom-0 left-0 right-0 opacity-30">
        <svg viewBox="0 0 1440 120" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full">
          <path d="M0 120L60 105C120 90 240 60 360 45C480 30 600 30 720 37.5C840 45 960 60 1080 67.5C1200 75 1320 75 1380 75L1440 75V120H1380C1320 120 1200 120 1080 120C960 120 840 120 720 120C600 120 480 120 360 120C240 120 120 120 60 120H0Z" fill="white"/>
        </svg>
      </div>
    </AuroraBackground>

    <!-- 核心功能展示区 -->
    <section ref="featuresSectionRef" class="py-24 bg-gradient-to-b from-gray-50 to-white dark:from-gray-900 dark:to-gray-800">
      <div class="max-w-content mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-20">
          <h2 class="text-4xl md:text-5xl font-bold text-gray-900 dark:text-white mb-6">
            我们的特色
          </h2>
          <p class="text-lg text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">
            提供全方位的技术学习和实践平台
          </p>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-3 gap-10">
          <div class="feature-card-ultimate glass-effect-ultimate p-10 rounded-3xl hover-lift-ultimate cursor-pointer group">
            <div class="w-20 h-20 bg-gradient-to-br from-purple-500 to-blue-600 rounded-2xl flex items-center justify-center mb-8 group-hover:scale-110 transition-transform duration-300 shadow-lg">
              <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4" />
              </svg>
            </div>
            <h3 class="text-2xl font-bold text-gray-900 dark:text-white mb-4">技术培训</h3>
            <p class="text-gray-600 dark:text-gray-400 text-lg leading-relaxed">系统的编程课程和技术分享，从入门到进阶</p>
          </div>
          
          <div class="feature-card-ultimate glass-effect-ultimate p-10 rounded-3xl hover-lift-ultimate cursor-pointer group">
            <div class="w-20 h-20 bg-gradient-to-br from-green-500 to-teal-600 rounded-2xl flex items-center justify-center mb-8 group-hover:scale-110 transition-transform duration-300 shadow-lg">
              <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <h3 class="text-2xl font-bold text-gray-900 dark:text-white mb-4">项目实战</h3>
            <p class="text-gray-600 dark:text-gray-400 text-lg leading-relaxed">参与真实项目开发，积累实战经验</p>
          </div>
          
          <div class="feature-card-ultimate glass-effect-ultimate p-10 rounded-3xl hover-lift-ultimate cursor-pointer group">
            <div class="w-20 h-20 bg-gradient-to-br from-orange-500 to-red-600 rounded-2xl flex items-center justify-center mb-8 group-hover:scale-110 transition-transform duration-300 shadow-lg">
              <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
            <h3 class="text-2xl font-bold text-gray-900 dark:text-white mb-4">竞赛指导</h3>
            <p class="text-gray-600 dark:text-gray-400 text-lg leading-relaxed">专业导师指导，助力各类编程竞赛</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 最新动态区 -->
    <section ref="contentSectionRef" class="py-24 bg-gradient-to-b from-white to-gray-50 dark:from-gray-800 dark:to-gray-900">
      <div class="max-w-content mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-20">
          <h2 class="text-4xl md:text-5xl font-bold text-gray-900 dark:text-white mb-6">
            最新动态
          </h2>
          <p class="text-lg text-gray-600 dark:text-gray-400">了解社团最新活动和资讯</p>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-10">
          <div class="content-card-ultimate glass-effect-ultimate rounded-3xl overflow-hidden hover-lift-ultimate cursor-pointer group">
            <div class="h-56 bg-gradient-to-br from-purple-500 via-pink-500 to-red-500 relative overflow-hidden">
              <div class="absolute inset-0 bg-black/20 group-hover:bg-black/10 transition-colors"></div>
            </div>
            <div class="p-8">
              <h3 class="text-2xl font-bold text-gray-900 dark:text-white mb-3">春季编程训练营报名开始</h3>
              <p class="text-gray-600 dark:text-gray-400 mb-6 text-lg leading-relaxed">为期两周的密集培训，涵盖前端、后端、算法等多个方向...</p>
              <div class="flex items-center text-sm text-gray-500 dark:text-gray-400">
                <span>2024-03-15</span>
                <span class="mx-2">•</span>
                <span class="group-hover:text-purple-600 transition-colors">阅读更多 →</span>
              </div>
            </div>
          </div>
          
          <div class="content-card-ultimate glass-effect-ultimate rounded-3xl overflow-hidden hover-lift-ultimate cursor-pointer group">
            <div class="h-56 bg-gradient-to-br from-blue-500 via-cyan-500 to-teal-500 relative overflow-hidden">
              <div class="absolute inset-0 bg-black/20 group-hover:bg-black/10 transition-colors"></div>
            </div>
            <div class="p-8">
              <h3 class="text-2xl font-bold text-gray-900 dark:text-white mb-3">ACM 校赛获奖喜报</h3>
              <p class="text-gray-600 dark:text-gray-400 mb-6 text-lg leading-relaxed">我社成员在 ACM 校赛中取得优异成绩，获得多个奖项...</p>
              <div class="flex items-center text-sm text-gray-500 dark:text-gray-400">
                <span>2024-03-10</span>
                <span class="mx-2">•</span>
                <span class="group-hover:text-blue-600 transition-colors">阅读更多 →</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* 鼠标跟随光效（升级版 - 更大更柔和） */
.mouse-glow-ultimate {
  position: fixed;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background: radial-gradient(circle, 
    rgba(139, 92, 246, 0.15) 0%,
    rgba(14, 165, 233, 0.1) 30%,
    transparent 70%
  );
  pointer-events: none;
  z-index: 9999;
  transform: translate(-50%, -50%);
  transition: opacity 0.3s;
  filter: blur(40px);
}

/* 透视效果 */
.perspective-2000 {
  perspective: 2000px;
}

.title-char-ultimate {
  display: inline-block;
  backface-visibility: hidden;
  will-change: transform, opacity;
}

/* CTA 按钮样式（升级版） */
.cta-button-ultimate {
  @apply px-10 py-5 rounded-full font-semibold text-lg transition-all duration-500 flex items-center shadow-lg;
}

.cta-primary-ultimate {
  @apply bg-gradient-to-r from-purple-600 to-blue-600 text-white;
  box-shadow: 0 10px 40px rgba(139, 92, 246, 0.4);
}

.cta-primary-ultimate:hover {
  @apply scale-105;
  box-shadow: 0 15px 50px rgba(139, 92, 246, 0.6);
  transform: translateY(-2px);
}

.cta-secondary-ultimate {
  @apply border-2 border-white/50 text-white backdrop-blur-sm;
  background: rgba(255, 255, 255, 0.1);
}

.cta-secondary-ultimate:hover {
  @apply bg-white/20 scale-105;
  border-color: white;
  transform: translateY(-2px);
}

/* 玻璃态效果（升级版） */
.glass-effect-ultimate {
  @apply bg-white/70 dark:bg-gray-800/70 backdrop-blur-xl border border-white/30 dark:border-gray-700/30;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

/* 悬停抬升效果（升级版） */
.hover-lift-ultimate {
  @apply transition-all duration-500;
}

.hover-lift-ultimate:hover {
  @apply -translate-y-3;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}
</style>
