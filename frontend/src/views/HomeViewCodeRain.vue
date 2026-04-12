<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import Lenis from 'lenis'

// 注册 ScrollTrigger 插件
gsap.registerPlugin(ScrollTrigger)

const heroTitleRef = ref<HTMLHeadingElement | null>(null)
const heroSubtitleRef = ref<HTMLParagraphElement | null>(null)
const heroButtonsRef = ref<HTMLDivElement | null>(null)
const scrollHintRef = ref<HTMLDivElement | null>(null)
const codeRainCanvasRef = ref<HTMLCanvasElement | null>(null)
const featuresSectionRef = ref<HTMLElement | null>(null)
const contentSectionRef = ref<HTMLElement | null>(null)

let lenis: Lenis | null = null
let animationId: number | null = null
let drops: CodeDrop[] = []
let canvasWidth = 0
let canvasHeight = 0

// 编程关键词字符集
const codeCharacters = 'ifelseforwhileclassfunctionreturnvarletconstimportexportdefaultasyncawaittrycatchthrownewthis{}[]()<>+=-*/&|^~!?:;,'

interface CodeDrop {
  x: number
  y: number
  speed: number
  characters: string[]
  fontSize: number
  opacity: number
}

onMounted(() => {
  console.log('🚀 HomeViewCodeRain mounted')
  
  // 初始化 Lenis 平滑滚动
  initLenis()
  
  // 初始化代码雨效果
  initCodeRain()
  
  // 执行 Hero 区域入场动画序列
  animateHeroEntrance()
  
  // 设置滚动触发动画
  setupScrollAnimations()
  
  // 添加滚动监听同步 Lenis 和 ScrollTrigger
  setupLenisScrollTrigger()
})

onUnmounted(() => {
  // 清理 Lenis
  if (lenis) {
    lenis.destroy()
  }
  
  // 清理所有 ScrollTrigger 实例
  ScrollTrigger.getAll().forEach(trigger => trigger.kill())
  
  // 清理代码雨动画
  if (animationId !== null) {
    cancelAnimationFrame(animationId)
  }
  
  // 移除事件监听
  window.removeEventListener('resize', handleResize)
  
  console.log('🚀 HomeViewCodeRain unmounted')
})

/**
 * 初始化 Lenis 平滑滚动
 */
function initLenis() {
  lenis = new Lenis({
    duration: 1.2,
    easing: (t: number) => Math.min(1, 1.001 - Math.pow(2, -10 * t)),
    orientation: 'vertical',
    gestureOrientation: 'vertical',
    smoothWheel: true,
    wheelMultiplier: 1,
    touchMultiplier: 2,
    infinite: false,
  })
  
  console.log('✅ Lenis smooth scroll initialized')
}

/**
 * 初始化代码雨效果
 */
function initCodeRain() {
  if (!codeRainCanvasRef.value) return
  
  const canvas = codeRainCanvasRef.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  // 设置 canvas 尺寸
  canvasWidth = window.innerWidth
  canvasHeight = window.innerHeight
  canvas.width = canvasWidth
  canvas.height = canvasHeight
  
  // 创建代码雨滴
  const fontSize = 16
  const columns = Math.floor(canvasWidth / fontSize)
  drops = []
  
  for (let i = 0; i < columns; i++) {
    // 随机生成每列的字符序列
    const charCount = Math.floor(Math.random() * 15) + 10
    const characters: string[] = []
    
    for (let j = 0; j < charCount; j++) {
      const randomIndex = Math.floor(Math.random() * codeCharacters.length)
      characters.push(codeCharacters[randomIndex])
    }
    
    drops.push({
      x: i * fontSize,
      y: Math.random() * -canvasHeight, // 随机起始位置（屏幕上方）
      speed: Math.random() * 2 + 1, // 随机下落速度
      characters: characters,
      fontSize: fontSize,
      opacity: Math.random() * 0.3 + 0.1 // 随机透明度
    })
  }
  
  // 添加窗口大小变化监听
  window.addEventListener('resize', handleResize)
  
  // 启动动画循环
  animateCodeRain()
  
  console.log('✅ Code rain initialized with', columns, 'columns')
}

