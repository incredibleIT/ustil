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
const neuralCanvasRef = ref<HTMLCanvasElement | null>(null)
const featuresSectionRef = ref<HTMLElement | null>(null)
const contentSectionRef = ref<HTMLElement | null>(null)

let lenis: Lenis | null = null
let animationId: number | null = null
let neurons: Neuron[] = []
let synapses: Synapse[] = []
let pulses: Pulse[] = []
let canvasWidth = 0
let canvasHeight = 0
let mouseX = 0
let mouseY = 0
let propagationDirection: 'forward' | 'backward' = 'forward' // 传播方向
let lastPropagationTime = 0

// 神经网络层结构
interface Neuron {
  x: number
  y: number
  layer: number
  radius: number
  activation: number // 激活程度 0-1
  baseActivation: number
  pulsePhase: number
  color: string
}

interface Synapse {
  from: Neuron
  to: Neuron
  weight: number
  active: boolean
  pulseProgress: number
}

interface Pulse {
  synapse: Synapse
  progress: number
  speed: number
}

onMounted(() => {
  console.log('🚀 HomeViewNeuralNetwork mounted')
  
  // 初始化 Lenis 平滑滚动
  initLenis()
  
  // 初始化神经网络
  initNeuralNetwork()
  
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
  
  // 清理神经网络动画
  if (animationId !== null) {
    cancelAnimationFrame(animationId)
  }
  
  // 移除事件监听
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('resize', handleResize)
  
  console.log('🚀 HomeViewNeuralNetwork unmounted')
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
 * 初始化神经网络
 */
function initNeuralNetwork() {
  if (!neuralCanvasRef.value) return
  
  const canvas = neuralCanvasRef.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  // 设置 canvas 尺寸
  canvasWidth = window.innerWidth
  canvasHeight = window.innerHeight
  canvas.width = canvasWidth
  canvas.height = canvasHeight
  
  // 清空数据
  neurons = []
  synapses = []
  pulses = []
  
  // 创建神经网络层结构：输入层(6) -> 隐藏层1(8) -> 隐藏层2(6) -> 输出层(4)
  const layers = [6, 8, 6, 4]
  const layerSpacing = canvasWidth / (layers.length + 1)
  
  layers.forEach((neuronCount, layerIndex) => {
    const layerX = layerSpacing * (layerIndex + 1)
    const neuronSpacing = canvasHeight / (neuronCount + 1)
    
    for (let i = 0; i < neuronCount; i++) {
      const neuron: Neuron = {
        x: layerX + (Math.random() - 0.5) * 40, // 轻微随机偏移
        y: neuronSpacing * (i + 1) + (Math.random() - 0.5) * 30,
        layer: layerIndex,
        radius: layerIndex === 0 || layerIndex === layers.length - 1 ? 6 : 4,
        activation: 0,
        baseActivation: Math.random() * 0.3 + 0.1,
        pulsePhase: Math.random() * Math.PI * 2,
        color: getLayerColor(layerIndex, layers.length)
      }
      neurons.push(neuron)
    }
  })
  
  // 创建突触连接（稀疏连接，仅连接部分节点）
  for (let i = 0; i < neurons.length; i++) {
    for (let j = i + 1; j < neurons.length; j++) {
      const n1 = neurons[i]
      const n2 = neurons[j]
      
      // 仅连接相邻层，且随机选择部分连接（稀疏化）
      if (Math.abs(n1.layer - n2.layer) === 1) {
        // 30% 概率连接，使网络更离散
        if (Math.random() < 0.3) {
          const weight = Math.random() * 0.5 + 0.1 // 降低权重范围
          synapses.push({
            from: n1,
            to: n2,
            weight: weight,
            active: false,
            pulseProgress: 0
          })
        }
      }
    }
  }
  
  // 添加鼠标监听
  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('resize', handleResize)
  
  // 启动脉冲生成定时器（降低频率）
  setInterval(generatePropagation, 1500) // 从 800ms 增加到 1500ms，每次生成一组传播
  
  // 启动动画循环
  animateNeuralNetwork()
  
  console.log('✅ Neural network initialized:', neurons.length, 'neurons,', synapses.length, 'synapses')
}

/**
 * 获取层级颜色
 */
function getLayerColor(layerIndex: number, totalLayers: number): string {
  const colors = [
    'rgba(99, 102, 241, ',   // 靛蓝色 - 输入层
    'rgba(139, 92, 246, ',   // 紫色 - 隐藏层1
    'rgba(168, 85, 247, ',   // 深紫色 - 隐藏层2
    'rgba(192, 38, 211, '    // 紫红色 - 输出层
  ]
  return colors[layerIndex] || colors[colors.length - 1]
}

/**
 * 生成完整的前向/反向传播
 */
function generatePropagation() {
  const now = Date.now()
  if (now - lastPropagationTime < 1000) return // 防止过快生成
  lastPropagationTime = now
  
  if (synapses.length === 0) return
  
  // 切换传播方向
  propagationDirection = propagationDirection === 'forward' ? 'backward' : 'forward'
  
  const isForward = propagationDirection === 'forward'
  
  // 找到起始层的神经元
  const startLayer = isForward ? 0 : neurons.reduce((max, n) => Math.max(max, n.layer), 0)
  const startNeurons = neurons.filter(n => n.layer === startLayer)
  
  if (startNeurons.length === 0) return
  
  // 从起始层随机选择 2-4 个神经元开始传播
  const numStartNeurons = Math.min(startNeurons.length, Math.floor(Math.random() * 3) + 2)
  const shuffled = startNeurons.sort(() => Math.random() - 0.5)
  const selectedNeurons = shuffled.slice(0, numStartNeurons)
  
  // 为每个起始神经元生成脉冲
  selectedNeurons.forEach(startNeuron => {
    // 找到该神经元的所有出边
    const outgoingSynapses = synapses.filter(s => 
      isForward ? s.from === startNeuron : s.to === startNeuron
    )
    
    // 随机选择 1-2 条突触生成脉冲
    const numPulses = Math.min(outgoingSynapses.length, Math.floor(Math.random() * 2) + 1)
    const shuffledSynapses = outgoingSynapses.sort(() => Math.random() - 0.5)
    
    shuffledSynapses.slice(0, numPulses).forEach(synapse => {
      pulses.push({
        synapse: synapse,
        progress: 0,
        speed: 0.015 + Math.random() * 0.01
      })
      synapse.active = true
    })
    
    // 标记起始神经元激活
    startNeuron.activation = Math.min(1, startNeuron.activation + 0.6)
  })
  
  console.log(`🔄 ${isForward ? '前向传播' : '反向传播'}: 从第 ${startLayer} 层发起 ${selectedNeurons.length} 个节点`)
}

/**
 * 神经网络动画循环
 */
function animateNeuralNetwork() {
  if (!neuralCanvasRef.value) return
  
  const canvas = neuralCanvasRef.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  // 清空画布（带淡出效果）
  ctx.fillStyle = 'rgba(255, 255, 255, 0.1)'
  ctx.fillRect(0, 0, canvasWidth, canvasHeight)
  
  const time = Date.now() * 0.001
  
  // 绘制突触连线（轻量化）
  synapses.forEach(synapse => {
    const dx = synapse.to.x - synapse.from.x
    const dy = synapse.to.y - synapse.from.y
    const distance = Math.sqrt(dx * dx + dy * dy)
    
    // 大幅降低透明度，使连线更轻
    const baseOpacity = synapse.weight * 0.06 // 从 0.15 降低到 0.06
    const activation = (synapse.from.activation + synapse.to.activation) / 2
    const opacity = baseOpacity + activation * 0.1 // 从 0.3 降低到 0.1
    
    ctx.beginPath()
    ctx.moveTo(synapse.from.x, synapse.from.y)
    ctx.lineTo(synapse.to.x, synapse.to.y)
    ctx.strokeStyle = `rgba(99, 102, 241, ${opacity})`
    ctx.lineWidth = synapse.weight * 1 // 从 2 降低到 1
    ctx.stroke()
  })
  
  // 更新和绘制脉冲（链式传播）
  pulses = pulses.filter(pulse => {
    pulse.progress += pulse.speed
    
    if (pulse.progress >= 1) {
      // 脉冲到达目标神经元
      const targetNeuron = propagationDirection === 'forward' ? pulse.synapse.to : pulse.synapse.from
      const sourceNeuron = propagationDirection === 'forward' ? pulse.synapse.from : pulse.synapse.to
      
      // 触发目标神经元激活
      targetNeuron.activation = Math.min(1, targetNeuron.activation + 0.4)
      
      // 链式传播：从目标神经元继续传播到下一层（100% 传播到端点，单条路径）
      const nextLayerSynapses = synapses.filter(s => 
        propagationDirection === 'forward' 
          ? s.from === targetNeuron && s.to.layer > targetNeuron.layer
          : s.to === targetNeuron && s.from.layer < targetNeuron.layer
      )
      
      // 如果有下一层的连接，选择一条继续传播（单路径）
      if (nextLayerSynapses.length > 0) {
        const nextSynapse = nextLayerSynapses[Math.floor(Math.random() * nextLayerSynapses.length)]
        pulses.push({
          synapse: nextSynapse,
          progress: 0,
          speed: pulse.speed * (0.95 + Math.random() * 0.1) // 速度略微变化
        })
        nextSynapse.active = true
      }
      
      pulse.synapse.active = false
      return false
    }
    
    // 绘制脉冲光点（根据传播方向确定起点和终点）
    const startX = propagationDirection === 'forward' ? pulse.synapse.from.x : pulse.synapse.to.x
    const startY = propagationDirection === 'forward' ? pulse.synapse.from.y : pulse.synapse.to.y
    const endX = propagationDirection === 'forward' ? pulse.synapse.to.x : pulse.synapse.from.x
    const endY = propagationDirection === 'forward' ? pulse.synapse.to.y : pulse.synapse.from.y
    
    const currentX = startX + (endX - startX) * pulse.progress
    const currentY = startY + (endY - startY) * pulse.progress
    
    // 根据传播方向设置颜色
    const pulseColor = propagationDirection === 'forward' 
      ? 'rgba(99, 102, 241, '   // 前向传播：靛蓝色
      : 'rgba(236, 72, 153, '   // 反向传播：粉红色
    
    // 脉冲光晕
    const gradient = ctx.createRadialGradient(currentX, currentY, 0, currentX, currentY, 12)
    gradient.addColorStop(0, pulseColor + '0.8)')
    gradient.addColorStop(1, pulseColor + '0)')
    ctx.fillStyle = gradient
    ctx.beginPath()
    ctx.arc(currentX, currentY, 12, 0, Math.PI * 2)
    ctx.fill()
    
    // 脉冲核心
    ctx.fillStyle = 'rgba(255, 255, 255, 0.9)'
    ctx.beginPath()
    ctx.arc(currentX, currentY, 3, 0, Math.PI * 2)
    ctx.fill()
    
    return true
  })
  
  // 更新和绘制神经元
  neurons.forEach((neuron, index) => {
    // 浮动效果
    const floatX = Math.sin(time * 0.5 + neuron.pulsePhase) * 3
    const floatY = Math.cos(time * 0.3 + neuron.pulsePhase) * 3
    neuron.x += floatX * 0.01
    neuron.y += floatY * 0.01
    
    // 鼠标吸引效果
    const dx = mouseX - neuron.x
    const dy = mouseY - neuron.y
    const distance = Math.sqrt(dx * dx + dy * dy)
    const mouseInfluenceRadius = 150
    
    if (distance < mouseInfluenceRadius) {
      const force = (mouseInfluenceRadius - distance) / mouseInfluenceRadius
      neuron.x += (dx / distance) * force * 0.5
      neuron.y += (dy / distance) * force * 0.5
      neuron.activation = Math.min(1, neuron.activation + 0.1)
    }
    
    // 激活衰减
    neuron.activation = Math.max(neuron.baseActivation, neuron.activation * 0.98)
    
    // 周期性激活
    const periodicActivation = Math.sin(time * 2 + index) * 0.5 + 0.5
    neuron.activation = Math.max(neuron.activation, periodicActivation * 0.3)
    
    // 绘制神经元光晕（更轻盈）
    const activationRadius = neuron.radius + neuron.activation * 6 // 从 10 降低到 6
    const gradient = ctx.createRadialGradient(
      neuron.x, neuron.y, 0,
      neuron.x, neuron.y, activationRadius
    )
    gradient.addColorStop(0, neuron.color + (0.4 + neuron.activation * 0.3) + ')') // 降低不透明度
    gradient.addColorStop(0.5, neuron.color + (0.2 + neuron.activation * 0.2) + ')') // 降低不透明度
    gradient.addColorStop(1, neuron.color + '0)')
    
    ctx.fillStyle = gradient
    ctx.beginPath()
    ctx.arc(neuron.x, neuron.y, activationRadius, 0, Math.PI * 2)
    ctx.fill()
    
    // 绘制神经元核心
    ctx.fillStyle = neuron.color + '0.9)'
    ctx.beginPath()
    ctx.arc(neuron.x, neuron.y, neuron.radius, 0, Math.PI * 2)
    ctx.fill()
    
    // 高激活状态的特殊效果（降低强度）
    if (neuron.activation > 0.6) {
      ctx.strokeStyle = neuron.color + (neuron.activation * 0.3) + ')' // 从 0.5 降低到 0.3
      ctx.lineWidth = 1.5 // 从 2 降低到 1.5
      ctx.beginPath()
      ctx.arc(neuron.x, neuron.y, neuron.radius + 3, 0, Math.PI * 2) // 从 4 降低到 3
      ctx.stroke()
    }
  })
  
  // 继续动画循环
  animationId = requestAnimationFrame(animateNeuralNetwork)
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
  if (!neuralCanvasRef.value) return
  
  canvasWidth = window.innerWidth
  canvasHeight = window.innerHeight
  neuralCanvasRef.value.width = canvasWidth
  neuralCanvasRef.value.height = canvasHeight
  
  // 重新初始化神经网络
  initNeuralNetwork()
}

/**
 * 执行 Hero 区域入场动画序列
 */
function animateHeroEntrance() {
  const tl = gsap.timeline({ delay: 0.3 })
  
  // 背景 canvas 淡入
  if (neuralCanvasRef.value) {
    tl.fromTo(neuralCanvasRef.value,
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
  <!-- 神经网络背景 -->
  <canvas ref="neuralCanvasRef" class="fixed inset-0 w-full h-full z-0"></canvas>
  
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

/* 神经网络 Canvas 样式 */
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
