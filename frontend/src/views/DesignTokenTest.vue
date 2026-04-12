<script setup lang="ts">
// 设计 Token 测试页面
import { ref } from 'vue'

const colors = [
  { name: 'Primary 500', class: 'bg-primary-500', var: '--color-primary-500' },
  { name: 'Role - Probation', class: 'bg-role-probation', var: '--color-role-probation' },
  { name: 'Role - Member', class: 'bg-role-member', var: '--color-role-member' },
  { name: 'Role - Admin', class: 'bg-role-admin', var: '--color-role-admin' },
  { name: 'Status - Draft', class: 'bg-status-draft', var: '--color-status-draft' },
  { name: 'Status - Pending', class: 'bg-status-pending', var: '--color-status-pending' },
  { name: 'Status - Published', class: 'bg-status-published', var: '--color-status-published' },
]

const spacings = [
  { name: 'xs (4px)', value: 'var(--spacing-xs)' },
  { name: 'sm (8px)', value: 'var(--spacing-sm)' },
  { name: 'md (12px)', value: 'var(--spacing-md)' },
  { name: 'base (16px)', value: 'var(--spacing-base)' },
  { name: 'lg (24px)', value: 'var(--spacing-lg)' },
  { name: 'xl (32px)', value: 'var(--spacing-xl)' },
]

const radii = [
  { name: 'sm (4px)', value: 'var(--radius-sm)' },
  { name: 'md (8px)', value: 'var(--radius-md)' },
  { name: 'lg (12px)', value: 'var(--radius-lg)' },
  { name: 'xl (16px)', value: 'var(--radius-xl)' },
  { name: '2xl (32px)', value: 'var(--radius-2xl)' },
]
</script>