/**
 * 代码雨动画循环
 */
function animateCodeRain() {
  if (!codeRainCanvasRef.value) return
  
  const canvas = codeRainCanvasRef.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  // 半透明黑色覆盖，产生拖尾效果
  ctx.fillStyle = 'rgba(255, 255, 255, 0.05)'
  ctx.fillRect(0, 0, canvasWidth, canvasHeight)
  
  // 绘制代码雨
  drops.forEach((drop, index) => {
    // 绘制字符
    drop.characters.forEach((char, charIndex) => {
      const charY = drop.y - charIndex * drop.fontSize
      
      // 只绘制在屏幕范围内的字符
      if (charY > 0 && charY < canvasHeight) {
        // 渐变透明度（越新越亮）
        const opacity = drop.opacity * (1 - charIndex / drop.characters.length)
        
        // 颜色选择（蓝紫色系）
        const colorIndex = charIndex / drop.characters.length
        let color: string
        
        if (colorIndex < 0.3) {
          // 头部：更亮的靛蓝色
          color = `rgba(99, 102, 241, ${opacity + 0.2})`
        } else if (colorIndex < 0.7) {
          // 中部：紫色
          color = `rgba(139, 92, 246, ${opacity})`
        } else {
          // 尾部：淡紫色
          color = `rgba(167, 139, 250, ${opacity * 0.5})`
        }
        
        ctx.fillStyle = color
        ctx.font = `${drop.fontSize}px monospace`
        ctx.fillText(char, drop.x, charY)
      }
    })
    
    // 更新位置
    drop.y += drop.speed
    
    // 如果完全离开屏幕，重置到上方
    if (drop.y - drop.characters.length * drop.fontSize > canvasHeight) {
      drop.y = Math.random() * -200
      drop.speed = Math.random() * 2 + 1
      
      // 随机更新字符序列
      const charCount = Math.floor(Math.random() * 15) + 10
      drop.characters = []
      for (let j = 0; j < charCount; j++) {
        const randomIndex = Math.floor(Math.random() * codeCharacters.length)
        drop.characters.push(codeCharacters[randomIndex])
      }
    }
  })
  
  // 继续动画循环
  animationId = requestAnimationFrame(animateCodeRain)
}

/**
 * 处理窗口大小变化
 */
function handleResize() {
  if (!codeRainCanvasRef.value) return
  
  canvasWidth = window.innerWidth
  canvasHeight = window.innerHeight
  codeRainCanvasRef.value.width = canvasWidth
  codeRainCanvasRef.value.height = canvasHeight
  
  // 重新初始化代码雨
  initCodeRain()
}

/**
 * 执行 Hero 区域入场动画序列
 */
function animateHeroEntrance() {
  const tl = gsap.timeline({ delay: 0.3 })
  
  // 背景 canvas 淡入
  if (codeRainCanvasRef.value) {
    tl.fromTo(codeRainCanvasRef.value,
      { opacity: 0 },
      { opacity: 1, duration: 1.5, ease: 'power2.out' },
      0
    )
  }
  
  // 主标题动画（从下方滑入 + 3D 倾斜）
  if (heroTitleRef.value) {
    tl.fromTo(heroTitleRef.value,
      { 
        y: 100, 
        opacity: 0, 
        rotateX: -15,
        scale: 0.9
      },
      { 
        y: 0, 
        opacity: 1, 
        rotateX: 0,
        scale: 1,
        duration: 1.2,
        ease: 'power4.out'
      },
      0.2
    )
  }
  
  // 副标题动画
  if (heroSubtitleRef.value) {
    tl.fromTo(heroSubtitleRef.value,
      { y: 50, opacity: 0 },
      { y: 0, opacity: 1, duration: 1, ease: 'power3.out' },
      0.5
    )
  }
  
  // 按钮动画
  if (heroButtonsRef.value) {
    tl.fromTo(heroButtonsRef.value,
      { y: 30, opacity: 0 },
      { y: 0, opacity: 1, duration: 0.8, ease: 'power3.out' },
      0.7
    )
  }
  
  // 滚动提示动画
  if (scrollHintRef.value) {
    tl.fromTo(scrollHintRef.value,
      { y: 20, opacity: 0 },
      { 
        y: 0, 
        opacity: 1, 
        duration: 0.6,
        ease: 'power3.out',
        onComplete: () => {
          // 添加持续的上下浮动动画
          gsap.to(scrollHintRef.value, {
            y: 10,
            duration: 1.5,
            ease: 'power1.inOut',
            repeat: -1,
            yoyo: true
          })
        }
      },
      1
    )
  }
  
  console.log('✅ Hero entrance animation started')
}

