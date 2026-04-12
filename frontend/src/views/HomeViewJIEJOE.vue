<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import Lenis from 'lenis'
import * as THREE from 'three'

// 注册 ScrollTrigger 插件
gsap.registerPlugin(ScrollTrigger)

const heroTitleRef = ref<HTMLHeadingElement | null>(null)
const heroSubtitleRef = ref<HTMLParagraphElement | null>(null)
const heroButtonsRef = ref<HTMLDivElement | null>(null)
const scrollHintRef = ref<HTMLDivElement | null>(null)
const threeCanvasRef = ref<HTMLCanvasElement | null>(null)
const featuresSectionRef = ref<HTMLElement | null>(null)
const contentSectionRef = ref<HTMLElement | null>(null)

let lenis: Lenis | null = null
let scene: THREE.Scene | null = null
let camera: THREE.PerspectiveCamera | null = null
let renderer: THREE.WebGLRenderer | null = null
let wireframeMesh: THREE.Mesh | null = null
let animationId: number | null = null

onMounted(() => {
  console.log('🚀 HomeViewJIEJOE mounted')
  
  // 初始化 Lenis 平滑滚动
  initLenis()
  
  // 初始化 Three.js 3D 场景
  initThreeJS()
  
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
  
  // 清理 Three.js
  if (animationId !== null) {
    cancelAnimationFrame(animationId)
  }
  if (renderer) {
    renderer.dispose()
  }
  
  console.log('🚀 HomeViewJIEJOE unmounted')
})

/**
 * 初始化 Lenis 平滑滚动
 */
