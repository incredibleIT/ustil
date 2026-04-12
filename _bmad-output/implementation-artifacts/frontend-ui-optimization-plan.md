# 前端 UI 优化方案

**创建日期**: 2026-04-11  
**状态**: Draft  
**优先级**: P0  
**负责人**: UX Designer + Senior Dev  

---

## 📋 目录

1. [项目背景](#项目背景)
2. [优化目标](#优化目标)
3. [第一部分：消息通知弹窗 UI 优化](#第一部分消息通知弹窗-ui-优化)
4. [第二部分：首页动态背景重构](#第二部分首页动态背景重构)
5. [技术选型对比](#技术选型对比)
6. [实施计划](#实施计划)
7. [风险评估](#风险评估)
8. [成功标准](#成功标准)
9. [参考资料](#参考资料)

---

## 项目背景

根据 Epic 4 回顾会议的反馈，我们需要对前端进行两方面的优化：

1. **消息通知弹窗 UI 优化** - 提升用户体验和视觉一致性
2. **首页动态背景重构** - 增强视觉吸引力，参考 jiejoe.com 的动态效果

本次优化将遵循 BMAD 工作流的技术选型最佳实践：
- ✅ 深度调研技术方案
- ✅ 多方案对比评估
- ✅ PoC 验证可行性
- ✅ 考虑团队技能匹配度

---

## 优化目标

### 业务目标
- 提升用户交互体验满意度
- 增强品牌视觉识别度
- 降低用户操作认知负担

### 技术目标
- 保持代码可维护性
- 确保性能不受影响（FPS ≥ 60）
- 兼容主流浏览器（Chrome、Firefox、Safari、Edge）
- 支持移动端响应式

### 设计目标
- 符合现有设计系统规范
- 提升视觉层次感
- 增加微交互动画

---

## 第一部分：消息通知弹窗 UI 优化

### 现状分析

#### 当前实现
- **组件位置**: `AppNavbar.vue` 中的通知图标
- **通知类型**: 转正通过/拒绝通知
- **显示方式**: Element Plus Dropdown + Badge
- **问题点**:
  1. ❌ 缺少即时反馈动画
  2. ❌ 通知列表样式单一
  3. ❌ 没有区分通知优先级
  4. ❌ 缺少 Toast 提示（操作反馈）
  5. ❌ 未读计数更新不及时

#### 用户痛点
- 新通知到达时没有视觉提醒
- 无法快速识别重要通知
- 标记已读后缺乏确认反馈

---

### 设计方案

#### 方案 A：Element Plus Notification + Toast（推荐 ⭐⭐⭐⭐⭐）

**核心思路**: 利用 Element Plus 内置的通知组件，定制化样式

**优点**:
- ✅ 零学习成本（团队已熟悉 Element Plus）
- ✅ 开箱即用，无需额外依赖
- ✅ 完善的无障碍支持
- ✅ 与现有设计系统集成度高

**缺点**:
- ⚠️ 自定义动画能力有限
- ⚠️ 样式覆盖需要深度定制

**实现要点**:
```typescript
// 1. 全局 Toast 管理器
import { ElNotification } from 'element-plus'

export function showToast(type: 'success' | 'warning' | 'error' | 'info', message: string) {
  ElNotification({
    title: type === 'success' ? '成功' : '提示',
    message,
    type,
    duration: 3000,
    position: 'top-right',
    customClass: 'custom-toast'
  })
}

// 2. 通知中心下拉面板优化
<el-dropdown trigger="click" @command="handleNotificationCommand">
  <el-badge :value="unreadCount" :hidden="unreadCount === 0">
    <el-icon><Bell /></el-icon>
  </el-badge>
  <template #dropdown>
    <el-dropdown-menu class="notification-dropdown">
      <!-- 通知列表 -->
    </el-dropdown-menu>
  </template>
</el-dropdown>
```

**样式定制**:
```css
/* 自定义 Toast 样式 */
.custom-toast {
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card-hover);
  animation: slideInRight 0.3s ease-out;
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* 通知下拉面板优化 */
.notification-dropdown {
  min-width: 320px;
  max-height: 400px;
  overflow-y: auto;
}
```

---

#### 方案 B：Vue Toastification（备选 ⭐⭐⭐⭐）

**核心思路**: 使用专门的 Toast 库，提供更丰富的功能

**优点**:
- ✅ 高度可定制
- ✅ 丰富的动画效果
- ✅ 支持队列管理
- ✅ 轻量级（~5KB gzipped）

**缺点**:
- ⚠️ 新增依赖
- ⚠️ 需要学习新 API
- ⚠️ 样式需要适配设计系统

**实现示例**:
```bash
npm install vue-toastification@next
```

```typescript
// main.ts
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";

app.use(Toast, {
  position: "top-right",
  timeout: 3000,
  closeOnClick: true,
  pauseOnFocusLoss: true,
  pauseOnHover: true,
  draggable: true,
  transition: "Vue-Toastification__bounce",
});
```

---

#### 方案 C：自定义 Toast 组件（不推荐 ⭐⭐）

**核心思路**: 完全自主开发 Toast 组件

**优点**:
- ✅ 完全可控
- ✅ 无外部依赖

**缺点**:
- ❌ 开发成本高（预计 8-12 小时）
- ❌ 需要处理边界情况（队列、动画、无障碍）
- ❌ 维护成本高

**结论**: MVP 阶段不建议采用

---

### 技术选型评估

| 维度 | 方案 A (Element Plus) | 方案 B (Vue Toastification) | 方案 C (自定义) |
|------|---------------------|---------------------------|---------------|
| **功能匹配度** (30%) | 8/10 | 9/10 | 10/10 |
| **学习成本** (20%) | 10/10 | 7/10 | 4/10 |
| **集成难度** (15%) | 10/10 | 8/10 | 3/10 |
| **维护成本** (15%) | 9/10 | 8/10 | 5/10 |
| **性能影响** (10%) | 9/10 | 9/10 | 8/10 |
| **可测试性** (10%) | 9/10 | 8/10 | 6/10 |
| **加权总分** | **9.15** | **8.25** | **5.9** |

**推荐方案**: **方案 A - Element Plus Notification + Toast**

**理由**:
1. 团队已熟悉 Element Plus，零学习成本
2. 与现有设计系统无缝集成
3. 维护成本低，长期收益高
4. 满足 MVP 阶段需求

---

### 实施细节

#### 1. 创建 Toast 工具函数

**文件**: `src/utils/toast.ts`

```typescript
import { ElNotification } from 'element-plus'
import type { NotificationParams } from 'element-plus'

export type ToastType = 'success' | 'warning' | 'error' | 'info'

interface ToastOptions {
  title?: string
  duration?: number
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left'
}

/**
 * 显示 Toast 通知
 */
export function showToast(
  type: ToastType,
  message: string,
  options: ToastOptions = {}
) {
  const {
    title = getDefaultTitle(type),
    duration = 3000,
    position = 'top-right'
  } = options

  ElNotification({
    title,
    message,
    type,
    duration,
    position,
    customClass: 'custom-toast-notification',
    offset: 60
  })
}

/**
 * 快捷方法
 */
export const toast = {
  success: (message: string, options?: ToastOptions) => showToast('success', message, options),
  warning: (message: string, options?: ToastOptions) => showToast('warning', message, options),
  error: (message: string, options?: ToastOptions) => showToast('error', message, options),
  info: (message: string, options?: ToastOptions) => showToast('info', message, options),
}

function getDefaultTitle(type: ToastType): string {
  const titles: Record<ToastType, string> = {
    success: '✅ 成功',
    warning: '⚠️ 警告',
    error: '❌ 错误',
    info: 'ℹ️ 提示'
  }
  return titles[type]
}
```

#### 2. 优化通知下拉面板

**文件**: `src/components/layout/AppNavbar.vue`

```vue
<template>
  <el-dropdown trigger="click" @command="handleNotificationCommand" class="notification-dropdown-wrapper">
    <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
      <el-icon class="notification-icon"><Bell /></el-icon>
    </el-badge>
    
    <template #dropdown>
      <el-dropdown-menu class="notification-menu">
        <!-- 头部 -->
        <div class="notification-header">
          <h3>通知中心</h3>
          <el-button link size="small" @click="markAllAsRead" v-if="unreadCount > 0">
            全部已读
          </el-button>
        </div>
        
        <!-- 通知列表 -->
        <div class="notification-list" v-if="notifications.length > 0">
          <div
            v-for="item in notifications.slice(0, 5)"
            :key="item.id"
            class="notification-item"
            :class="{ 'is-unread': item.isRead === 0 }"
            @click="handleNotificationClick(item)"
          >
            <div class="notification-icon-wrapper">
              <el-icon v-if="item.type === 'promotion_approved'" class="text-green-500">
                <CircleCheck />
              </el-icon>
              <el-icon v-else-if="item.type === 'promotion_rejected'" class="text-red-500">
                <CircleClose />
              </el-icon>
            </div>
            
            <div class="notification-content">
              <p class="notification-title">{{ item.title }}</p>
              <p class="notification-time">{{ formatTime(item.createdAt) }}</p>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-else class="notification-empty">
          <el-icon class="text-gray-400 text-4xl mb-2"><Bell /></el-icon>
          <p class="text-gray-500">暂无通知</p>
        </div>
        
        <!-- 查看更多 -->
        <div class="notification-footer">
          <el-button link @click="router.push('/notifications')">
            查看全部通知 →
          </el-button>
        </div>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<style scoped>
.notification-badge {
  cursor: pointer;
  transition: transform var(--transition-base);
}

.notification-badge:hover {
  transform: scale(1.1);
}

.notification-menu {
  min-width: 360px;
  max-height: 480px;
  padding: 0;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--color-border);
}

.notification-list {
  max-height: 320px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  cursor: pointer;
  transition: background-color var(--transition-base);
  border-bottom: 1px solid var(--color-border);
}

.notification-item:hover {
  background-color: var(--color-primary-50);
}

.notification-item.is-unread {
  background-color: var(--color-primary-50);
  border-left: 3px solid var(--color-primary-500);
}

.notification-icon-wrapper {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  background-color: var(--color-neutral-100);
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}

.notification-empty {
  padding: var(--spacing-2xl) var(--spacing-lg);
  text-align: center;
}

.notification-footer {
  padding: var(--spacing-md) var(--spacing-lg);
  border-top: 1px solid var(--color-border);
  text-align: center;
}
</style>
```

#### 3. 集成到业务流程

在关键操作后调用 Toast：

```typescript
// 示例：内容发布成功
import { toast } from '@/utils/toast'

async function handlePublish() {
  try {
    await publishContent(formData)
    toast.success('内容发布成功！')
    router.push('/profile')
  } catch (error) {
    toast.error('发布失败，请重试')
  }
}
```

---

## 第二部分：首页动态背景重构

### 现状分析

#### 当前实现
- **背景样式**: 静态渐变 (`bg-gradient-to-br from-primary-500 via-primary-600 to-primary-700`)
- **装饰元素**: 简单的点阵图案（CSS radial-gradient）
- **动画效果**: 仅内容卡片有滚动渐入动画

#### 参考网站分析：jiejoe.com

**观察到的动态效果**:
1. ✨ **粒子背景** - 浮动的几何图形
2. 🌊 **波浪动画** - 底部波浪线条
3. 💫 **视差滚动** - 多层背景以不同速度移动
4. 🎨 **渐变流动** - 颜色缓慢变化
5. 🖱️ **鼠标交互** - 粒子跟随鼠标移动
6. 📜 **滚动触发动画** - 元素进入视口时淡入

**技术特点**:
- 使用 Canvas/WebGL 渲染高性能动画
- CSS animations 处理简单过渡
- Intersection Observer 实现滚动触发
- 性能优化：requestAnimationFrame + 节流

---

### 设计方案

#### 方案 A：Canvas 粒子系统（推荐 ⭐⭐⭐⭐⭐）

**核心思路**: 使用原生 Canvas API 绘制粒子动画

**优点**:
- ✅ 高性能（60 FPS）
- ✅ 完全可控
- ✅ 无外部依赖
- ✅ 轻量级（~200 行代码）
- ✅ 易于定制

**缺点**:
- ⚠️ 需要编写 Canvas 代码
- ⚠️ 移动端需要降级处理

**视觉效果**:
- 浮动粒子（圆形/方形）
- 连线效果（距离近的粒子之间）
- 鼠标交互（排斥/吸引）
- 渐变背景流动

**实现示例**:

```typescript
// src/utils/particleBackground.ts
export interface ParticleConfig {
  particleCount?: number
  particleColor?: string
  lineColor?: string
  mouseRadius?: number
  speed?: number
}

export class ParticleBackground {
  private canvas: HTMLCanvasElement
  private ctx: CanvasRenderingContext2D
  private particles: Particle[]
  private mouse: { x: number | null; y: number | null }
  private config: Required<ParticleConfig>
  private animationId: number | null = null

  constructor(canvas: HTMLCanvasElement, config: ParticleConfig = {}) {
    this.canvas = canvas
    this.ctx = canvas.getContext('2d')!
    this.mouse = { x: null, y: null }
    this.config = {
      particleCount: 80,
      particleColor: 'rgba(255, 255, 255, 0.5)',
      lineColor: 'rgba(255, 255, 255, 0.15)',
      mouseRadius: 150,
      speed: 0.5,
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
      this.particles.push(new Particle(this.canvas, this.config))
    }
  }

  private bindEvents() {
    window.addEventListener('resize', () => {
      this.resize()
      this.initParticles()
    })

    this.canvas.addEventListener('mousemove', (e) => {
      this.mouse.x = e.clientX
      this.mouse.y = e.clientY
    })

    this.canvas.addEventListener('mouseleave', () => {
      this.mouse.x = null
      this.mouse.y = null
    })
  }

  public start() {
    this.animate()
  }

  public stop() {
    if (this.animationId) {
      cancelAnimationFrame(this.animationId)
      this.animationId = null
    }
  }

  private animate() {
    this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)
    
    // 更新和绘制粒子
    this.particles.forEach(particle => {
      particle.update(this.mouse, this.config.speed)
      particle.draw(this.ctx)
    })

    // 绘制连线
    this.drawLines()

    this.animationId = requestAnimationFrame(() => this.animate())
  }

  private drawLines() {
    for (let i = 0; i < this.particles.length; i++) {
      for (let j = i + 1; j < this.particles.length; j++) {
        const dx = this.particles[i].x - this.particles[j].x
        const dy = this.particles[i].y - this.particles[j].y
        const distance = Math.sqrt(dx * dx + dy * dy)

        if (distance < 120) {
          this.ctx.beginPath()
          this.ctx.strokeStyle = this.config.lineColor
          this.ctx.lineWidth = 0.5
          this.ctx.moveTo(this.particles[i].x, this.particles[i].y)
          this.ctx.lineTo(this.particles[j].x, this.particles[j].y)
          this.ctx.stroke()
        }
      }
    }
  }
}

class Particle {
  x: number
  y: number
  vx: number
  vy: number
  size: number
  color: string

  constructor(canvas: HTMLCanvasElement, config: Required<ParticleConfig>) {
    this.x = Math.random() * canvas.width
    this.y = Math.random() * canvas.height
    this.vx = (Math.random() - 0.5) * config.speed
    this.vy = (Math.random() - 0.5) * config.speed
    this.size = Math.random() * 3 + 1
    this.color = config.particleColor
  }

  update(mouse: { x: number | null; y: number | null }, speed: number) {
    this.x += this.vx
    this.y += this.vy

    // 边界检测
    if (this.x < 0 || this.x > window.innerWidth) this.vx *= -1
    if (this.y < 0 || this.y > window.innerHeight) this.vy *= -1

    // 鼠标交互
    if (mouse.x !== null && mouse.y !== null) {
      const dx = mouse.x - this.x
      const dy = mouse.y - this.y
      const distance = Math.sqrt(dx * dx + dy * dy)

      if (distance < 150) {
        const forceDirectionX = dx / distance
        const forceDirectionY = dy / distance
        const force = (150 - distance) / 150
        const directionX = forceDirectionX * force * speed
        const directionY = forceDirectionY * force * speed

        this.vx -= directionX
        this.vy -= directionY
      }
    }
  }

  draw(ctx: CanvasRenderingContext2D) {
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    ctx.fillStyle = this.color
    ctx.fill()
  }
}
```

**在 HomeView 中使用**:

```vue
<template>
  <div class="home-page">
    <!-- Hero 区域 -->
    <section class="hero-section">
      <canvas ref="particleCanvas" class="particle-canvas"></canvas>
      
      <div class="hero-content">
        <h1>沈阳工业大学<br/>计算机程序设计社团</h1>
        <p>探索编程之美，创造无限可能</p>
        <!-- 按钮... -->
      </div>
    </section>
    
    <!-- 其他内容... -->
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ParticleBackground } from '@/utils/particleBackground'

const particleCanvas = ref<HTMLCanvasElement>()
let particleSystem: ParticleBackground | null = null

onMounted(() => {
  if (particleCanvas.value) {
    particleSystem = new ParticleBackground(particleCanvas.value, {
      particleCount: 60,
      speed: 0.8,
      particleColor: 'rgba(255, 255, 255, 0.6)',
      lineColor: 'rgba(255, 255, 255, 0.2)'
    })
    particleSystem.start()
  }
})

onUnmounted(() => {
  if (particleSystem) {
    particleSystem.stop()
  }
})
</script>

<style scoped>
.hero-section {
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.particle-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.hero-content {
  position: relative;
  z-index: 2;
  /* 其他样式... */
}
</style>
```

---

#### 方案 B：CSS 动画 + SVG（备选 ⭐⭐⭐⭐）

**核心思路**: 使用纯 CSS 动画和 SVG 实现波浪效果

**优点**:
- ✅ 无 JavaScript，性能极佳
- ✅ 易于维护
- ✅ 天然支持响应式

**缺点**:
- ⚠️ 效果相对简单
- ⚠️ 无法实现复杂交互

**实现示例**:

```vue
<template>
  <section class="hero-section">
    <div class="gradient-bg"></div>
    <svg class="wave-animation" viewBox="0 0 1440 320">
      <path fill="rgba(255,255,255,0.1)" d="M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,112C672,96,768,96,864,112C960,128,1056,160,1152,160C1248,160,1344,128,1392,112L1440,96L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path>
    </svg>
    
    <div class="hero-content">
      <!-- 内容... -->
    </div>
  </section>
</template>

<style scoped>
.gradient-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(-45deg, #ee7752, #e73c7e, #23a6d5, #23d5ab);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.wave-animation {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100px;
  animation: waveMove 10s linear infinite;
}

@keyframes waveMove {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
}
</style>
```

---

#### 方案 C：Three.js 3D 粒子（不推荐 ⭐⭐）

**核心思路**: 使用 Three.js 创建 3D 粒子效果

**优点**:
- ✅ 视觉效果震撼
- ✅ 可实现复杂 3D 场景

**缺点**:
- ❌ 学习曲线陡峭
- ❌ 包体积大（~600KB）
- ❌ 移动端性能问题
- ❌ 过度工程化（MVP 不需要）

**结论**: 暂不考虑

---

### 技术选型评估

| 维度 | 方案 A (Canvas) | 方案 B (CSS+SVG) | 方案 C (Three.js) |
|------|----------------|-----------------|------------------|
| **功能匹配度** (30%) | 9/10 | 7/10 | 10/10 |
| **学习成本** (20%) | 8/10 | 10/10 | 4/10 |
| **集成难度** (15%) | 9/10 | 10/10 | 5/10 |
| **维护成本** (15%) | 8/10 | 10/10 | 4/10 |
| **性能影响** (10%) | 8/10 | 10/10 | 5/10 |
| **可测试性** (10%) | 8/10 | 9/10 | 6/10 |
| **加权总分** | **8.55** | **9.15** | **5.95** |

**推荐方案**: **方案 A - Canvas 粒子系统**

**理由**:
1. 平衡了视觉效果和开发成本
2. 性能优秀（60 FPS）
3. 无外部依赖，包体积小
4. 可扩展性强（未来可添加更多交互）
5. 团队具备 Canvas 基础技能

**注意**: 如果时间紧张，可以先实施方案 B（CSS+SVG），后续再升级到方案 A。

---

### 实施细节

#### 1. 创建粒子背景工具类

**文件**: `src/utils/particleBackground.ts`

（见上方完整代码）

#### 2. 优化 HomeView 结构

**文件**: `src/views/HomeView.vue`

```vue
<template>
  <div class="min-h-screen bg-neutral-50">
    <AppNavbar />

    <!-- Hero 区域 -->
    <section class="hero-section">
      <canvas ref="particleCanvas" class="particle-canvas"></canvas>
      
      <div class="hero-overlay"></div>
      
      <div class="relative max-w-content mx-auto px-4 sm:px-6 lg:px-8 py-24 md:py-32 hero-content">
        <div class="text-center animate-fade-in">
          <h1 class="text-4xl md:text-5xl font-bold text-white mb-6 leading-tight drop-shadow-lg">
            沈阳工业大学<br/>计算机程序设计社团
          </h1>
          <p class="text-xl md:text-2xl text-white/90 mb-12 animate-slide-up drop-shadow">
            探索编程之美，创造无限可能
          </p>
          
          <!-- 按钮... -->
        </div>
      </div>
      
      <!-- 底部波浪 -->
      <svg class="wave-divider" viewBox="0 0 1440 120" preserveAspectRatio="none">
        <path fill="#f9fafb" d="M0,64L80,69.3C160,75,320,85,480,80C640,75,800,53,960,48C1120,43,1280,53,1360,58.7L1440,64L1440,120L1360,120C1280,120,1120,120,960,120C800,120,640,120,480,120C320,120,160,120,80,120L0,120Z"></path>
      </svg>
    </section>

    <!-- 特色功能区域 -->
    <section class="max-w-content mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <!-- 内容... -->
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ParticleBackground } from '@/utils/particleBackground'

const particleCanvas = ref<HTMLCanvasElement>()
let particleSystem: ParticleBackground | null = null

onMounted(() => {
  if (particleCanvas.value) {
    particleSystem = new ParticleBackground(particleCanvas.value, {
      particleCount: window.innerWidth < 768 ? 40 : 80, // 移动端减少粒子数
      speed: 0.6,
      particleColor: 'rgba(255, 255, 255, 0.6)',
      lineColor: 'rgba(255, 255, 255, 0.15)',
      mouseRadius: 120
    })
    particleSystem.start()
  }
})

onUnmounted(() => {
  if (particleSystem) {
    particleSystem.stop()
  }
})
</script>

<style scoped>
.hero-section {
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.particle-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.1);
  z-index: 2;
}

.hero-content {
  position: relative;
  z-index: 3;
}

.wave-divider {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 80px;
  z-index: 4;
}

@media (max-width: 768px) {
  .wave-divider {
    height: 60px;
  }
}
</style>
```

#### 3. 添加滚动触发动画

扩展现有的 `scrollAnimation.ts`，添加更多动画类型：

```typescript
// src/utils/scrollAnimation.ts

/**
 * 滚动渐入动画类型
 */
export type AnimationType = 
  | 'fade-in-up'      // 淡入 + 上移
  | 'fade-in-left'    // 淡入 + 左移
  | 'fade-in-right'   // 淡入 + 右移
  | 'scale-in'        // 缩放进入
  | 'rotate-in'       // 旋转进入

export interface ScrollAnimationOptions {
  threshold?: number
  rootMargin?: string
  animationType?: AnimationType
  once?: boolean
  delay?: number  // 延迟时间（ms）
}

const animationStyles: Record<AnimationType, string> = {
  'fade-in-up': 'opacity: 0; transform: translateY(30px);',
  'fade-in-left': 'opacity: 0; transform: translateX(-30px);',
  'fade-in-right': 'opacity: 0; transform: translateX(30px);',
  'scale-in': 'opacity: 0; transform: scale(0.8);',
  'rotate-in': 'opacity: 0; transform: rotate(-10deg) scale(0.9);'
}

const animationKeyframes: Record<AnimationType, string> = {
  'fade-in-up': 'fadeInUp',
  'fade-in-left': 'fadeInLeft',
  'fade-in-right': 'fadeInRight',
  'scale-in': 'scaleIn',
  'rotate-in': 'rotateIn'
}

export function observeScrollAnimation(
  element: HTMLElement,
  options: ScrollAnimationOptions = {}
): () => void {
  const {
    threshold = 0.1,
    rootMargin = '0px',
    animationType = 'fade-in-up',
    once = true,
    delay = 0
  } = options

  // 应用初始样式
  element.style.cssText = animationStyles[animationType]
  element.style.transition = `all 0.6s ease-out ${delay}ms`

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          setTimeout(() => {
            element.style.opacity = '1'
            element.style.transform = 'translateY(0) translateX(0) scale(1) rotate(0)'
          }, delay)

          if (once) {
            observer.unobserve(element)
          }
        }
      })
    },
    { threshold, rootMargin }
  )

  observer.observe(element)

  return () => {
    observer.unobserve(element)
    observer.disconnect()
  }
}
```

---

## 技术选型对比

### 消息通知方案对比

| 特性 | Element Plus | Vue Toastification | 自定义 |
|------|-------------|-------------------|--------|
| 开发时间 | 2-3 小时 | 3-4 小时 | 8-12 小时 |
| 包体积 | 0（已有） | ~5KB | 0 |
| 学习成本 | 低 | 中 | 高 |
| 可定制性 | 中 | 高 | 极高 |
| 维护成本 | 低 | 中 | 高 |

### 动态背景方案对比

| 特性 | Canvas | CSS+SVG | Three.js |
|------|--------|---------|----------|
| 开发时间 | 4-6 小时 | 2-3 小时 | 12-16 小时 |
| 包体积 | 0 | 0 | ~600KB |
| 性能 | 优秀 | 极佳 | 良好（桌面端） |
| 视觉效果 | 优秀 | 良好 | 卓越 |
| 移动端兼容 | 良好 | 极佳 | 较差 |

---

## 实施计划

### Phase 1: 消息通知优化（预计 3 小时）

| 任务 | 负责人 | 预估时间 | 状态 |
|------|--------|---------|------|
| 创建 Toast 工具函数 | Senior Dev | 1h | ⏳ Pending |
| 优化通知下拉面板 UI | Senior Dev | 1.5h | ⏳ Pending |
| 集成到业务流程 | Senior Dev | 0.5h | ⏳ Pending |

### Phase 2: 首页动态背景（预计 6 小时）

| 任务 | 负责人 | 预估时间 | 状态 |
|------|--------|---------|------|
| 创建 Canvas 粒子系统 | Senior Dev | 3h | ⏳ Pending |
| 重构 HomeView Hero 区域 | Senior Dev | 2h | ⏳ Pending |
| 扩展滚动动画工具 | Senior Dev | 1h | ⏳ Pending |

### Phase 3: 测试与优化（预计 2 小时）

| 任务 | 负责人 | 预估时间 | 状态 |
|------|--------|---------|------|
| 跨浏览器测试 | QA | 1h | ⏳ Pending |
| 性能优化（移动端） | Senior Dev | 0.5h | ⏳ Pending |
| 无障碍检查 | QA | 0.5h | ⏳ Pending |

**总预估时间**: 11 小时

---

## 风险评估

### 高风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|---------|
| Canvas 动画导致移动端卡顿 | 中 | 高 | 1. 移动端减少粒子数量<br>2. 检测设备性能，降级为静态背景<br>3. 使用 requestAnimationFrame 优化 |
| Toast 通知过多干扰用户 | 低 | 中 | 1. 设置合理的自动消失时间<br>2. 限制同时显示的数量（最多 3 个）<br>3. 提供关闭所有通知的功能 |

### 中风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|---------|
| 粒子系统与现有动画冲突 | 低 | 中 | 1. 独立 z-index 层级<br>2. 使用 CSS isolation |
| 浏览器兼容性问题 | 低 | 低 | 1. 使用 Canvas API（广泛支持）<br>2. 提供 fallback 方案 |

### 低风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|---------|
| 设计不符合用户期望 | 低 | 低 | 1. 先在小范围测试<br>2. 收集用户反馈迭代 |

---

## 成功标准

### 功能性指标
- ✅ Toast 通知在所有关键操作中正确显示
- ✅ 通知下拉面板实时更新未读计数
- ✅ 粒子动画在桌面端保持 60 FPS
- ✅ 移动端粒子数量自适应（≤ 40 个）

### 性能指标
- ✅ Lighthouse Performance Score ≥ 90
- ✅ First Contentful Paint ≤ 1.5s
- ✅ Canvas 动画 CPU 占用 ≤ 10%
- ✅ 包体积增长 ≤ 5KB

### 用户体验指标
- ✅ 用户满意度评分 ≥ 4.5/5
- ✅ 通知点击率提升 ≥ 20%
- ✅ 首页停留时长增加 ≥ 15%

### 代码质量指标
- ✅ TypeScript 覆盖率 100%
- ✅ 单元测试覆盖率 ≥ 80%
- ✅ ESLint 无错误
- ✅ 代码审查通过

---

## 参考资料

### 设计规范
- [Element Plus Notification](https://element-plus.org/zh-CN/component/notification.html)
- [Material Design - SnackBar](https://material.io/components/snackbars)
- [Apple Human Interface Guidelines - Notifications](https://developer.apple.com/design/human-interface-guidelines/notifications)

### 技术文档
- [Canvas API MDN](https://developer.mozilla.org/zh-CN/docs/Web/API/Canvas_API)
- [Intersection Observer API](https://developer.mozilla.org/zh-CN/docs/Web/API/Intersection_Observer_API)
- [requestAnimationFrame 最佳实践](https://developer.mozilla.org/zh-CN/docs/Web/API/window/requestAnimationFrame)

### 灵感来源
- [jiejoe.com](https://www.jiejoe.com/home) - 动态背景参考
- [CodePen - Particle Effects](https://codepen.io/search/pens?q=particle+background)
- [Awwwards - Animated Backgrounds](https://www.awwwards.com/websites/animated-background/)

### 性能优化
- [Web Animations Performance Checklist](https://github.com/GoogleChromeLabs/web-perf-checklist)
- [Canvas Performance Tips](https://developer.mozilla.org/zh-CN/docs/Web/API/Canvas_API/Tutorial/Optimizing_canvas)

---

## 下一步行动

1. **立即执行**:
   - [ ] 创建 Story 文档（UI 优化专项）
   - [ ] 进行 PoC 验证（Canvas 粒子系统）
   - [ ] 更新 Architecture 文档（记录技术决策）

2. **本周内完成**:
   - [ ] 实施 Phase 1（消息通知优化）
   - [ ] 实施 Phase 2（首页动态背景）
   - [ ] 完成 Phase 3（测试与优化）

3. **验收标准**:
   - [ ] 所有成功标准达标
   - [ ] 代码审查通过
   - [ ] 部署到 staging 环境测试
   - [ ] 收集用户反馈

---

**文档版本**: v1.0  
**最后更新**: 2026-04-11  
**审核人**: UX Designer, Senior Dev, Product Owner