/**
 * 设置滚动触发动画
 */
function setupScrollAnimations() {
  // Feature Cards 滚动触发（从下方翻转进入）
  if (featuresSectionRef.value) {
    const featureCards = featuresSectionRef.value.querySelectorAll('.feature-card-jiejoe')
    
    featureCards.forEach((card, index) => {
      gsap.fromTo(card,
        { 
          opacity: 0, 
          y: 80, 
          rotateX: -20,
          scale: 0.9
        },
        {
          opacity: 1,
          y: 0,
          rotateX: 0,
          scale: 1,
          duration: 0.7,
          ease: 'power3.out',
          scrollTrigger: {
            trigger: card,
            start: 'top 85%',
            end: 'top 65%',
            toggleActions: 'play none none reverse'
          }
        }
      )
    })
  }
  
  // Content Cards 滚动触发（左右交替 + 3D 翻转）
  if (contentSectionRef.value) {
    const contentCards = contentSectionRef.value.querySelectorAll('.content-card-jiejoe')
    
    contentCards.forEach((card, index) => {
      gsap.fromTo(card,
        { 
          opacity: 0, 
          x: index % 2 === 0 ? -100 : 100, 
          rotateY: index % 2 === 0 ? -30 : 30,
          z: -100
        },
        {
          opacity: 1,
          x: 0,
          rotateY: 0,
          z: 0,
          duration: 0.8,
          ease: 'power3.out',
          scrollTrigger: {
            trigger: card,
            start: 'top 85%',
            end: 'top 65%',
            toggleActions: 'play none none reverse'
          }
        }
      )
    })
  }
  
  console.log('✅ Scroll animations configured')
}

/**
 * 设置 Lenis 和 ScrollTrigger 的同步
 */
function setupLenisScrollTrigger() {
  if (!lenis) return
  
  // Lenis 滚动时更新 ScrollTrigger
  lenis.on('scroll', ScrollTrigger.update)
  
  // 将 GSAP 的 ticker 添加到 lenis 的 requestAnimationFrame 中
  lenis.on('scroll', () => {
    gsap.ticker.tick()
  })
}
</script>

