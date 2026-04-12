# 设计 Token 使用指南

## 📋 概述

本文档说明如何在项目中使用设计 Token 系统。所有样式都应基于设计 Token，确保一致性和可维护性。

---

## 🎨 颜色系统

### 主色调（技术蓝）

```vue
<!-- TailwindCSS 类名 -->
<div class="bg-primary-500 text-white">主色背景</div>
<div class="text-primary-600">深色文字</div>
<div class="border-primary-300">浅色边框</div>

<!-- CSS 变量 -->
<style>
.element {
  background-color: var(--color-primary-500);
  color: var(--color-primary-600);
}
</style>
```

**可用色阶：** 50, 100, 200, 300, 400, 500 (主色), 600, 700, 800, 900

---

### 角色颜色

```vue
<!-- 预备成员 -->
<span class="text-role-probation bg-role-probation/10 px-2 py-1 rounded-full">
  预备成员
</span>

<!-- 正式成员 -->
<span class="text-role-member bg-role-member/10 px-2 py-1 rounded-full">
  正式成员
</span>

<!-- 负责人 -->
<span class="text-role-admin bg-role-admin/10 px-2 py-1 rounded-full">
  负责人
</span>
```

**CSS 变量：**
- `--color-role-probation`: #9ca3af (灰色)
- `--color-role-member`: #3b82f6 (蓝色)
- `--color-role-admin`: #f59e0b (金色)

---

### 内容状态颜色

```vue
<!-- 草稿 -->
<el-tag type="info">草稿</el-tag>

<!-- 待审核 -->
<el-tag type="warning">待审核</el-tag>

<!-- 已发布 -->
<el-tag type="success">已发布</el-tag>

<!-- 已拒绝 -->
<el-tag type="danger">已拒绝</el-tag>
```

**CSS 变量：**
- `--color-status-draft`: #9ca3af
- `--color-status-pending`: #f59e0b
- `--color-status-published`: #10b981
- `--color-status-rejected`: #ef4444

---

### 中性色

```vue
<!-- 背景色 -->
<div class="bg-neutral-50">浅灰背景</div>
<div class="bg-surface">白色表面</div>

<!-- 文字颜色 -->
<p class="text-text-primary">主要文字</p>
<p class="text-text-secondary">次要文字</p>
<p class="text-text-tertiary">辅助文字</p>

<!-- 边框 -->
<div class="border border-border">带边框的元素</div>
```

---

## 📏 间距系统

基于 4px 基数，使用 TailwindCSS 的 spacing 或 CSS 变量：

```vue
<!-- TailwindCSS -->
<div class="p-4 m-2 gap-3">
  <!-- p-4 = 16px, m-2 = 8px, gap-3 = 12px -->
</div>

<!-- CSS 变量 -->
<style>
.container {
  padding: var(--spacing-base);    /* 16px */
  margin: var(--spacing-sm);       /* 8px */
  gap: var(--spacing-md);          /* 12px */
}
</style>
```

**可用间距：**
- `xs`: 4px
- `sm`: 8px
- `md`: 12px
- `base`: 16px
- `lg`: 24px
- `xl`: 32px
- `2xl`: 48px
- `3xl`: 64px

---

## 🔲 圆角系统

```vue
<!-- TailwindCSS -->
<div class="rounded-md">中等圆角</div>
<div class="rounded-xl">大圆角</div>
<div class="rounded-full">完全圆形</div>

<!-- CSS 变量 -->
<style>
.card {
  border-radius: var(--radius-lg);  /* 12px */
}
.button {
  border-radius: var(--radius-md);  /* 8px */
}
</style>
```

**可用圆角：**
- `sm`: 4px
- `md`: 8px
- `lg`: 12px
- `xl`: 16px
- `2xl`: 32px
- `full`: 9999px (圆形)

---

## 💫 阴影系统

```vue
<!-- TailwindCSS -->
<div class="shadow-card">卡片阴影</div>
<div class="hover:shadow-card-hover">悬停时加深阴影</div>

<!-- CSS 变量 -->
<style>
.card {
  box-shadow: var(--shadow-card);
  transition: box-shadow var(--transition-base);
}
.card:hover {
  box-shadow: var(--shadow-card-hover);
}
</style>
```

**可用阴影：**
- `card`: 默认卡片阴影
- `card-hover`: 悬停时的加深阴影
- `nav`: 导航栏阴影
- `nav-scrolled`: 滚动后的导航栏阴影

---

## ✍️ 字体系统

### 字体大小

```vue
<!-- TailwindCSS -->
<h1 class="text-4xl font-bold">大标题</h1>
<h2 class="text-2xl font-semibold">副标题</h2>
<p class="text-base">正文</p>
<p class="text-sm text-text-secondary">辅助文字</p>

<!-- CSS 变量 -->
<style>
.title {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
}
.subtitle {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-semibold);
}
</style>
```

**可用字号：**
- `xs`: 12px
- `sm`: 14px
- `base`: 16px
- `lg`: 18px
- `xl`: 20px
- `2xl`: 24px
- `3xl`: 30px
- `4xl`: 36px
- `5xl`: 48px

### 字重

- `normal`: 400
- `medium`: 500
- `semibold`: 600
- `bold`: 700

### 行高

- `tight`: 1.25
- `normal`: 1.5
- `relaxed`: 1.75

---

## 🎬 动画系统

### 过渡时长

```vue
<!-- TailwindCSS -->
<button class="transition duration-200 hover:bg-primary-600">
  按钮
</button>

<!-- CSS 变量 -->
<style>
.element {
  transition: all var(--transition-base);
}
</style>
```

