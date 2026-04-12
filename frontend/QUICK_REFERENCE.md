# 前端开发快速参考

> 基于重构规范的速查表 | 完整文档见 `REFACTORING_SUMMARY.md`

---

## 🎨 设计 Token 速查

### 颜色
```css
/* 主色 */
var(--color-primary)          /* #3b82f6 - 主要操作 */
var(--color-primary-hover)    /* #2563eb - Hover 状态 */

/* 角色 */
var(--color-role-probation)   /* #9ca3af - 预备成员 */
var(--color-role-member)      /* #3b82f6 - 正式成员 */
var(--color-role-admin)       /* #f59e0b - 管理员 */

/* 状态 */
var(--color-status-published) /* #10b981 - 成功/已发布 */
var(--color-status-rejected)  /* #ef4444 - 失败/已拒绝 */

/* 文本 */
var(--color-text-primary)     /* #111827 - 主文本 */
var(--color-text-secondary)   /* #4b5563 - 次要文本 */
var(--color-text-tertiary)    /* #9ca3af - 辅助文本 */

/* 背景 */
var(--color-bg-primary)       /* #ffffff - 主背景 */
var(--color-bg-secondary)     /* #f9fafb - 次背景 */
var(--color-bg-tertiary)      /* #f3f4f6 - 第三背景 */
```

### 间距
```css
var(--spacing-xs)   /* 4px */
var(--spacing-sm)   /* 8px */
var(--spacing-md)   /* 16px */
var(--spacing-lg)   /* 24px */
var(--spacing-xl)   /* 32px */
var(--spacing-2xl)  /* 48px */
```

### 字体
```css
/* 大小 */
var(--font-size-sm)    /* 14px */
var(--font-size-base)  /* 16px */
var(--font-size-lg)    /* 18px */
var(--font-size-xl)    /* 20px */
var(--font-size-2xl)   /* 24px */
var(--font-size-3xl)   /* 30px */

/* 字重 */
var(--font-weight-normal)     /* 400 */
var(--font-weight-medium)     /* 500 */
var(--font-weight-semibold)   /* 600 */
var(--font-weight-bold)       /* 700 */
```

### 圆角
```css
var(--radius-sm)    /* 4px */
var(--radius-md)    /* 6px */
var(--radius-lg)    /* 8px */
var(--radius-xl)    /* 12px */
var(--radius-full)  /* 圆形 */
```

### 容器宽度
```css
var(--max-width-content)  /* 896px - 内容区域 */
var(--max-width-wide)     /* 1280px - 宽屏 */
```

---

## 🧩 组件使用

### AppButton
```vue
<!-- 变体 -->
<AppButton variant="primary">主要</AppButton>
<AppButton variant="success">成功</AppButton>
<AppButton variant="outline">描边</AppButton>
<AppButton variant="danger">危险</AppButton>

<!-- 尺寸 -->
<AppButton size="sm">小</AppButton>
<AppButton size="md">中</AppButton>
<AppButton size="lg">大</AppButton>
<AppButton size="xl">超大</AppButton>

<!-- 图标 -->
<AppButton variant="primary">
  <el-icon><Plus /></el-icon>
  添加
</AppButton>

<!-- 加载 -->
<AppButton :loading="true">提交中...</AppButton>
```

### 徽章
```vue
<!-- 类型 -->
<div class="badge badge-info">信息</div>
<div class="badge badge-primary">主要</div>
<div class="badge badge-success">成功</div>
<div class="badge badge-warning">警告</div>
<div class="badge badge-danger">危险</div>

<!-- 辅助函数示例 -->
<script setup lang="ts">
function getRoleBadgeClass(role: string): string {
  const map: Record<string, string> = {
    ROLE_PROBATION: 'badge-info',
    ROLE_MEMBER: 'badge-primary',
    ROLE_ADMIN: 'badge-warning',
  }
  return map[role] || 'badge-info'
}
</script>

<template>
  <div class="badge" :class="getRoleBadgeClass(row.role)">
    {{ getRoleLabel(row.role) }}
  </div>
</template>
```

---

## 📝 页面模板

### 标准页面结构
```vue
<template>
  <div class="page-container">
    <!-- 头部 -->
    <div class="page-header animate-fade-in-up">
      <h1 class="page-title">标题</h1>
      <AppButton variant="primary" @click="handleAction">
        操作
      </AppButton>
    </div>

    <!-- 内容 -->
    <el-card shadow="never" class="animate-fade-in-up">
      内容
    </el-card>
  </div>
</template>

<script setup lang="ts">
import AppButton from '@/components/common/AppButton.vue'
</script>

<style scoped>
.page-container {
  max-width: var(--max-width-content);
  margin: 0 auto;
  padding: var(--spacing-2xl) var(--spacing-base);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.page-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

@media (max-width: 768px) {
  .page-container {
    padding: var(--spacing-xl) var(--spacing-sm);
  }
  .page-title {
    font-size: var(--font-size-2xl);
  }
}
</style>
```