<template>
  <div class="min-h-screen bg-neutral-50 p-8">
    <div class="max-w-content mx-auto">
      <!-- 标题 -->
      <h1 class="text-4xl font-bold text-text-primary mb-8 animate-fade-in">
        设计 Token 测试页面
      </h1>

      <!-- 颜色系统 -->
      <section class="mb-12 animate-slide-up">
        <h2 class="text-2xl font-semibold text-text-primary mb-4">🎨 颜色系统</h2>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div
            v-for="color in colors"
            :key="color.name"
            class="p-4 rounded-lg shadow-card hover:shadow-card-hover transition-shadow duration-200"
          >
            <div
              :class="[color.class, 'h-16 rounded-md mb-2']"
            ></div>
            <p class="text-sm font-medium text-text-primary">{{ color.name }}</p>
            <p class="text-xs text-text-secondary font-mono">{{ color.var }}</p>
          </div>
        </div>
      </section>

      <!-- 间距系统 -->
      <section class="mb-12 animate-slide-up" style="animation-delay: 100ms">
        <h2 class="text-2xl font-semibold text-text-primary mb-4">📏 间距系统</h2>
        <div class="space-y-3">
          <div
            v-for="spacing in spacings"
            :key="spacing.name"
            class="flex items-center gap-4"
          >
            <span class="text-sm text-text-secondary w-32">{{ spacing.name }}</span>
            <div
              class="bg-primary-500 rounded"
              :style="{ width: spacing.value, height: '24px' }"
            ></div>
          </div>
        </div>
      </section>

      <!-- 圆角系统 -->
      <section class="mb-12 animate-slide-up" style="animation-delay: 200ms">
        <h2 class="text-2xl font-semibold text-text-primary mb-4">🔲 圆角系统</h2>
        <div class="flex flex-wrap gap-4">
          <div
            v-for="radius in radii"
            :key="radius.name"
            class="text-center"
          >
            <div
              class="w-24 h-24 bg-primary-500 mb-2"
              :style="{ borderRadius: radius.value }"
            ></div>
            <p class="text-sm text-text-secondary">{{ radius.name }}</p>
          </div>
        </div>
      </section>

      <!-- 阴影系统 -->
      <section class="mb-12 animate-slide-up" style="animation-delay: 300ms">
        <h2 class="text-2xl font-semibold text-text-primary mb-4">💫 阴影系统</h2>
        <div class="grid grid-cols-2 gap-6">
          <div class="p-6 bg-surface rounded-lg shadow-card">
            <p class="font-medium text-text-primary">Shadow Card</p>
            <p class="text-sm text-text-secondary mt-1">默认卡片阴影</p>
          </div>
          <div class="p-6 bg-surface rounded-lg shadow-card-hover">
            <p class="font-medium text-text-primary">Shadow Card Hover</p>
            <p class="text-sm text-text-secondary mt-1">悬停卡片阴影</p>
          </div>
        </div>
      </section>

      <!-- 字体系统 -->
      <section class="mb-12 animate-slide-up" style="animation-delay: 400ms">
        <h2 class="text-2xl font-semibold text-text-primary mb-4">✍️ 字体系统</h2>
        <div class="space-y-4">
          <div>
            <h1 class="text-5xl font-bold text-text-primary">Heading 1 (48px)</h1>
          </div>
          <div>
            <h2 class="text-4xl font-bold text-text-primary">Heading 2 (36px)</h2>
          </div>
          <div>
            <h3 class="text-3xl font-semibold text-text-primary">Heading 3 (30px)</h3>
          </div>
          <div>
            <h4 class="text-2xl font-semibold text-text-primary">Heading 4 (24px)</h4>
          </div>
          <div>
            <p class="text-xl text-text-primary">Large Text (20px)</p>
          </div>
          <div>
            <p class="text-base text-text-primary">Base Text (16px) - 正文默认大小</p>
          </div>
          <div>
            <p class="text-sm text-text-secondary">Small Text (14px) - 辅助文字</p>
          </div>
          <div>
            <p class="text-xs text-text-tertiary">Extra Small Text (12px) - 微小文字</p>
          </div>
        </div>
      </section>

      <!-- 动画演示 -->
      <section class="mb-12 animate-slide-up" style="animation-delay: 500ms">
        <h2 class="text-2xl font-semibold text-text-primary mb-4">🎬 动画演示</h2>
        <div class="grid grid-cols-3 gap-4">
          <div class="p-6 bg-surface rounded-lg shadow-card animate-fade-in">
            <p class="font-medium text-text-primary">Fade In</p>
            <p class="text-sm text-text-secondary mt-1">淡入效果</p>
          </div>
          <div class="p-6 bg-surface rounded-lg shadow-card animate-slide-up">
            <p class="font-medium text-text-primary">Slide Up</p>
            <p class="text-sm text-text-secondary mt-1">上滑淡入</p>
          </div>
          <div class="p-6 bg-surface rounded-lg shadow-card animate-scale-in">
            <p class="font-medium text-text-primary">Scale In</p>
            <p class="text-sm text-text-secondary mt-1">缩放淡入</p>
          </div>
        </div>
      </section>

      <!-- 角色徽章示例 -->
      <section class="mb-12 animate-slide-up" style="animation-delay: 600ms">
        <h2 class="text-2xl font-semibold text-text-primary mb-4">👥 角色徽章示例</h2>
        <div class="flex gap-4">
          <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-role-probation/10 text-role-probation">
            预备成员
          </span>
          <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-role-member/10 text-role-member">
            正式成员
          </span>
          <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-role-admin/10 text-role-admin">
            负责人
          </span>
        </div>
      </section>

      <!-- 内容状态标签示例 -->
      <section class="mb-12 animate-slide-up" style="animation-delay: 700ms">
        <h2 class="text-2xl font-semibold text-text-primary mb-4">📝 内容状态标签示例</h2>
        <div class="flex gap-4">
          <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-status-draft/10 text-status-draft">
            草稿
          </span>
          <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-status-pending/10 text-status-pending">
            待审核
          </span>
          <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-status-published/10 text-status-published">
            已发布
          </span>
          <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-status-rejected/10 text-status-rejected">
            已拒绝
          </span>
        </div>
      </section>

      <!-- 按钮示例 -->
      <section class="mb-12 animate-slide-up" style="animation-delay: 800ms">
        <h2 class="text-2xl font-semibold text-text-primary mb-4">🔘 按钮示例</h2>
        <div class="flex gap-4">
          <button class="px-4 py-2 bg-primary-500 text-white rounded-lg shadow-card hover:shadow-card-hover hover:bg-primary-600 transition-all duration-200">
            Primary Button
          </button>
          <button class="px-4 py-2 bg-surface text-text-primary border border-border rounded-lg shadow-card hover:shadow-card-hover transition-all duration-200">
            Secondary Button
          </button>
          <button class="px-4 py-2 bg-role-member text-white rounded-lg shadow-card hover:shadow-card-hover hover:bg-role-member/90 transition-all duration-200">
            Member Button
          </button>
        </div>
      </section>

      <!-- 卡片示例 -->
      <section class="mb-12 animate-slide-up" style="animation-delay: 900ms">
        <h2 class="text-2xl font-semibold text-text-primary mb-4">🃏 卡片示例</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="bg-surface rounded-xl shadow-card hover:shadow-card-hover transition-shadow duration-200 overflow-hidden">
            <div class="h-48 bg-gradient-to-br from-primary-400 to-primary-600"></div>
            <div class="p-6">
              <h3 class="text-xl font-semibold text-text-primary mb-2">Card Title</h3>
              <p class="text-text-secondary line-clamp-2">
                This is a card component with modern design tokens. It uses the new design system with proper spacing, shadows, and typography.
              </p>
              <div class="mt-4 flex items-center justify-between">
                <span class="text-sm text-text-tertiary">2026-04-08</span>
                <button class="text-primary-500 hover:text-primary-600 font-medium text-sm transition-colors duration-200">
                  Read More →
                </button>
              </div>
            </div>
          </div>

          <div class="bg-surface rounded-xl shadow-card hover:shadow-card-hover transition-shadow duration-200 p-6">
            <div class="flex items-start gap-4">
              <div class="w-12 h-12 rounded-full bg-primary-100 flex items-center justify-center">
                <span class="text-primary-600 font-bold text-lg">U</span>
              </div>
              <div class="flex-1">
                <h3 class="text-lg font-semibold text-text-primary mb-1">User Card</h3>
                <p class="text-sm text-text-secondary mb-2">
                  A user profile card showing how to use the design tokens for avatars and user information.
                </p>
                <div class="flex gap-2">
                  <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-role-member/10 text-role-member">
                    正式成员
                  </span>
                  <span class="text-xs text-text-tertiary">Joined 2024</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 使用说明 -->
      <section class="animate-slide-up" style="animation-delay: 1000ms">
        <h2 class="text-2xl font-semibold text-text-primary mb-4">📚 使用指南</h2>
        <div class="bg-surface rounded-xl shadow-card p-6">
          <p class="text-text-secondary mb-4">
            设计 Token 系统已经成功配置！您可以开始使用这些 Token 来重构前端页面。
          </p>
          <ul class="space-y-2 text-text-secondary">
            <li class="flex items-start gap-2">
              <span class="text-primary-500 mt-1">✓</span>
              <span><strong>TailwindCSS 配置：</strong>在 tailwind.config.js 中定义了自定义颜色、间距、圆角、阴影和动画</span>
            </li>
            <li class="flex items-start gap-2">
              <span class="text-primary-500 mt-1">✓</span>
              <span><strong>CSS 变量：</strong>在 design-tokens.css 中定义了完整的 CSS 变量系统</span>
            </li>
            <li class="flex items-start gap-2">
              <span class="text-primary-500 mt-1">✓</span>
              <span><strong>使用文档：</strong>查看 frontend/docs/design-tokens-guide.md 了解详细用法</span>
            </li>
          </ul>
          <div class="mt-6 p-4 bg-primary-50 rounded-lg border border-primary-200">
            <p class="text-sm text-primary-800">
              <strong>下一步：</strong>开始重构首页（HomeView.vue），应用新的设计 Token 系统。
            </p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
/* 所有样式都使用 TailwindCSS 类，不需要额外的 CSS */
</style>