**可用过渡：**
- `fast`: 150ms
- `base`: 200ms
- `slow`: 300ms
- `slower`: 500ms

### 预定义动画

```vue
<!-- TailwindCSS 动画类 -->
<div class="animate-fade-in">淡入</div>
<div class="animate-slide-up">上滑淡入</div>
<div class="animate-scale-in">缩放淡入</div>

<!-- 自定义动画示例 -->
<style>
@keyframes customAnimation {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.animated-element {
  animation: customAnimation 300ms ease-out;
}
</style>
```

**可用动画：**
- `fade-in`: 淡入效果
- `slide-up`: 上滑淡入
- `scale-in`: 缩放淡入

---

## 📐 布局系统

### 内容区域最大宽度

```vue
<!-- TailwindCSS -->
<main class="max-w-content mx-auto px-4">
  <!-- 内容区域，最大宽度 896px，居中 -->
</main>

<!-- CSS 变量 -->
<style>
.container {
  max-width: var(--max-width-content);
  margin: 0 auto;
}
</style>
```

**可用宽度：**
- `content`: 896px (max-w-4xl) - 用于主要内容区域
- `wide`: 1024px (max-w-5xl) - 用于宽屏布局

---

## 🛠️ 实用工具类

### 文本截断

```vue
<!-- 单行截断 -->
<p class="truncate-1">这是一段很长的文字...</p>

<!-- 两行截断 -->
<p class="truncate-2">这是一段很长的文字，超过两行会被截断...</p>

<!-- 三行截断 -->
<p class="truncate-3">这是一段很长的文字，超过三行会被截断...</p>
```

### 滚动条美化

滚动条已自动美化，无需额外配置。

---

## 📝 最佳实践

### ✅ 推荐做法

1. **始终使用设计 Token**
   ```vue
   <!-- ✅ 好 -->
   <div class="bg-primary-500 text-white rounded-lg shadow-card">
   
   <!-- ❌ 不好 -->
   <div style="background: #3b82f6; color: white; border-radius: 8px;">
   ```

2. **使用语义化类名**
   ```vue
   <!-- ✅ 好 -->
   <span class="badge-role-member">正式成员</span>
   
   <!-- ❌ 不好 -->
   <span class="text-blue-500 bg-blue-50">正式成员</span>
   ```

3. **保持一致的间距**
   ```vue
   <!-- ✅ 好 - 使用统一的间距系统 -->
   <div class="p-4 space-y-3">
   
   <!-- ❌ 不好 - 随意使用间距 -->
   <div class="p-[17px] space-y-[11px]">
   ```

4. **使用预定义动画**
   ```vue
   <!-- ✅ 好 -->
   <div class="animate-fade-in">
   
   <!-- ❌ 不好 - 自定义复杂动画 -->
   <div style="animation: customFadeIn 0.3s ease-out;">
   ```

### ❌ 避免的做法

1. **不要硬编码颜色和尺寸**
2. **不要混合使用多种间距系统**
3. **不要在组件中定义全局样式**
4. **不要忽略响应式设计**

---

## 🎯 组件开发指南

### 创建新组件时

1. **使用设计 Token 定义样式**
   ```vue
   <template>
     <div class="card">
       <h3 class="card-title">{{ title }}</h3>
       <p class="card-content">{{ content }}</p>
     </div>
   </template>

   <style scoped>
   .card {
     background: var(--color-surface);
     border-radius: var(--radius-lg);
     box-shadow: var(--shadow-card);
     padding: var(--spacing-lg);
     transition: box-shadow var(--transition-base);
   }
   
   .card:hover {
     box-shadow: var(--shadow-card-hover);
   }
   
   .card-title {
     font-size: var(--font-size-xl);
     font-weight: var(--font-weight-semibold);
     color: var(--color-text-primary);
     margin-bottom: var(--spacing-sm);
   }
   
   .card-content {
     font-size: var(--font-size-base);
     color: var(--color-text-secondary);
     line-height: var(--line-height-relaxed);
   }
   </style>
   ```

2. **支持主题定制**
   ```vue
   <style scoped>
   .component {
     /* 使用 CSS 变量，方便后续主题切换 */
     background: var(--color-surface);
     color: var(--color-text-primary);
   }
   </style>
   ```

---

## 🔄 迁移旧代码

### 从硬编码样式迁移到设计 Token

**之前：**
```vue
<style>
.card {
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 16px;
}
</style>
```

**之后：**
```vue
<style>
.card {
  background: var(--color-surface);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-card);
  padding: var(--spacing-base);
}
</style>
```

---

## 📚 参考资源

- [TailwindCSS 官方文档](https://tailwindcss.com/docs)
- [Element Plus 组件库](https://element-plus.org/)
- [UX 设计规范文档](../../_bmad-output/planning-artifacts/ux-design-specification.md)

---

## 🆘 常见问题

### Q: 什么时候使用 TailwindCSS，什么时候使用 CSS 变量？

**A:** 
- **TailwindCSS**: 快速原型、简单样式、响应式布局
- **CSS 变量**: 复杂组件、需要主题定制、复用性高的样式

### Q: 如何添加新的颜色？

**A:** 
1. 在 `tailwind.config.js` 的 `colors` 中添加
2. 在 `design-tokens.css` 的 `:root` 中添加对应的 CSS 变量
3. 更新本文档

### Q: 设计 Token 可以覆盖吗？

**A:** 
不建议直接覆盖。如果需要特殊样式，应该：
1. 评估是否真的需要
2. 考虑是否可以扩展现有 Token
3. 如果必须，在组件级别使用内联样式或 scoped 样式

---

**最后更新：** 2026-04-08  
**维护者：** 前端开发团队
