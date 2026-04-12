<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import DynamicBackground from '@/components/DynamicBackground.vue'

// 注册 ScrollTrigger 插件
gsap.registerPlugin(ScrollTrigger)

const heroTitleRef = ref<HTMLHeadingElement | null>(null)
const heroSubtitleRef = ref<HTMLParagraphElement | null>(null)
const heroButtonsRef = ref<HTMLDivElement | null>(null)
const featuresSectionRef = ref<HTMLElement | null>(null)
const contentSectionRef = ref<HTMLElement | null>(null)

let mouseGlow: HTMLElement | null = null

onMounted(() => {
  console.log('🚀 HomeViewPOC Advanced mounted')
  
  // 创建鼠标跟随光效
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
 * 创建鼠标跟随光效
 */
function createMouseGlow() {
  mouseGlow = document.createElement('div')
  mouseGlow.className = 'mouse-glow'
  document.body.appendChild(mouseGlow)
  
  const handleMouseMove = (e: MouseEvent) => {
    if (mouseGlow) {
      mouseGlow.style.left = `${e.clientX}px`
      mouseGlow.style.top = `${e.clientY}px`
    }
  }
  
  window.addEventListener('mousemove', handleMouseMove)
}

/**
 * Hero 区域入场动画序列
 */
function animateHeroEntrance() {
  const tl = gsap.timeline({ delay: 0.3 })
  
  // 1. 背景渐变流动（CSS 动画自动播放）
  
  // 2. 标题逐字淡入 + 3D 旋转效果
  if (heroTitleRef.value) {
    const titleText = heroTitleRef.value.textContent || ''
    heroTitleRef.value.innerHTML = ''
    
    // 将标题拆分为单个字符
    const chars = titleText.split('').map((char, index) => {
      const span = document.createElement('span')
      span.textContent = char === ' ' ? '\u00A0' : char
      span.className = 'title-char'
      span.style.display = 'inline-block'
      span.style.opacity = '0'
      span.style.transform = 'translateY(50px) rotateX(90deg)'
      heroTitleRef.value!.appendChild(span)
      return span
    })
    
    // 逐字动画
    tl.to(chars, {
      opacity: 1,
      y: 0,
      rotateX: 0,
      duration: 0.8,
      stagger: 0.05,
      ease: 'power3.out'
    }, 0)
  }
  
  // 3. 副标题淡入上移
  if (heroSubtitleRef.value) {
    tl.fromTo(heroSubtitleRef.value, 
      { opacity: 0, y: 30 },
      { opacity: 1, y: 0, duration: 0.6, ease: 'power2.out' },
      '-=0.4'
    )
  }
  
  // 4. 按钮组弹性出现
  if (heroButtonsRef.value) {
    tl.fromTo(heroButtonsRef.value,
      { opacity: 0, scale: 0.8 },
      { opacity: 1, scale: 1, duration: 0.5, ease: 'back.out(1.7)' },
      '-=0.3'
    )
  }
}

/**
 * 设置滚动触发动画
 */
function setupScrollAnimations() {
  // Feature Cards 滚动触发
  if (featuresSectionRef.value) {
    const featureCards = featuresSectionRef.value.querySelectorAll('.feature-card')
    
    featureCards.forEach((card, index) => {
      gsap.fromTo(card,
        { 
          opacity: 0, 
          y: 60,
          scale: 0.9
        },
        {
          opacity: 1,
          y: 0,
          scale: 1,
          duration: 0.8,
          ease: 'power2.out',
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
  
  // Content Cards 滚动触发
  if (contentSectionRef.value) {
    const contentCards = contentSectionRef.value.querySelectorAll('.content-card')
    
    contentCards.forEach((card, index) => {
      gsap.fromTo(card,
        { 
          opacity: 0, 
          x: index % 2 === 0 ? -60 : 60,
          rotateY: index % 2 === 0 ? -15 : 15
        },
        {
          opacity: 1,
          x: 0,
          rotateY: 0,
          duration: 0.8,
          ease: 'power2.out',
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
  <div class="home-poc-advanced min-h-screen bg-white dark:bg-gray-900 overflow-hidden">
    <!-- 鼠标跟随光效 -->
    <div class="mouse-glow"></div>
    
    <!-- Hero 区域 - 高级动态效果 -->
    <section class="relative overflow-hidden" style="min-height: 700px;">
      <!-- CSS 渐变流动背景（最底层） -->
      <div class="absolute inset-0 animated-gradient-bg"></div>
      
      <!-- Canvas 粒子系统（中间层） -->
      <DynamicBackground class="absolute inset-0">
        <div></div>
      </DynamicBackground>
      
      <!-- 内容层（最上层） -->
      <div class="relative z-10 max-w-content mx-auto px-4 sm:px-6 lg:px-8 py-32 md:py-40">
        <div class="text-center">
          <!-- 主标题 - GSAP 逐字动画 -->
          <h1 
            ref="heroTitleRef"
            class="text-4xl md:text-6xl font-bold text-white mb-6 leading-tight perspective-1000"
            style="transform-style: preserve-3d;"
          >
            沈阳工业大学计算机程序设计社团
          </h1>
          
          <!-- 副标题 -->
          <p 
            ref="heroSubtitleRef"
            class="text-xl md:text-2xl text-white/90 mb-12 max-w-3xl mx-auto"
          >
            培养创新思维 · 提升编程能力 · 打造技术社区
          </p>
          
          <!-- CTA 按钮组 -->
          <div 
            ref="heroButtonsRef"
            class="flex flex-col sm:flex-row gap-4 justify-center items-center"
          >
            <button class="cta-button cta-primary group">
              <span>立即加入</span>
              <svg class="w-5 h-5 ml-2 transform group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6" />
              </svg>
            </button>
            
            <button class="cta-button cta-secondary">
              <span>了解更多</span>
            </button>
          </div>
        </div>
      </div>
      
      <!-- 底部波浪装饰 -->
      <div class="absolute bottom-0 left-0 right-0">
        <svg viewBox="0 0 1440 120" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full">
          <path d="M0 120L60 105C120 90 240 60 360 45C480 30 600 30 720 37.5C840 45 960 60 1080 67.5C1200 75 1320 75 1380 75L1440 75V120H1380C1320 120 1200 120 1080 120C960 120 840 120 720 120C600 120 480 120 360 120C240 120 120 120 60 120H0Z" fill="white"/>
        </svg>
      </div>
    </section>

    <!-- 核心功能展示区 -->
    <section ref="featuresSectionRef" class="py-20 bg-white dark:bg-gray-900">
      <div class="max-w-content mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-16">
          <h2 class="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">
            我们的特色
          </h2>
          <p class="text-lg text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">
            提供全方位的技术学习和实践平台
          </p>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div class="feature-card glass-effect p-8 rounded-2xl hover-lift cursor-pointer">
            <div class="w-16 h-16 bg-gradient-to-br from-blue-500 to-purple-600 rounded-xl flex items-center justify-center mb-6">
              <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4" />
              </svg>
            </div>
            <h3 class="text-xl font-bold text-gray-900 dark:text-white mb-3">技术培训</h3>
            <p class="text-gray-600 dark:text-gray-400">系统的编程课程和技术分享，从入门到进阶</p>
          </div>
          
          <div class="feature-card glass-effect p-8 rounded-2xl hover-lift cursor-pointer">
            <div class="w-16 h-16 bg-gradient-to-br from-green-500 to-teal-600 rounded-xl flex items-center justify-center mb-6">
              <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <h3 class="text-xl font-bold text-gray-900 dark:text-white mb-3">项目实战</h3>
            <p class="text-gray-600 dark:text-gray-400">参与真实项目开发，积累实战经验</p>
          </div>
          
          <div class="feature-card glass-effect p-8 rounded-2xl hover-lift cursor-pointer">
            <div class="w-16 h-16 bg-gradient-to-br from-orange-500 to-red-600 rounded-xl flex items-center justify-center mb-6">
              <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
            <h3 class="text-xl font-bold text-gray-900 dark:text-white mb-3">竞赛指导</h3>
            <p class="text-gray-600 dark:text-gray-400">专业导师指导，助力各类编程竞赛</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 最新动态区 -->
    <section ref="contentSectionRef" class="py-20 bg-gray-50 dark:bg-gray-800">
      <div class="max-w-content mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-16">
          <h2 class="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">
            最新动态
          </h2>
          <p class="text-lg text-gray-600 dark:text-gray-400">了解社团最新活动和资讯</p>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div class="content-card glass-effect rounded-2xl overflow-hidden hover-lift cursor-pointer">
            <div class="h-48 bg-gradient-to-br from-blue-500 to-purple-600"></div>
            <div class="p-6">
              <h3 class="text-xl font-bold text-gray-900 dark:text-white mb-2">春季编程训练营报名开始</h3>
              <p class="text-gray-600 dark:text-gray-400 mb-4">为期两周的密集培训，涵盖前端、后端、算法等多个方向...</p>
              <div class="flex items-center text-sm text-gray-500 dark:text-gray-400">
                <span>2024-03-15</span>
                <span class="mx-2">•</span>
                <span>阅读更多 →</span>
              </div>
            </div>
          </div>
          
          <div class="content-card glass-effect rounded-2xl overflow-hidden hover-lift cursor-pointer">
            <div class="h-48 bg-gradient-to-br from-green-500 to-teal-600"></div>
            <div class="p-6">
              <h3 class="text-xl font-bold text-gray-900 dark:text-white mb-2">ACM 校赛获奖喜报</h3>
              <p class="text-gray-600 dark:text-gray-400 mb-4">我社成员在 ACM 校赛中取得优异成绩，获得多个奖项...</p>
              <div class="flex items-center text-sm text-gray-500 dark:text-gray-400">
                <span>2024-03-10</span>
                <span class="mx-2">•</span>
                <span>阅读更多 →</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* CSS 渐变流动动画 */
.animated-gradient-bg {
  background: linear-gradient(
    135deg, 
    #667eea 0%, 
    #764ba2 25%, 
    #f093fb 50%, 
    #764ba2 75%, 
    #667eea 100%
  );
  background-size: 300% 300%;
  animation: gradientFlow 12s ease-in-out infinite;
  will-change: background-position;
}

@keyframes gradientFlow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 鼠标跟随光效 */
.mouse-glow {
  position: fixed;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.15) 0%, transparent 70%);
  pointer-events: none;
  z-index: 9999;
  transform: translate(-50%, -50%);
  transition: opacity 0.3s;
}

/* 透视效果 */
.perspective-1000 {
  perspective: 1000px;
}

.title-char {
  display: inline-block;
  backface-visibility: hidden;
}

/* CTA 按钮样式 */
.cta-button {
  @apply px-8 py-4 rounded-full font-semibold text-lg transition-all duration-300 flex items-center;
}

.cta-primary {
  @apply bg-white text-purple-600 hover:bg-gray-100 hover:shadow-lg hover:scale-105;
}

.cta-secondary {
  @apply border-2 border-white text-white hover:bg-white/10 hover:scale-105;
}

/* 玻璃态效果 */
.glass-effect {
  @apply bg-white/80 dark:bg-gray-800/80 backdrop-blur-lg border border-white/20 dark:border-gray-700/30 shadow-lg;
}

/* 悬停抬升效果 */
.hover-lift {
  @apply transition-all duration-300;
}

.hover-lift:hover {
  @apply -translate-y-2 shadow-2xl;
}
</style>
