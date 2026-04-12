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
const cursorGlowCanvasRef = ref<HTMLCanvasElement | null>(null)
const scrollProgressBarRef = ref<HTMLDivElement | null>(null)

let lenis: Lenis | null = null
let animationId: number | null = null
let neurons: Neuron[] = []
let synapses: Synapse[] = []
let pulses: Pulse[] = []
let dataFlowParticles: DataFlowParticle[] = []
let canvasWidth = 0
let canvasHeight = 0
let mouseX = 0
let mouseY = 0
let propagationDirection: 'forward' | 'backward' = 'forward'
let lastPropagationTime = 0
let hoveredSynapse: Synapse | null = null
let hoveredNeuron: Neuron | null = null
let scrollProgress = ref(0)

// 神经网络层结构
interface Neuron {
  x: number
  y: number
  layer: number
  radius: number
  activation: number
  baseActivation: number
  pulsePhase: number
  color: string
  activationHistory: number[]
}

interface Synapse {
  from: Neuron
  to: Neuron
  weight: number
  active: boolean
  pulseProgress: number
  midpointX: number
  midpointY: number
}

interface Pulse {
  synapse: Synapse
  progress: number
  speed: number
}

interface DataFlowParticle {
  x: number
  y: number
  layer: number
  speed: number
  size: number
  opacity: number
  direction: 'forward' | 'backward'
}

// 按钮磁吸相关
const primaryButtonRef = ref<HTMLButtonElement | null>(null)
const secondaryButtonRef = ref<HTMLButtonElement | null>(null)

// 卡片 3D 悬浮相关
const featureCardsRef = ref<HTMLElement[]>([])
const contentCardsRef = ref<HTMLElement[]>([])

onMounted(() => {
  console.log('🚀 HomeViewNeuralNetworkUltimate mounted')
  
  initLenis()
  initNeuralNetwork()
  initCursorGlow()
  animateHeroEntrance()
  setupScrollAnimations()
  setupLenisScrollTrigger()
  setupMagneticButtons()
  setup3DCards()
  setupScrollProgress()
})