function initLenis() {
  lenis = new Lenis({
    duration: 1.2,          // 滚动持续时间
    easing: (t: number) => Math.min(1, 1.001 - Math.pow(2, -10 * t)), // 指数缓动
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
 * 初始化 Three.js 3D 场景
 */
function initThreeJS() {
  if (!threeCanvasRef.value) return
  
  // 创建场景
  scene = new THREE.Scene()
  
  // 创建相机
  camera = new THREE.PerspectiveCamera(
    75,
    window.innerWidth / window.innerHeight,
    0.1,
    1000
  )
  camera.position.z = 5
  
  // 创建渲染器
  renderer = new THREE.WebGLRenderer({
    canvas: threeCanvasRef.value,
    alpha: true,
    antialias: true
  })
  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  
  // 创建线框几何体（二十面体 - 类似 JIEJOE 的三角形）
  const geometry = new THREE.IcosahedronGeometry(2, 1)
  
  // 创建线框材质（蓝色系，浅色调）
  const material = new THREE.MeshBasicMaterial({
    color: 0x6366f1,  // 靛蓝色
    wireframe: true,
    transparent: true,
    opacity: 0.6
  })
  
  wireframeMesh = new THREE.Mesh(geometry, material)
  wireframeMesh.position.set(3, 0, 0)  // 放在右侧
  scene.add(wireframeMesh)
  
  // 添加第二个几何体（左侧小一点的）
  const geometry2 = new THREE.OctahedronGeometry(1, 0)
  const material2 = new THREE.MeshBasicMaterial({
    color: 0x8b5cf6,  // 紫色
    wireframe: true,
    transparent: true,
    opacity: 0.3
  })
  const mesh2 = new THREE.Mesh(geometry2, material2)
  mesh2.position.set(-4, 1, -2)
  scene.add(mesh2)
  
  // 添加一些装饰性线条
  const lineGeometry = new THREE.BufferGeometry()
  const linePositions = new Float32Array([
    -10, -5, -5,  10, 5, -5,
    -10, 5, -5,  10, -5, -5,
    -8, -8, -8,  8, 8, -8,
  ])
  lineGeometry.setAttribute('position', new THREE.BufferAttribute(linePositions, 3))
  const lineMaterial = new THREE.LineBasicMaterial({ 
    color: 0x6366f1, 
    transparent: true, 
    opacity: 0.15 
  })
  const lines = new THREE.LineSegments(lineGeometry, lineMaterial)
  scene.add(lines)
  
  // 动画循环
  const animate = () => {
    animationId = requestAnimationFrame(animate)
    
    if (wireframeMesh) {
      wireframeMesh.rotation.x += 0.003
      wireframeMesh.rotation.y += 0.005
    }
    
    if (mesh2) {
      mesh2.rotation.x -= 0.005
      mesh2.rotation.y -= 0.003
    }
    
    renderer!.render(scene!, camera!)
  }
  
  animate()
  
  // 窗口大小调整
  window.addEventListener('resize', onWindowResize)
  
  console.log('✅ Three.js 3D scene initialized')
}

/**
 * 窗口大小调整
 */
function onWindowResize() {
  if (!camera || !renderer) return
  
  camera.aspect = window.innerWidth / window.innerHeight
  camera.updateProjectionMatrix()
  renderer.setSize(window.innerWidth, window.innerHeight)
}

/**
 * Hero 区域入场动画序列（对标 JIEJOE）
 */
function animateHeroEntrance() {
  const tl = gsap.timeline({ delay: 0.5 })
  
  // 1. 标题逐词出现（而非逐字）
  if (heroTitleRef.value) {
    const titleText = heroTitleRef.value.textContent || ''
    heroTitleRef.value.innerHTML = ''
    
    // 拆分为单词
    const words = titleText.split(' ').map((word, index) => {
      const span = document.createElement('span')
      span.textContent = word + (index < titleText.split(' ').length - 1 ? '\u00A0' : '')
      span.className = 'title-word-jiejoe'
      span.style.display = 'inline-block'
      span.style.opacity = '0'
      span.style.transform = 'translateY(100px) rotateX(-90deg)'
      heroTitleRef.value!.appendChild(span)
      return span
    })
    
    // 单词从下方翻转进入
    tl.to(words, {
      opacity: 1,
      y: 0,
      rotateX: 0,
      duration: 1.2,
      stagger: 0.1,
      ease: 'power4.out'
    }, 0)
  }
  
  // 2. 副标题从左侧滑入
  if (heroSubtitleRef.value) {
    tl.fromTo(heroSubtitleRef.value,
      { opacity: 0, x: -100, filter: 'blur(20px)' },
      { opacity: 1, x: 0, filter: 'blur(0px)', duration: 1, ease: 'power3.out' },
      '-=0.8'
    )
  }
  
  // 3. 按钮组缩放出现
  if (heroButtonsRef.value) {
    tl.fromTo(heroButtonsRef.value,
      { opacity: 0, scale: 0, rotation: -180 },
      { opacity: 1, scale: 1, rotation: 0, duration: 0.8, ease: 'back.out(2)' },
      '-=0.6'
    )
  }
  
  // 4. 滚动提示闪烁出现
  if (scrollHintRef.value) {
    tl.fromTo(scrollHintRef.value,
      { opacity: 0, y: 50 },
      { opacity: 1, y: 0, duration: 0.6, ease: 'power2.out' },
      '-=0.4'
    )
    
    // 添加持续的上下浮动动画
    gsap.to(scrollHintRef.value, {
      y: 10,
      duration: 1.5,
      ease: 'power1.inOut',
      yoyo: true,
      repeat: -1,
      delay: 3
    })
  }
}

/**
 * 设置滚动触发动画（对标 JIEJOE 的复杂序列）
 */
function setupScrollAnimations() {
  // 3D 几何体随滚动旋转
  if (wireframeMesh) {
    gsap.to(wireframeMesh.rotation, {
      x: Math.PI * 2,
      y: Math.PI * 2,
      scrollTrigger: {
        trigger: 'body',
        start: 'top top',
        end: 'bottom bottom',
        scrub: 1
      }
    })
  }
  
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
}

/**
 * 同步 Lenis 和 ScrollTrigger
 */
function setupLenisScrollTrigger() {
  if (!lenis) return
  
  lenis.on('scroll', ScrollTrigger.update)
  
  gsap.ticker.add((time) => {
    lenis?.raf(time * 1000)
  })
  
  gsap.ticker.lagSmoothing(0)
  
  console.log('✅ Lenis + ScrollTrigger synced')
}
</script>

<template>
  <div class="home-jiejoe min-h-screen bg-gradient-to-b from-white to-blue-50/30 overflow-hidden">
    <!-- Three.js 3D Canvas 层 -->
    <canvas ref="threeCanvasRef" class="three-canvas"></canvas>
    
    <!-- 蓝色/紫色光斑装饰（浅色调） -->
    <div class="blue-glow blue-glow-1"></div>
    <div class="purple-glow purple-glow-2"></div>
    
    <!-- 装饰性线条 -->
    <div class="decorative-lines">
      <div class="line line-1"></div>
      <div class="line line-2"></div>
      <div class="line line-3"></div>
    </div>
    
    <!-- Hero 区域 -->
    <section class="relative z-10 min-h-screen flex items-center justify-center px-4">
      <div class="max-w-7xl mx-auto text-center">
        <!-- 主标题 - 逐词动画 -->
        <h1 
          ref="heroTitleRef"
          class="text-6xl md:text-8xl lg:text-9xl font-black text-slate-900 mb-8 leading-none tracking-tighter"
          style="transform-style: preserve-3d;"
        >
          沈阳工业大学计算机程序设计社团
        </h1>
        
        <!-- 副标题 -->
        <p 
          ref="heroSubtitleRef"
          class="text-xl md:text-2xl lg:text-3xl text-indigo-600 mb-16 max-w-4xl mx-auto font-light tracking-wide"
        >
          培养创新思维 · 提升编程能力 · 打造技术社区
        </p>
        
        <!-- CTA 按钮组 -->
        <div 
          ref="heroButtonsRef"
          class="flex flex-col sm:flex-row gap-6 justify-center items-center"
        >
          <button class="cta-button-jiejoe cta-primary-jiejoe group">
            <span>立即加入</span>
            <svg class="w-6 h-6 ml-3 transform group-hover:translate-x-2 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M13 7l5 5m0 0l-5 5m5-5H6" />
            </svg>
          </button>
          
          <button class="cta-button-jiejoe cta-secondary-jiejoe">
            <span>了解更多</span>
          </button>
        </div>
      </div>
      
      <!-- 滚动提示 -->
      <div 
        ref="scrollHintRef"
        class="absolute bottom-12 left-1/2 transform -translate-x-1/2 text-center"
      >
        <div class="scroll-icon mb-4">
          <svg class="w-8 h-8 text-indigo-500 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3" />
          </svg>
        </div>
        <p class="text-indigo-600 text-sm font-mono tracking-widest">
          SCROLL CAREFULLY, IT'S SMOOTH
        </p>
        <p class="text-indigo-400 text-xs font-mono tracking-wider mt-1">
          小 · 心 · 地 · 滑
        </p>
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
          
          <div class="feature-card-jiejoe group">
            <div class="h-full border border-indigo-200 p-10 rounded-2xl hover:border-indigo-400 hover:shadow-xl transition-all duration-500 bg-white/70 backdrop-blur-sm flex flex-col items-center text-center">
              <div class="w-20 h-20 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center mb-6 group-hover:scale-110 group-hover:rotate-6 transition-all duration-500 shadow-lg">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-slate-900 mb-3">项目实战</h3>
              <p class="text-slate-600 text-base leading-relaxed">参与真实项目开发，积累实战经验</p>
            </div>
          </div>
          
          <div class="feature-card-jiejoe group">
            <div class="h-full border border-indigo-200 p-10 rounded-2xl hover:border-indigo-400 hover:shadow-xl transition-all duration-500 bg-white/70 backdrop-blur-sm flex flex-col items-center text-center">
              <div class="w-20 h-20 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center mb-6 group-hover:scale-110 group-hover:rotate-6 transition-all duration-500 shadow-lg">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-slate-900 mb-3">竞赛指导</h3>
              <p class="text-slate-600 text-base leading-relaxed">专业导师指导，助力各类编程竞赛</p>
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
          <div class="content-card-jiejoe group">
            <div class="h-full border border-indigo-200 rounded-2xl overflow-hidden hover:border-indigo-400 hover:shadow-2xl transition-all duration-500 bg-white/70 backdrop-blur-sm flex flex-col">
              <div class="h-56 bg-gradient-to-br from-indigo-100 to-purple-100 relative overflow-hidden">
                <div class="absolute inset-0 bg-gradient-to-br from-indigo-500/10 to-purple-500/10 group-hover:from-indigo-500/20 group-hover:to-purple-500/20 transition-all duration-500"></div>
              </div>
              <div class="p-8 flex-1 flex flex-col">
                <h3 class="text-2xl font-bold text-slate-900 mb-3">春季编程训练营报名开始</h3>
                <p class="text-slate-600 mb-6 text-base leading-relaxed flex-1">为期两周的密集培训，涵盖前端、后端、算法等多个方向...</p>
                <div class="flex items-center text-sm text-indigo-500 font-mono">
                  <span>2024-03-15</span>
                  <span class="mx-3">•</span>
                  <span class="group-hover:text-indigo-600 transition-colors font-semibold">阅读更多 →</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="content-card-jiejoe group">
            <div class="h-full border border-indigo-200 rounded-2xl overflow-hidden hover:border-indigo-400 hover:shadow-2xl transition-all duration-500 bg-white/70 backdrop-blur-sm flex flex-col">
              <div class="h-56 bg-gradient-to-br from-purple-100 to-indigo-100 relative overflow-hidden">
                <div class="absolute inset-0 bg-gradient-to-br from-purple-500/10 to-indigo-500/10 group-hover:from-purple-500/20 group-hover:to-indigo-500/20 transition-all duration-500"></div>
              </div>
              <div class="p-8 flex-1 flex flex-col">
                <h3 class="text-2xl font-bold text-slate-900 mb-3">ACM 校赛获奖喜报</h3>
                <p class="text-slate-600 mb-6 text-base leading-relaxed flex-1">我社成员在 ACM 校赛中取得优异成绩，获得多个奖项...</p>
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
  </div>
</template>

<style scoped>
/* Three.js Canvas */
.three-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

/* 蓝色光斑装饰 */
.blue-glow {
  position: fixed;
  border-radius: 50%;
  filter: blur(120px);
  pointer-events: none;
  z-index: 1;
}

.blue-glow-1 {
  width: 600px;
  height: 600px;
  background: rgba(99, 102, 241, 0.12);
  top: -200px;
  left: -200px;
  animation: float1 15s ease-in-out infinite;
}

.purple-glow {
  position: fixed;
  border-radius: 50%;
  filter: blur(120px);
  pointer-events: none;
  z-index: 1;
}

.purple-glow-2 {
  width: 500px;
  height: 500px;
  background: rgba(139, 92, 246, 0.1);
  bottom: -150px;
  right: -150px;
  animation: float2 18s ease-in-out infinite;
}

@keyframes float1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(100px, 100px) scale(1.2); }
}

@keyframes float2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-80px, -80px) scale(1.1); }
}

/* 装饰性线条 */
.decorative-lines {
  position: fixed;
  inset: 0;
  z-index: 2;
  pointer-events: none;
  overflow: hidden;
}

.line {
  position: absolute;
  background: rgba(99, 102, 241, 0.1);
}

.line-1 {
  width: 200%;
  height: 1px;
  top: 30%;
  left: -50%;
  transform: rotate(-15deg);
}

.line-2 {
  width: 200%;
  height: 1px;
  top: 60%;
  left: -50%;
  transform: rotate(10deg);
}

.line-3 {
  width: 1px;
  height: 200%;
  left: 70%;
  top: -50%;
  transform: rotate(5deg);
}

/* 标题单词动画 */
.title-word-jiejoe {
  display: inline-block;
  backface-visibility: hidden;
  will-change: transform, opacity;
}

/* CTA 按钮样式（浅色调） */
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

/* 滚动提示图标动画 */
.scroll-icon {
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

/* Feature Cards */
.feature-card-jiejoe {
  @apply transition-all duration-500;
}

/* Content Cards */
.content-card-jiejoe {
  @apply transition-all duration-500;
}
</style>