---

## 🎭 Element Plus 深度定制

### 表格
```css
:deep(.el-table) {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

:deep(.el-table th.el-table__cell) {
  background-color: var(--color-bg-tertiary);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

:deep(.el-table tbody tr:hover > td) {
  background-color: var(--color-primary-light) !important;
}
```

### 对话框
```css
:deep(.el-dialog__header) {
  border-bottom: 1px solid var(--color-neutral-200);
  padding-bottom: var(--spacing-md);
}

:deep(.el-dialog__title) {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

:deep(.el-dialog__body) {
  padding: var(--spacing-xl);
}
```

### 描述列表
```css
:deep(.el-descriptions__label) {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-secondary);
}

:deep(.el-descriptions__content) {
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}
```

### 步骤条
```css
:deep(.el-step__title) {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

:deep(.el-step__description) {
  color: var(--color-text-tertiary);
  font-size: var(--font-size-sm);
}
```

### 结果组件
```css
:deep(.el-result__title) {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

:deep(.el-result__subtitle) {
  color: var(--color-text-secondary);
  font-size: var(--font-size-base);
}
```

### Card Header
```css
.card-header :deep(span) {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}
```

---

## ✨ 动画类

### 淡入上移
```vue
<div class="animate-fade-in-up">内容</div>
```

### 滚动渐入
```vue
<div class="scroll-reveal">内容</div>
```

```typescript
// 需要在 mounted 时添加 IntersectionObserver
onMounted(() => {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible')
      }
    })
  })
  
  document.querySelectorAll('.scroll-reveal').forEach(el => {
    observer.observe(el)
  })
})
```

---

## ✅ 代码审查清单

提交前必须检查：

- [ ] 使用 `AppButton` 而非 `el-button`
- [ ] 使用自定义徽章而非 `el-tag`
- [ ] 所有颜色使用 CSS 变量
- [ ] 所有间距使用 CSS 变量
- [ ] 所有字体大小使用 CSS 变量
- [ ] 添加 `animate-fade-in-up` 动画类
- [ ] 实现响应式设计（@media ≤768px）
- [ ] TypeScript 类型定义完整
- [ ] 无编译错误
- [ ] 热更新正常

---

## 🚫 禁止事项

### ❌ 不要这样做

```vue
<!-- 禁止：直接使用 el-button -->
<el-button type="primary">按钮</el-button>

<!-- 禁止：使用 el-tag -->
<el-tag type="primary">标签</el-tag>

<!-- 禁止：硬编码颜色 -->
<style>
.title { color: #333; }
</style>

<!-- 禁止：硬编码间距 -->
<style>
.container { padding: 20px; }
</style>

<!-- 禁止：硬编码字体大小 -->
<style>
.text { font-size: 16px; }
</style>
```

### ✅ 应该这样做

```vue
<!-- 正确：使用 AppButton -->
<AppButton variant="primary">按钮</AppButton>

<!-- 正确：使用自定义徽章 -->
<div class="badge badge-primary">标签</div>

<!-- 正确：使用 CSS 变量 -->
<style>
.title { color: var(--color-text-primary); }
.container { padding: var(--spacing-xl); }
.text { font-size: var(--font-size-base); }
</style>
```

---

## 📚 常用模式

### 搜索过滤栏
```vue
<div class="filter-bar">
  <el-input
    v-model="searchKeyword"
    placeholder="搜索..."
    clearable
    style="width: 300px"
  >
    <template #prefix>
      <el-icon><Search /></el-icon>
    </template>
  </el-input>
  <AppButton variant="primary" @click="handleSearch">
    <el-icon><Search /></el-icon>
    搜索
  </AppButton>
</div>

<style scoped>
.filter-bar {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}
</style>
```

### 分页器
```vue
<div class="pagination-container">
  <el-pagination
    v-model:current-page="currentPage"
    v-model:page-size="pageSize"
    :total="total"
    layout="total, prev, pager, next"
  />
</div>

<style scoped>
.pagination-container {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
}
</style>
```

### 表单提交
```vue
<el-form ref="formRef" :model="form" :rules="rules">
  <el-form-item label="名称" prop="name">
    <el-input v-model="form.name" />
  </el-form-item>
  
  <el-form-item>
    <AppButton variant="primary" size="lg" @click="handleSubmit">
      提交
    </AppButton>
    <AppButton variant="outline" size="lg" @click="handleCancel">
      取消
    </AppButton>
  </el-form-item>
</el-form>
```

---

## 🔗 相关链接

- **完整规范**: `REFACTORING_SUMMARY.md`
- **设计 Token**: `/src/assets/design-tokens.css`
- **AppButton**: `/src/components/common/AppButton.vue`
- **全局样式**: `/src/assets/main.css`

---

**提示**: 将此文件加入书签，开发时随时查阅！