<template>
  <!-- 代码雨背景 -->
  <canvas ref="codeRainCanvasRef" class="fixed inset-0 w-full h-full z-0"></canvas>
  
  <!-- 主容器 -->
  <div class="home-jiejoe min-h-screen bg-gradient-to-b from-white to-blue-50/30 overflow-hidden">
    
    <!-- Hero 区域 - 全屏高度 -->
    <section class="relative min-h-screen flex items-center justify-center z-10">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <!-- 主标题 -->
        <h1 ref="heroTitleRef"
            class="text-6xl md:text-8xl lg:text-9xl font-black text-slate-900 mb-8 leading-none tracking-tighter"
            style="transform-style: preserve-3d;">
          沈阳工业大学计算机程序设计社团
        </h1>
        
        <!-- 副标题 -->
        <p ref="heroSubtitleRef"
           class="text-xl md:text-2xl lg:text-3xl text-indigo-600 mb-16 max-w-4xl mx-auto font-light tracking-wide">
          培养创新思维 · 提升编程能力 · 打造技术社区
        </p>
        
        <!-- CTA 按钮组 -->
        <div ref="heroButtonsRef" class="flex flex-col sm:flex-row gap-6 justify-center items-center mb-16">
          <button class="cta-button-jiejoe cta-primary-jiejoe">
            <span>立即加入</span>
            <svg class="w-6 h-6 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6" />
            </svg>
          </button>
          
          <button class="cta-button-jiejoe cta-secondary-jiejoe">
            <span>了解更多</span>
          </button>
        </div>
      </div>
      
      <!-- 滚动提示 -->
      <div ref="scrollHintRef" class="absolute bottom-12 left-1/2 -translate-x-1/2 text-center">
        <div class="text-indigo-500 text-sm font-mono mb-2 tracking-widest">
          SCROLL CAREFULLY, IT'S SMOOTH
        </div>
        <div class="text-indigo-400 text-xs tracking-wider">
          小 · 心 · 地 · 滑
        </div>
        <div class="w-6 h-10 border-2 border-indigo-400 rounded-full mx-auto mt-4 flex items-start justify-center p-1">
          <div class="w-1.5 h-3 bg-indigo-500 rounded-full animate-bounce"></div>
        </div>
      </div>
    </section>

    <!-- 核心功能展示区 -->
    <section ref="featuresSectionRef" class="relative z-10 py-32 bg-gradient-to-b from-white/80 to-blue-50/50">
      <div class="max-w-7xl mx-auto px-4">
        <div class="text-center mb-24">
          <h2 class="text-5xl md:text-7xl font-black text-slate-900 mb-8 tracking-tighter">
            我们的特色
          </h2>
          <p class="text-xl text-indigo-600 max-w-3xl mx-auto font-light">
            提供全方位的技术学习和实践平台
          </p>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <!-- 功能卡片 1 -->
          <div class="feature-card-jiejoe group">
            <div class="h-full border border-indigo-200 p-10 rounded-2xl hover:border-indigo-400 hover:shadow-xl transition-all duration-500 bg-white/70 backdrop-blur-sm flex flex-col items-center text-center">
              <div class="w-20 h-20 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center mb-6 group-hover:scale-110 group-hover:rotate-6 transition-all duration-500 shadow-lg">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4" />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-slate-900 mb-3">技术培训</h3>
              <p class="text-slate-600 text-base leading-relaxed">系统的编程课程和技术分享，从入门到进阶</p>
            </div>
          </div>
          
          <!-- 功能卡片 2 -->
          <div class="feature-card-jiejoe group">
            <div class="h-full border border-indigo-200 p-10 rounded-2xl hover:border-indigo-400 hover:shadow-xl transition-all duration-500 bg-white/70 backdrop-blur-sm flex flex-col items-center text-center">
              <div class="w-20 h-20 bg-gradient-to-br from-purple-500 to-pink-600 rounded-xl flex items-center justify-center mb-6 group-hover:scale-110 group-hover:rotate-6 transition-all duration-500 shadow-lg">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-slate-900 mb-3">项目实践</h3>
              <p class="text-slate-600 text-base leading-relaxed">参与真实项目开发，积累实战经验</p>
            </div>
          </div>
          
          <!-- 功能卡片 3 -->
          <div class="feature-card-jiejoe group">
            <div class="h-full border border-indigo-200 p-10 rounded-2xl hover:border-indigo-400 hover:shadow-xl transition-all duration-500 bg-white/70 backdrop-blur-sm flex flex-col items-center text-center">
              <div class="w-20 h-20 bg-gradient-to-br from-blue-500 to-cyan-600 rounded-xl flex items-center justify-center mb-6 group-hover:scale-110 group-hover:rotate-6 transition-all duration-500 shadow-lg">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-slate-900 mb-3">竞赛交流</h3>
              <p class="text-slate-600 text-base leading-relaxed">组织编程竞赛和技术交流活动</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 最新动态区 -->
    <section ref="contentSectionRef" class="relative z-10 py-32 bg-gradient-to-b from-blue-50/50 to-white/80">
      <div class="max-w-7xl mx-auto px-4">
        <div class="text-center mb-24">
          <h2 class="text-5xl md:text-7xl font-black text-slate-900 mb-8 tracking-tighter">
            最新动态
          </h2>
          <p class="text-xl text-indigo-600">了解社团最新活动和资讯</p>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
          <!-- 动态卡片 1 -->
          <div class="content-card-jiejoe group">
            <div class="h-full border border-indigo-200 rounded-2xl overflow-hidden hover:border-indigo-400 hover:shadow-2xl transition-all duration-500 bg-white/70 backdrop-blur-sm flex flex-col">
              <div class="h-56 bg-gradient-to-br from-indigo-100 to-purple-100 relative overflow-hidden">
                <div class="absolute inset-0 bg-gradient-to-br from-indigo-500/10 to-purple-500/10 group-hover:from-indigo-500/20 group-hover:to-purple-500/20 transition-all duration-500"></div>
              </div>
              <div class="p-8 flex-1 flex flex-col">
                <h3 class="text-2xl font-bold text-slate-900 mb-3">春季编程训练营报名开始</h3>
                <p class="text-slate-600 mb-6 text-base leading-relaxed flex-1">为期两周的密集培训，涵盖前端、后端、算法等多个方向，帮助你快速提升编程能力。</p>
                <div class="flex items-center text-sm text-indigo-500 font-mono">
                  <span>2024-03-15</span>
                  <span class="mx-3">•</span>
                  <span class="group-hover:text-indigo-600 transition-colors font-semibold">阅读更多 →</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 动态卡片 2 -->
          <div class="content-card-jiejoe group">
            <div class="h-full border border-indigo-200 rounded-2xl overflow-hidden hover:border-indigo-400 hover:shadow-2xl transition-all duration-500 bg-white/70 backdrop-blur-sm flex flex-col">
              <div class="h-56 bg-gradient-to-br from-purple-100 to-pink-100 relative overflow-hidden">
                <div class="absolute inset-0 bg-gradient-to-br from-purple-500/10 to-pink-500/10 group-hover:from-purple-500/20 group-hover:to-pink-500/20 transition-all duration-500"></div>
              </div>
              <div class="p-8 flex-1 flex flex-col">
                <h3 class="text-2xl font-bold text-slate-900 mb-3">ACM校赛获奖喜报</h3>
                <p class="text-slate-600 mb-6 text-base leading-relaxed flex-1">恭喜我社成员在 ACM 校赛中斩获一等奖 2 项、二等奖 5 项的优异成绩！</p>
                <div class="flex items-center text-sm text-indigo-500 font-mono">
                  <span>2024-03-10</span>
                  <span class="mx-3">•</span>
                  <span class="group-hover:text-indigo-600 transition-colors font-semibold">阅读更多 →</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="relative z-10 py-16 bg-gradient-to-b from-white/80 to-blue-50/30 border-t border-indigo-200">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <p class="text-slate-600 text-lg">
          © 2024 沈阳工业大学计算机程序设计社团
        </p>
        <p class="text-slate-500 text-sm mt-2">
          用代码改变世界
        </p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
