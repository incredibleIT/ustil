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
const particleCanvasRef = ref<HTMLCanvasElement | null>(null)
const featuresSectionRef = ref<HTMLElement | null>(null)
const contentSectionRef = ref<HTMLElement | null>(null)

let lenis: Lenis | null = null
let animationId: number | null = null
let particles: Particle[] = []
let canvasWidth = 0
let canvasHeight = 0
let mouseX = 0
let mouseY = 0

interface Particle {
  x: number
  y: number
  vx: number
  vy: number
  radius: number
  color: string
}

onMounted(() => {
  console.log('🚀 HomeViewParticles mounted')
  
  // 初始化 Lenis 平滑滚动
  initLenis()
  
  // 初始化粒子系统
  initParticles()
  
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
  
  // 清理粒子动画
  if (animationId !== null) {
    cancelAnimationFrame(animationId)
  }
  
  // 移除事件监听
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('resize', handleResize)
  
  console.log('🚀 HomeViewParticles unmounted')
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
 * 初始化粒子系统
 */
function initParticles() {
  if (!particleCanvasRef.value) return
  
  const canvas = particleCanvasRef.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  // 设置 canvas 尺寸
  canvasWidth = window.innerWidth
  canvasHeight = window.innerHeight
  canvas.width = canvasWidth
  canvas.height = canvasHeight
  
  // 创建粒子
  const particleCount = Math.min(100, Math.floor((canvasWidth * canvasHeight) / 15000))
  particles = []
  
  for (let i = 0; i < particleCount; i++) {
    particles.push({
      x: Math.random() * canvasWidth,
      y: Math.random() * canvasHeight,
      vx: (Math.random() - 0.5) * 0.5,
      vy: (Math.random() - 0.5) * 0.5,
      radius: Math.random() * 2 + 1,
      color: Math.random() > 0.5 ? 'rgba(99, 102, 241, ' : 'rgba(139, 92, 246, '
    })
  }
  
  // 添加鼠标监听
  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('resize', handleResize)
  
  // 启动动画循环
  animateParticles()
  
  console.log('✅ Particle system initialized with', particleCount, 'particles')
}

/**
 * 粒子动画循环
 */
function animateParticles() {
  if (!particleCanvasRef.value) return
  
  const canvas = particleCanvasRef.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  // 清空画布
  ctx.clearRect(0, 0, canvasWidth, canvasHeight)
  
  // 更新和绘制粒子
  const connectionDistance = 150
  const mouseInfluenceRadius = 200
  
  particles.forEach((particle, i) => {
    // 更新位置
    particle.x += particle.vx
    particle.y += particle.vy
    
    // 边界检测（反弹）
    if (particle.x < 0 || particle.x > canvasWidth) particle.vx *= -1
    if (particle.y < 0 || particle.y > canvasHeight) particle.vy *= -1
    
    // 鼠标吸引效果
    const dx = mouseX - particle.x
    const dy = mouseY - particle.y
    const distance = Math.sqrt(dx * dx + dy * dy)
    
    if (distance < mouseInfluenceRadius) {
      const force = (mouseInfluenceRadius - distance) / mouseInfluenceRadius
      particle.vx += (dx / distance) * force * 0.02
      particle.vy += (dy / distance) * force * 0.02
    }
    
    // 限制速度
    const speed = Math.sqrt(particle.vx * particle.vx + particle.vy * particle.vy)
    if (speed > 1) {
      particle.vx = (particle.vx / speed) * 1
      particle.vy = (particle.vy / speed) * 1
    }
    
    // 绘制粒子
    ctx.beginPath()
    ctx.arc(particle.x, particle.y, particle.radius, 0, Math.PI * 2)
    ctx.fillStyle = particle.color + '0.6)'
    ctx.fill()
    
    // 绘制连线
    for (let j = i + 1; j < particles.length; j++) {
      const otherParticle = particles[j]
      const dx = particle.x - otherParticle.x
      const dy = particle.y - otherParticle.y
      const dist = Math.sqrt(dx * dx + dy * dy)
      
      if (dist < connectionDistance) {
        const opacity = (1 - dist / connectionDistance) * 0.3
        ctx.beginPath()
        ctx.moveTo(particle.x, particle.y)
        ctx.lineTo(otherParticle.x, otherParticle.y)
        ctx.strokeStyle = `rgba(99, 102, 241, ${opacity})`
        ctx.lineWidth = 1
        ctx.stroke()
      }
    }
  })
  
  // 继续动画循环
  animationId = requestAnimationFrame(animateParticles)
}

/**
 * 处理鼠标移动
 */
function handleMouseMove(event: MouseEvent) {
  mouseX = event.clientX
  mouseY = event.clientY
}

/**
 * 处理窗口大小变化
 */
function handleResize() {
  if (!particleCanvasRef.value) return
  
  canvasWidth = window.innerWidth
  canvasHeight = window.innerHeight
  particleCanvasRef.value.width = canvasWidth
  particleCanvasRef.value.height = canvasHeight
}

/**
 * 执行 Hero 区域入场动画序列
 */
function animateHeroEntrance() {
  const tl = gsap.timeline({ delay: 0.3 })
  
  // 背景 canvas 淡入
  if (particleCanvasRef.value) {
    tl.fromTo(particleCanvasRef.value,
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
  <!-- 粒子连线背景 -->
  <canvas ref="particleCanvasRef" class="fixed inset-0 w-full h-full z-0"></canvas>
  
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

/* 粒子 Canvas 样式 */
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
