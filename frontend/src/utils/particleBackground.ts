/**
 * Canvas 粒子背景系统
 * 实现浮动粒子、连线效果和鼠标交互
 */

export interface ParticleConfig {
  particleCount?: number
  particleColor?: string
  lineColor?: string
  mouseRadius?: number
  speed?: number
  connectionDistance?: number
}

class Particle {
  x: number
  y: number
  vx: number
  vy: number
  size: number
  
  constructor(canvasWidth: number, canvasHeight: number) {
    this.x = Math.random() * canvasWidth
    this.y = Math.random() * canvasHeight
    this.vx = (Math.random() - 0.5) * 0.5
    this.vy = (Math.random() - 0.5) * 0.5
    this.size = Math.random() * 2 + 1
  }
  
  update(mouse: { x: number; y: number }, speed: number) {
    // 基础移动
    this.x += this.vx * speed
    this.y += this.vy * speed
    
    // 边界检测（循环）
    if (this.x < 0) this.x = window.innerWidth
    if (this.x > window.innerWidth) this.x = 0
    if (this.y < 0) this.y = window.innerHeight
    if (this.y > window.innerHeight) this.y = 0
    
    // 鼠标交互 - 粒子被鼠标推开
    const dx = mouse.x - this.x
    const dy = mouse.y - this.y
    const distance = Math.sqrt(dx * dx + dy * dy)
    
    if (distance < 150) {
      const forceDirectionX = dx / distance
      const forceDirectionY = dy / distance
      const force = (150 - distance) / 150
      const directionX = forceDirectionX * force * 2
      const directionY = forceDirectionY * force * 2
      
      this.x -= directionX
      this.y -= directionY
    }
  }
  
  draw(ctx: CanvasRenderingContext2D, color: string) {
    ctx.fillStyle = color
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    ctx.closePath()
    ctx.fill()
  }
}

export class ParticleBackground {
  private canvas: HTMLCanvasElement
  private ctx: CanvasRenderingContext2D
  private particles: Particle[]
  private mouse: { x: number; y: number }
  private animationId: number | null = null
  private config: Required<ParticleConfig>
  
  constructor(canvas: HTMLCanvasElement, config: ParticleConfig = {}) {
    this.canvas = canvas
    this.ctx = canvas.getContext('2d')!
    this.mouse = { x: -1000, y: -1000 }
    this.config = {
      particleCount: 80,
      particleColor: 'rgba(255, 255, 255, 0.6)',
      lineColor: 'rgba(255, 255, 255, 0.15)',
      mouseRadius: 150,
      speed: 0.5,
      connectionDistance: 120,
      ...config
    }
    this.particles = []
    
    this.resize()
    this.initParticles()
    this.bindEvents()
  }
  
  private resize() {
    this.canvas.width = window.innerWidth
    this.canvas.height = window.innerHeight
  }
  
  private initParticles() {
    this.particles = []
    for (let i = 0; i < this.config.particleCount; i++) {
      this.particles.push(new Particle(this.canvas.width, this.canvas.height))
    }
  }
  
  private bindEvents() {
    // 窗口大小变化
    window.addEventListener('resize', () => {
      this.resize()
      this.initParticles()
    })
    
    // 鼠标移动
    window.addEventListener('mousemove', (e) => {
      this.mouse.x = e.clientX
      this.mouse.y = e.clientY
    })
    
    // 鼠标离开
    window.addEventListener('mouseleave', () => {
      this.mouse.x = -1000
      this.mouse.y = -1000
    })
    
    // 触摸设备支持
    window.addEventListener('touchmove', (e) => {
      if (e.touches.length > 0) {
        const touch = e.touches[0]
        if (touch) {
          this.mouse.x = touch.clientX
          this.mouse.y = touch.clientY
        }
      }
    })
  }
  
  private drawLines() {
    for (let i = 0; i < this.particles.length; i++) {
      for (let j = i + 1; j < this.particles.length; j++) {
        const particleA = this.particles[i]
        const particleB = this.particles[j]
        
        if (!particleA || !particleB) continue
        
        const dx = particleA.x - particleB.x
        const dy = particleA.y - particleB.y
        const distance = Math.sqrt(dx * dx + dy * dy)
        
        if (distance < this.config.connectionDistance) {
          this.ctx.beginPath()
          this.ctx.strokeStyle = this.config.lineColor
          this.ctx.lineWidth = 0.5
          this.ctx.moveTo(particleA.x, particleA.y)
          this.ctx.lineTo(particleB.x, particleB.y)
          this.ctx.stroke()
        }
      }
    }
  }
  
  private animate() {
    this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)
    
    // 更新和绘制粒子
    this.particles.forEach(particle => {
      particle.update(this.mouse, this.config.speed)
      particle.draw(this.ctx, this.config.particleColor)
    })
    
    // 绘制连线
    this.drawLines()
    
    this.animationId = requestAnimationFrame(() => this.animate())
  }
  
  public start() {
    if (!this.animationId) {
      this.animate()
    }
  }
  
  public stop() {
    if (this.animationId) {
      cancelAnimationFrame(this.animationId)
      this.animationId = null
    }
  }
  
  public destroy() {
    this.stop()
  }
}