/* 平滑滚动基础设置 */
html {
  scroll-behavior: auto;
}

/* 代码雨 Canvas 样式 */
canvas {
  pointer-events: auto;
}

/* CTA 按钮样式 */
.cta-button-jiejoe {
  @apply px-12 py-6 rounded-xl font-bold text-xl transition-all duration-500 flex items-center;
}

.cta-primary-jiejoe {
  @apply bg-gradient-to-r from-indigo-600 to-purple-600 text-white;
  box-shadow: 0 8px 30px rgba(99, 102, 241, 0.4);
}

.cta-primary-jiejoe:hover {
  @apply from-indigo-500 to-purple-500 scale-105;
  box-shadow: 0 12px 40px rgba(99, 102, 241, 0.5);
  transform: translateY(-3px);
}

.cta-secondary-jiejoe {
  @apply border-2 border-indigo-300 text-indigo-600;
  background: rgba(99, 102, 241, 0.05);
}

.cta-secondary-jiejoe:hover {
  @apply border-indigo-500 bg-indigo-50 scale-105;
  transform: translateY(-3px);
}

/* Feature Cards 3D 变换基础 */
.feature-card-jiejoe {
  transform-style: preserve-3d;
  perspective: 1000px;
}

/* Content Cards 3D 变换基础 */
.content-card-jiejoe {
  transform-style: preserve-3d;
  perspective: 1000px;
}
</style>