onUnmounted(() => {
  if (lenis) {
    lenis.destroy()
  }
  
  ScrollTrigger.getAll().forEach(trigger => trigger.kill())
  
  if (animationId !== null) {
    cancelAnimationFrame(animationId)
  }
  
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('resize', handleResize)
  
  console.log('🚀 HomeViewNeuralNetworkUltimate unmounted')
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
 * 初始化光标跟随光效 Canvas
 */
function initCursorGlow() {
  if (!cursorGlowCanvasRef.value) return
  
  const canvas = cursorGlowCanvasRef.value
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
  
  window.addEventListener('resize', () => {
    if (cursorGlowCanvasRef.value) {
      cursorGlowCanvasRef.value.width = window.innerWidth
      cursorGlowCanvasRef.value.height = window.innerHeight
    }
  })
}

/**
 * 初始化神经网络
 */
function initNeuralNetwork() {
  if (!neuralCanvasRef.value) return
  
  const canvas = neuralCanvasRef.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  canvasWidth = window.innerWidth
  canvasHeight = window.innerHeight
  canvas.width = canvasWidth
  canvas.height = canvasHeight
  
  neurons = []
  synapses = []
  pulses = []
  dataFlowParticles = []
  
  // 创建神经网络层结构：输入层(6) -> 隐藏层1(8) -> 隐藏层2(6) -> 输出层(4)
  const layers = [6, 8, 6, 4]
  const layerSpacing = canvasWidth / (layers.length + 1)
  
  layers.forEach((neuronCount, layerIndex) => {
    const layerX = layerSpacing * (layerIndex + 1)
    const neuronSpacing = canvasHeight / (neuronCount + 1)
    
    for (let i = 0; i < neuronCount; i++) {
      const neuron: Neuron = {
        x: layerX + (Math.random() - 0.5) * 40,
        y: neuronSpacing * (i + 1) + (Math.random() - 0.5) * 30,
        layer: layerIndex,
        radius: layerIndex === 0 || layerIndex === layers.length - 1 ? 6 : 4,
        activation: 0,
        baseActivation: Math.random() * 0.3 + 0.1,
        pulsePhase: Math.random() * Math.PI * 2,
        color: getLayerColor(layerIndex, layers.length),
        activationHistory: []
      }
      neurons.push(neuron)
    }
  })
  
  // 创建突触连接（稀疏连接）
  for (let i = 0; i < neurons.length; i++) {
    for (let j = i + 1; j < neurons.length; j++) {
      const n1 = neurons[i]
      const n2 = neurons[j]
      
      if (Math.abs(n1.layer - n2.layer) === 1) {
        if (Math.random() < 0.3) {
          const weight = Math.random() * 0.8 + 0.2
          synapses.push({
            from: n1,
            to: n2,
            weight: weight,
            active: false,
            pulseProgress: 0,
            midpointX: (n1.x + n2.x) / 2,
            midpointY: (n1.y + n2.y) / 2
          })
        }
      }
    }
  }
  
  // 初始化数据流粒子
  initDataFlowParticles()
  
  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('resize', handleResize)
  
  setInterval(generatePropagation, 1500)
  
  animateNeuralNetwork()
  animateCursorGlow()
  
  console.log('✅ Neural network initialized:', neurons.length, 'neurons,', synapses.length, 'synapses')
}

/**
 * 初始化层间数据流粒子
 */
function initDataFlowParticles() {
  dataFlowParticles = []
  
  for (let layer = 0; layer < 3; layer++) {
    const layerNeurons = neurons.filter(n => n.layer === layer)
    const nextLayerNeurons = neurons.filter(n => n.layer === layer + 1)
    
    const particleCount = 10 + Math.floor(Math.random() * 6)
    
    for (let i = 0; i < particleCount; i++) {
      dataFlowParticles.push({
        x: layerNeurons[Math.floor(Math.random() * layerNeurons.length)].x,
        y: layerNeurons[Math.floor(Math.random() * layerNeurons.length)].y + (Math.random() - 0.5) * 50,
        layer: layer,
        speed: 0.5 + Math.random() * 0.5,
        size: 1 + Math.random() * 2,
        opacity: 0.1 + Math.random() * 0.15,
        direction: Math.random() > 0.5 ? 'forward' : 'backward'
      })
    }
  }
}

/**
 * 获取层级颜色
 */
function getLayerColor(layerIndex: number, totalLayers: number): string {
  const colors = [
    'rgba(99, 102, 241, ',
    'rgba(139, 92, 246, ',
    'rgba(168, 85, 247, ',
    'rgba(192, 38, 211, '
  ]
  return colors[layerIndex] || colors[colors.length - 1]
}

/**
 * 生成完整的前向/反向传播
 */
function generatePropagation() {
  const now = Date.now()
  if (now - lastPropagationTime < 1000) return
  lastPropagationTime = now
  
  if (synapses.length === 0) return
  
  propagationDirection = propagationDirection === 'forward' ? 'backward' : 'forward'
  
  const isForward = propagationDirection === 'forward'
  
  const startLayer = isForward ? 0 : neurons.reduce((max, n) => Math.max(max, n.layer), 0)
  const startNeurons = neurons.filter(n => n.layer === startLayer)
  
  if (startNeurons.length === 0) return
  
  const numStartNeurons = Math.min(startNeurons.length, Math.floor(Math.random() * 3) + 2)
  const shuffled = startNeurons.sort(() => Math.random() - 0.5)
  const selectedNeurons = shuffled.slice(0, numStartNeurons)
  
  selectedNeurons.forEach(startNeuron => {
    const outgoingSynapses = synapses.filter(s => 
      isForward ? s.from === startNeuron : s.to === startNeuron
    )
    
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
    
    startNeuron.activation = Math.min(1, startNeuron.activation + 0.6)
  })
}

/**
 * Sigmoid 激活函数
 */
function sigmoid(x: number): number {
  return 1 / (1 + Math.exp(-x))
}

/**
 * 绘制激活函数曲线（Sigmoid）
 */
function drawActivationFunction(
  ctx: CanvasRenderingContext2D,
  neuron: Neuron,
  time: number
) {
  const curveWidth = 40
  const curveHeight = 30
  const startX = neuron.x + neuron.radius + 10
  const startY = neuron.y - curveHeight / 2
  
  ctx.save()
  ctx.globalAlpha = 0.6
  
  ctx.strokeStyle = 'rgba(99, 102, 241, 0.3)'
  ctx.lineWidth = 1
  ctx.beginPath()
  ctx.moveTo(startX, startY + curveHeight / 2)
  ctx.lineTo(startX + curveWidth, startY + curveHeight / 2)
  ctx.stroke()
  
  ctx.strokeStyle = 'rgba(99, 102, 241, 0.8)'
  ctx.lineWidth = 1.5
  ctx.beginPath()
  
  for (let i = 0; i <= curveWidth; i++) {
    const x = (i / curveWidth) * 6 - 3
    const y = sigmoid(x)
    const px = startX + i
    const py = startY + curveHeight - y * curveHeight
    
    if (i === 0) {
      ctx.moveTo(px, py)
    } else {
      ctx.lineTo(px, py)
    }
  }
  ctx.stroke()
  
  const activationX = startX + ((neuron.activation * 6 - 3 + 3) / 6) * curveWidth
  const activationY = startY + curveHeight - neuron.activation * curveHeight
  
  ctx.fillStyle = 'rgba(255, 255, 255, 0.9)'
  ctx.beginPath()
  ctx.arc(activationX, activationY, 2, 0, Math.PI * 2)
  ctx.fill()
  
  ctx.restore()
}

/**
 * 神经网络动画循环
 */
function animateNeuralNetwork() {
  if (!neuralCanvasRef.value) return
  
  const canvas = neuralCanvasRef.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  ctx.fillStyle = 'rgba(255, 255, 255, 0.1)'
  ctx.fillRect(0, 0, canvasWidth, canvasHeight)
  
  const time = Date.now() * 0.001
  
  // 1. 绘制数据流粒子
  dataFlowParticles.forEach(particle => {
    const layerSpacing = canvasWidth / 5
    const currentLayerX = layerSpacing * (particle.layer + 1)
    const nextLayerX = layerSpacing * (particle.layer + 2)
    
    if (particle.direction === 'forward') {
      particle.x += particle.speed * 0.5
      if (particle.x > nextLayerX) {
        particle.x = currentLayerX
        particle.y = neurons.filter(n => n.layer === particle.layer)[0]?.y || particle.y
      }
    } else {
      particle.x -= particle.speed * 0.5
      if (particle.x < currentLayerX) {
        particle.x = nextLayerX
        particle.y = neurons.filter(n => n.layer === particle.layer + 1)[0]?.y || particle.y
      }
    }
    
    ctx.fillStyle = `rgba(99, 102, 241, ${particle.opacity})`
    ctx.beginPath()
    ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2)
    ctx.fill()
  })
  
  // 2. 绘制突触连线
  synapses.forEach(synapse => {
    const lineWidth = 0.3 + synapse.weight * 1.2
    const activation = (synapse.from.activation + synapse.to.activation) / 2
    
    let baseColor: string
    let baseOpacity: number
    
    if (synapse.weight > 0.75) {
      baseColor = 'rgba(168, 85, 247, '
      baseOpacity = 0.08 + activation * 0.12
    } else if (synapse.weight > 0.45) {
      baseColor = 'rgba(99, 102, 241, '
      baseOpacity = 0.05 + activation * 0.10
    } else {
      baseColor = 'rgba(147, 197, 253, '
      baseOpacity = 0.03 + activation * 0.07
    }
    
    ctx.beginPath()
    ctx.moveTo(synapse.from.x, synapse.from.y)
    ctx.lineTo(synapse.to.x, synapse.to.y)
    ctx.strokeStyle = baseColor + baseOpacity + ')'
    ctx.lineWidth = lineWidth
    ctx.stroke()
    
    if (hoveredSynapse === synapse) {
      ctx.fillStyle = 'rgba(255, 255, 255, 0.95)'
      ctx.font = 'bold 11px monospace'
      ctx.textAlign = 'center'
      ctx.textBaseline = 'bottom'
      
      const labelY = synapse.midpointY - 8
      ctx.fillText(`w=${synapse.weight.toFixed(2)}`, synapse.midpointX, labelY)
      
      const textWidth = ctx.measureText(`w=${synapse.weight.toFixed(2)}`).width
      ctx.fillStyle = 'rgba(99, 102, 241, 0.08)'
      ctx.fillRect(
        synapse.midpointX - textWidth / 2 - 4,
        labelY - 14,
        textWidth + 8,
        16
      )
    }
  })
  
  // 3. 更新和绘制脉冲
  pulses = pulses.filter(pulse => {
    pulse.progress += pulse.speed
    
    if (pulse.progress >= 1) {
      const targetNeuron = propagationDirection === 'forward' ? pulse.synapse.to : pulse.synapse.from
      
      targetNeuron.activation = Math.min(1, targetNeuron.activation + 0.4)
      
      targetNeuron.activationHistory.push(targetNeuron.activation)
      if (targetNeuron.activationHistory.length > 20) {
        targetNeuron.activationHistory.shift()
      }
      
      const nextLayerSynapses = synapses.filter(s => 
        propagationDirection === 'forward' 
          ? s.from === targetNeuron && s.to.layer > targetNeuron.layer
          : s.to === targetNeuron && s.from.layer < targetNeuron.layer
      )
      
      if (nextLayerSynapses.length > 0) {
        const nextSynapse = nextLayerSynapses[Math.floor(Math.random() * nextLayerSynapses.length)]
        pulses.push({
          synapse: nextSynapse,
          progress: 0,
          speed: pulse.speed * (0.95 + Math.random() * 0.1)
        })
        nextSynapse.active = true
      }
      
      pulse.synapse.active = false
      return false
    }
    
    const startX = propagationDirection === 'forward' ? pulse.synapse.from.x : pulse.synapse.to.x
    const startY = propagationDirection === 'forward' ? pulse.synapse.from.y : pulse.synapse.to.y
    const endX = propagationDirection === 'forward' ? pulse.synapse.to.x : pulse.synapse.from.x
    const endY = propagationDirection === 'forward' ? pulse.synapse.to.y : pulse.synapse.from.y
    
    const currentX = startX + (endX - startX) * pulse.progress
    const currentY = startY + (endY - startY) * pulse.progress
    
    const pulseColor = propagationDirection === 'forward' 
      ? 'rgba(99, 102, 241, '
      : 'rgba(236, 72, 153, '
    
    const pulseSize = 8 + pulse.synapse.weight * 8
    
    const gradient = ctx.createRadialGradient(currentX, currentY, 0, currentX, currentY, pulseSize)
    gradient.addColorStop(0, pulseColor + '0.8)')
    gradient.addColorStop(1, pulseColor + '0)')
    ctx.fillStyle = gradient
    ctx.beginPath()
    ctx.arc(currentX, currentY, pulseSize, 0, Math.PI * 2)
    ctx.fill()
    
    ctx.fillStyle = 'rgba(255, 255, 255, 0.9)'
    ctx.beginPath()
    ctx.arc(currentX, currentY, 3, 0, Math.PI * 2)
    ctx.fill()
    
    return true
  })
  
  // 4. 更新和绘制神经元
  neurons.forEach((neuron, index) => {
    const floatX = Math.sin(time * 0.5 + neuron.pulsePhase) * 3
    const floatY = Math.cos(time * 0.3 + neuron.pulsePhase) * 3
    neuron.x += floatX * 0.01
    neuron.y += floatY * 0.01
    
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
    
    neuron.activation = Math.max(neuron.baseActivation, neuron.activation * 0.98)
    
    const periodicActivation = Math.sin(time * 2 + index) * 0.5 + 0.5
    neuron.activation = Math.max(neuron.activation, periodicActivation * 0.3)
    
    const activationRadius = neuron.radius + neuron.activation * 12
    const gradient = ctx.createRadialGradient(
      neuron.x, neuron.y, 0,
      neuron.x, neuron.y, activationRadius
    )
    
    const baseOpacity = 0.3 + neuron.activation * 0.5
    gradient.addColorStop(0, neuron.color + baseOpacity + ')')
    gradient.addColorStop(0.5, neuron.color + (baseOpacity * 0.5) + ')')
    gradient.addColorStop(1, neuron.color + '0)')
    
    ctx.fillStyle = gradient
    ctx.beginPath()
    ctx.arc(neuron.x, neuron.y, activationRadius, 0, Math.PI * 2)
    ctx.fill()
    
    ctx.fillStyle = neuron.color + '0.9)'
    ctx.beginPath()
    ctx.arc(neuron.x, neuron.y, neuron.radius, 0, Math.PI * 2)
    ctx.fill()
    
    if (neuron.activation > 0.6) {
      const pulseRadius = neuron.radius + 4 + Math.sin(time * 5) * 2
      ctx.strokeStyle = neuron.color + (neuron.activation * 0.4) + ')'
      ctx.lineWidth = 2
      ctx.beginPath()
      ctx.arc(neuron.x, neuron.y, pulseRadius, 0, Math.PI * 2)
      ctx.stroke()
    }
    
    if (hoveredNeuron === neuron) {
      drawActivationFunction(ctx, neuron, time)
    }
  })
  
  animationId = requestAnimationFrame(animateNeuralNetwork)
}

/**
 * 光标跟随光效动画
 */
function animateCursorGlow() {
  if (!cursorGlowCanvasRef.value) return
  
  const canvas = cursorGlowCanvasRef.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  if (mouseX > 0 && mouseY > 0) {
    const gradient = ctx.createRadialGradient(
      mouseX, mouseY, 0,
      mouseX, mouseY, 150
    )
    
    gradient.addColorStop(0, 'rgba(99, 102, 241, 0.06)')
    gradient.addColorStop(0.5, 'rgba(139, 92, 246, 0.03)')
    gradient.addColorStop(1, 'rgba(99, 102, 241, 0)')
    
    ctx.fillStyle = gradient
    ctx.beginPath()
    ctx.arc(mouseX, mouseY, 150, 0, Math.PI * 2)
    ctx.fill()
  }
  
  requestAnimationFrame(animateCursorGlow)
}

/**
 * 处理鼠标移动
 */
function handleMouseMove(event: MouseEvent) {
  mouseX = event.clientX
  mouseY = event.clientY
  
  hoveredNeuron = null
  for (const neuron of neurons) {
    const dx = mouseX - neuron.x
    const dy = mouseY - neuron.y
    const distance = Math.sqrt(dx * dx + dy * dy)
    
    if (distance < neuron.radius + 15) {
      hoveredNeuron = neuron
      break
    }
  }
  
  hoveredSynapse = null
  for (const synapse of synapses) {
    const dist = pointToLineDistance(
      mouseX, mouseY,
      synapse.from.x, synapse.from.y,
      synapse.to.x, synapse.to.y
    )
    
    if (dist < 10) {
      hoveredSynapse = synapse
      break
    }
  }
}

/**
 * 计算点到线段的距离
 */
function pointToLineDistance(
  px: number, py: number,
  x1: number, y1: number,
  x2: number, y2: number
): number {
  const A = px - x1
  const B = py - y1
  const C = x2 - x1
  const D = y2 - y1
  
  const dot = A * C + B * D
  const lenSq = C * C + D * D
  let param = -1
  
  if (lenSq !== 0) {
    param = dot / lenSq
  }
  
  let xx, yy
  
  if (param < 0) {
    xx = x1
    yy = y1
  } else if (param > 1) {
    xx = x2
    yy = y2
  } else {
    xx = x1 + param * C
    yy = y1 + param * D
  }
  
  const dx = px - xx
  const dy = py - yy
  
  return Math.sqrt(dx * dx + dy * dy)
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
  
  initNeuralNetwork()
}

/**
 * 按钮磁吸效果
 */
function setupMagneticButtons() {
  const buttons = [primaryButtonRef.value, secondaryButtonRef.value].filter(Boolean)
  
  buttons.forEach(button => {
    if (!button) return
    
    button.addEventListener('mousemove', (e: MouseEvent) => {
      const rect = button.getBoundingClientRect()
      const centerX = rect.left + rect.width / 2
      const centerY = rect.top + rect.height / 2
      
      const dx = e.clientX - centerX
      const dy = e.clientY - centerY
      
      const distance = Math.sqrt(dx * dx + dy * dy)
      const magnetRadius = 150
      
      if (distance < magnetRadius) {
        const magnetStrength = (magnetRadius - distance) / magnetRadius
        const offsetX = dx * magnetStrength * 0.4
        const offsetY = dy * magnetStrength * 0.4
        
        gsap.to(button, {
          x: offsetX,
          y: offsetY,
          duration: 0.3,
          ease: 'power2.out'
        })
      }
    })
    
    button.addEventListener('mouseleave', () => {
      gsap.to(button, {
        x: 0,
        y: 0,
        duration: 0.5,
        ease: 'elastic.out(1, 0.5)'
      })
    })
  })
  
  console.log('✅ Magnetic buttons initialized')
}

/**
 * 卡片 3D 悬浮效果
 */
function setup3DCards() {
  const allCards = [...featureCardsRef.value, ...contentCardsRef.value].filter(Boolean)
  
  allCards.forEach(card => {
    if (!card) return
    
    card.addEventListener('mousemove', (e: MouseEvent) => {
      const rect = card.getBoundingClientRect()
      const x = e.clientX - rect.left
      const y = e.clientY - rect.top
      
      const centerX = rect.width / 2
      const centerY = rect.height / 2
      
      const rotateX = (y - centerY) / 20
      const rotateY = (centerX - x) / 20
      
      gsap.to(card, {
        rotateX: rotateX,
        rotateY: rotateY,
        transformPerspective: 1000,
        duration: 0.3,
        ease: 'power2.out'
      })
    })
    
    card.addEventListener('mouseleave', () => {
      gsap.to(card, {
        rotateX: 0,
        rotateY: 0,
        duration: 0.5,
        ease: 'elastic.out(1, 0.5)'
      })
    })
  })
  
  console.log('✅ 3D card hover effects initialized')
}

/**
 * 滚动进度条
 */
function setupScrollProgress() {
  if (!lenis) return
  
  lenis.on('scroll', ({ scroll }: { scroll: number }) => {
    const scrollTop = scroll
    const docHeight = document.documentElement.scrollHeight - window.innerHeight
    scrollProgress.value = (scrollTop / docHeight) * 100
  })
  
  console.log('✅ Scroll progress tracking initialized')
}

/**
 * 文字逐字动画
 */
function animateTextByChars(element: HTMLElement | null, delay: number = 0) {
  if (!element) return
  
  const text = element.textContent || ''
  element.textContent = ''
  
  const chars = text.split('')
  const charSpans: HTMLSpanElement[] = []
  
  chars.forEach(char => {
    const span = document.createElement('span')
    span.textContent = char === ' ' ? '\u00A0' : char
    span.style.display = 'inline-block'
    span.style.opacity = '0'
    span.style.transform = 'translateY(40px) rotateX(-90deg)'
    element.appendChild(span)
    charSpans.push(span)
  })
  
  gsap.to(charSpans, {
    opacity: 1,
    y: 0,
    rotateX: 0,
    duration: 0.8,
    stagger: 0.03,
    delay: delay,
    ease: 'back.out(1.7)'
  })
}

/**
 * 执行 Hero 区域入场动画序列
 */
function animateHeroEntrance() {
  const tl = gsap.timeline({ delay: 0.3 })
  
  if (neuralCanvasRef.value) {
    tl.fromTo(neuralCanvasRef.value,
      { opacity: 0 },
      { opacity: 1, duration: 1.5, ease: 'power2.out' },
      0
    )
  }
  
  // 标题文字逐字动画
  if (heroTitleRef.value) {
    tl.call(() => {
      animateTextByChars(heroTitleRef.value, 0)
    }, undefined, 0.2)
  }
  
  if (heroSubtitleRef.value) {
    tl.fromTo(heroSubtitleRef.value,
      { y: 50, opacity: 0 },
      { y: 0, opacity: 1, duration: 1, ease: 'power3.out' },
      1.2
    )
  }
  
  if (heroButtonsRef.value) {
    tl.fromTo(heroButtonsRef.value,
      { y: 30, opacity: 0 },
      { y: 0, opacity: 1, duration: 0.8, ease: 'power3.out' },
      1.5
    )
  }
  
  if (scrollHintRef.value) {
    tl.fromTo(scrollHintRef.value,
      { y: 20, opacity: 0 },
      { 
        y: 0, 
        opacity: 1, 
        duration: 0.6,
        ease: 'power3.out',
        onComplete: () => {
          gsap.to(scrollHintRef.value, {
            y: 10,
            duration: 1.5,
            ease: 'power1.inOut',
            repeat: -1,
            yoyo: true
          })
        }
      },
      1.7
    )
  }
  
  console.log('✅ Hero entrance animation started')
}

/**
 * 设置滚动触发动画
 */
function setupScrollAnimations() {
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
  
  // 多层视差滚动效果
  if (heroTitleRef.value) {
    gsap.to(heroTitleRef.value, {
      y: -80,
      scrollTrigger: {
        trigger: '.hero-section',
        start: 'top top',
        end: 'bottom top',
        scrub: 0.5
      }
    })
  }
  
  if (neuralCanvasRef.value) {
    gsap.to(neuralCanvasRef.value, {
      y: -40,
      scrollTrigger: {
        trigger: '.hero-section',
        start: 'top top',
        end: 'bottom top',
        scrub: 1.5
      }
    })
  }
  
  if (scrollHintRef.value) {
    gsap.to(scrollHintRef.value, {
      opacity: 0,
      y: 20,
      scrollTrigger: {
        trigger: '.hero-section',
        start: 'top top',
        end: '20% top',
        scrub: true
      }
    })
  }
  
  console.log('✅ Scroll animations configured')
}

/**
 * 设置 Lenis 和 ScrollTrigger 的同步
 */
function setupLenisScrollTrigger() {
  if (!lenis) return
  
  lenis.on('scroll', ScrollTrigger.update)
  
  lenis.on('scroll', () => {
    gsap.ticker.tick()
  })
}
</script>

<template>
  <!-- 滚动进度条 -->
  <div class="scroll-progress-bar-container">
    <div class="scroll-progress-bar" :style="{ width: scrollProgress + '%' }"></div>
  </div>
  
  <!-- 神经网络背景 -->
  <canvas ref="neuralCanvasRef" class="fixed inset-0 w-full h-full z-0"></canvas>
  
  <!-- 光标跟随光效 -->
  <canvas ref="cursorGlowCanvasRef" class="fixed inset-0 w-full h-full z-[1] pointer-events-none"></canvas>
  
  <!-- 主容器 -->
  <div class="home-jiejoe min-h-screen bg-gradient-to-b from-white to-blue-50/30 overflow-hidden">
    
    <!-- Hero 区域 -->
    <section class="hero-section relative min-h-screen flex items-center justify-center z-10">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 ref="heroTitleRef"
            class="text-6xl md:text-8xl lg:text-9xl font-black text-slate-900 mb-8 leading-none tracking-tighter"
            style="transform-style: preserve-3d;">
          沈阳工业大学计算机程序设计社团
        </h1>
        
        <p ref="heroSubtitleRef"
           class="text-xl md:text-2xl lg:text-3xl text-indigo-600 mb-16 max-w-4xl mx-auto font-light tracking-wide">
          培养创新思维 · 提升编程能力 · 打造技术社区
        </p>
        
        <div ref="heroButtonsRef" class="flex flex-col sm:flex-row gap-6 justify-center items-center mb-16">
          <button ref="primaryButtonRef" class="cta-button-jiejoe cta-primary-jiejoe">
            <span>立即加入</span>
            <svg class="w-6 h-6 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6" />
            </svg>
          </button>
          
          <button ref="secondaryButtonRef" class="cta-button-jiejoe cta-secondary-jiejoe">
            <span>了解更多</span>
          </button>
        </div>
      </div>
      
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
          <div ref="featureCardsRef" class="feature-card-jiejoe group">
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
          <div ref="featureCardsRef" class="feature-card-jiejoe group">
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
          <div ref="featureCardsRef" class="feature-card-jiejoe group">
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
          <div ref="contentCardsRef" class="content-card-jiejoe group">
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
          <div ref="contentCardsRef" class="content-card-jiejoe group">
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
html {
  scroll-behavior: auto;
}

canvas {
  pointer-events: auto;
  cursor: crosshair;
}

/* 滚动进度条 */
.scroll-progress-bar-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: rgba(99, 102, 241, 0.1);
  z-index: 9999;
}

.scroll-progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #6366f1, #8b5cf6, #a855f7);
  transition: width 0.1s ease-out;
  box-shadow: 0 0 10px rgba(99, 102, 241, 0.5);
}

.cta-button-jiejoe {
  @apply px-12 py-6 rounded-xl font-bold text-xl transition-all duration-500 flex items-center;
}

.cta-primary-jiejoe {
  @apply bg-gradient-to-r from-indigo-600 to-purple-600 text-white;
  box-shadow: 0 8px 30px rgba(99, 102, 241, 0.4);
}

.cta-primary-jiejoe:hover {
  @apply from-indigo-500 to-purple-500;
  box-shadow: 0 12px 40px rgba(99, 102, 241, 0.5);
}

.cta-secondary-jiejoe {
  @apply border-2 border-indigo-300 text-indigo-600;
  background: rgba(99, 102, 241, 0.05);
}

.cta-secondary-jiejoe:hover {
  @apply border-indigo-500 bg-indigo-50;
}

.feature-card-jiejoe {
  transform-style: preserve-3d;
  perspective: 1000px;
  will-change: transform;
}

.content-card-jiejoe {
  transform-style: preserve-3d;
  perspective: 1000px;
  will-change: transform;
}
</style>
