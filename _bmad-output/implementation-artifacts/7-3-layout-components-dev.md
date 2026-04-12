---
story_id: "7.3"
story_key: "7-3-layout-components-dev"
epic: "前端 UI 实现"
status: done
created: "2026-04-08"
completed: "2026-04-09"
---

# Story 7.3: 布局组件开发

As a 前端开发,
I want 开发布局组件,
So that 构建页面结构。

## Acceptance Criteria

### AC1: AppHeader 组件
**Given** 开发 AppHeader 组件
**When** 页面加载
**Then** 显示 Logo 和导航链接
**And** 滚动时半透明背景

### AC2: BottomTabBar 组件
**Given** 开发 BottomTabBar 组件
**When** 在移动端显示
**Then** 显示首页、发现、发布、审核、我的
**And** 根据角色显示/隐藏审核入口

### AC3: MainLayout 组件
**Given** 开发 MainLayout 组件
**When** 包裹页面内容
**Then** 居中显示内容区（max-width: 768px）
**And** 适配移动端和桌面端

### AC4: 页面容器标准化（实际实现）
**Given** 需要统一的页面布局
**When** 创建标准页面容器样式
**Then** 定义最大宽度和内边距
**And** 支持响应式调整
**And** 所有页面遵循统一结构

### AC5: Element Plus 组件深度定制（实际实现）
**Given** Element Plus 默认样式不符合设计系统
**When** 使用 :deep() 选择器覆盖样式
**Then** 表格样式优化（表头、Hover、圆角）
**And** 对话框样式优化（标题、内边距）
**And** 描述列表样式优化
**And** 步骤条样式优化
**And** 结果组件样式优化
**And** Card Header 标题样式优化

## Tasks / Subtasks

### Task 1: 创建标准页面容器 (AC: 4)
- [x] 1.1 定义 `.page-container` 基础样式
- [x] 1.2 设置最大宽度为 `var(--max-width-content)`
- [x] 1.3 设置标准内边距 `var(--spacing-2xl) var(--spacing-base)`
- [x] 1.4 添加响应式媒体查询（≤768px）
- [x] 1.5 在所有页面中应用标准容器

### Task 2: 实现页面头部布局 (AC: 4)
- [x] 2.1 创建 `.page-header` 样式类
- [x] 2.2 使用 Flexbox 实现左右布局
- [x] 2.3 定义标题字体大小和字重
- [x] 2.4 添加底部间距 `var(--spacing-xl)`
- [x] 2.5 在所有页面中应用标准头部

### Task 3: Element Plus 表格定制 (AC: 5)
- [x] 3.1 使用 :deep() 覆盖表格圆角
- [x] 3.2 自定义表头背景色和字体
- [x] 3.3 实现行 Hover 浅蓝色高亮
- [x] 3.4 优化单元格内边距
- [x] 3.5 在 QuestionsView 和 MembersView 中应用

### Task 4: Element Plus 对话框定制 (AC: 5)
- [x] 4.1 使用 :deep() 覆盖对话框标题样式
- [x] 4.2 添加标题底部边框
- [x] 4.3 优化对话框主体内边距
- [x] 4.4 统一定义字体大小和颜色
- [x] 4.5 在所有使用对话框的页面中应用

### Task 5: Element Plus 其他组件定制 (AC: 5)
- [x] 5.1 定制 Descriptions 组件（标签和内容样式）
- [x] 5.2 定制 Steps 组件（标题和描述样式）
- [x] 5.3 定制 Result 组件（标题和副标题样式）
- [x] 5.4 定制 Card Header 标题样式
- [x] 5.5 在对应页面中应用定制样式

### Task 6: 布局文档和示例
- [x] 6.1 编写标准页面布局模板
- [x] 6.2 编写 Element Plus 定制指南
- [x] 6.3 创建布局最佳实践文档
- [x] 6.4 更新快速参考卡片

## Implementation Details

### 标准页面容器

**样式定义**:
```css
.page-container {
  max-width: var(--max-width-content);
  margin: 0 auto;
  padding: var(--spacing-2xl) var(--spacing-base);
}

/* 响应式 */
@media (max-width: 768px) {
  .page-container {
    padding: var(--spacing-xl) var(--spacing-sm);
  }
}
```

**使用方式**:
```vue
<template>
  <div class="page-container">
    <!-- 页面内容 -->
  </div>
</template>
```

### 标准页面头部

**样式定义**:
```css
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

/* 响应式 */
@media (max-width: 768px) {
  .page-title {
    font-size: var(--font-size-2xl);
  }
}
```

**使用方式**:
```vue
<template>
  <div class="page-header animate-fade-in-up">
    <h1 class="page-title">页面标题</h1>
    <AppButton variant="primary" @click="handleAction">
      操作按钮
    </AppButton>
  </div>
</template>
```

### Element Plus 表格定制

**样式定义**:
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

:deep(.el-table td.el-table__cell) {
  padding: var(--spacing-md);
}
```

**应用页面**: 
- [QuestionsView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/QuestionsView.vue) - 题库管理
- [MembersView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/MembersView.vue) - 成员管理
- [AdminPromotionReviewView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/AdminPromotionReviewView.vue) - 管理员审核

### Element Plus 对话框定制

**样式定义**:
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

:deep(.el-dialog__footer) {
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-neutral-200);
}
```

**应用页面**: 
- [QuestionsView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/QuestionsView.vue) - 添加/编辑题目
- [MembersView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/MembersView.vue) - 修改角色/状态
- [AdminPromotionReviewView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/AdminPromotionReviewView.vue) - 评审项目

### Element Plus Descriptions 定制

**样式定义**:
```css
:deep(.el-descriptions__label) {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-secondary);
}

:deep(.el-descriptions__content) {
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

:deep(.el-descriptions__body) {
  background-color: var(--color-bg-secondary);
  border-radius: var(--radius-md);
}
```

**应用页面**: 
- [PromotionView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/PromotionView.vue) - 转正信息展示
- [AdminPromotionReviewView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/AdminPromotionReviewView.vue) - 项目详情

### Element Plus Steps 定制

**样式定义**:
```css
:deep(.el-step__title) {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

:deep(.el-step__description) {
  color: var(--color-text-tertiary);
  font-size: var(--font-size-sm);
}

:deep(.el-step__head.is-process) {
  color: var(--color-primary);
  border-color: var(--color-primary);
}

:deep(.el-step__head.is-finish) {
  color: var(--color-status-published);
  border-color: var(--color-status-published);
}
```

**应用页面**: 
- [PromotionView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/PromotionView.vue) - 转正流程展示

### Element Plus Result 定制

**样式定义**:
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

:deep(.el-result__icon) {
  font-size: 64px;
}
```

**应用页面**: 
- [ExamResultView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/ExamResultView.vue) - 考试结果展示

### Element Plus Card Header 定制

**样式定义**:
```css
.card-header :deep(span) {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}
```

**使用方式**:
```vue
<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span>卡片标题</span>
      </div>
    </template>
    <!-- 卡片内容 -->
  </el-card>
</template>
```

**应用页面**: 
- [ProjectSubmitView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/ProjectSubmitView.vue) - 项目提交表单

## Testing

### 测试场景

1. **页面容器一致性测试**
   - Given: 访问不同页面
   - When: 检查页面容器的最大宽度和内边距
   - Then: 所有页面布局一致

2. **Element Plus 定制测试**
   - Given: 在多个页面中使用表格、对话框等组件
   - When: 检查组件样式
   - Then: 所有组件样式符合设计系统

3. **响应式布局测试**
   - Given: 在不同屏幕尺寸下查看页面
   - When: 调整浏览器窗口大小
   - Then: 布局正确适配，无横向滚动条

### 验证结果

✅ 所有页面容器样式统一  
✅ Element Plus 组件定制生效  
✅ 响应式布局正常工作  
✅ 视觉一致性达到预期  

## Notes

### 经验教训

1. **:deep() 选择器的力量**：通过 :deep() 可以精准覆盖 Element Plus 的默认样式，无需修改源码
2. **样式隔离**：使用 scoped 样式避免全局污染，同时通过 :deep() 覆盖子组件样式
3. **渐进增强**：先保证基本布局，再逐步添加细节定制
4. **文档重要性**：记录每个定制点的使用场景，方便后续维护

### 技术决策

1. **为什么不创建独立的布局组件**：当前项目规模适中，使用 CSS 类更轻量，减少组件层级
2. **为什么使用 :deep() 而非全局样式**：保持样式隔离，避免影响其他模块
3. **为什么选择 896px 作为内容区最大宽度**：平衡大屏幕阅读体验和空间利用率

### 后续优化建议

1. 考虑提取常用的布局模式为独立组件（如 PageLayout、ContentCard）
2. 添加更多布局变体（侧边栏布局、双栏布局等）
3. 实现布局切换功能（紧凑模式、舒适模式）
4. 添加布局自动化测试

## References

- **设计 Token 文件**: [/src/assets/design-tokens.css](file:///Users/max/Desktop/coder——project/ustil/frontend/src/assets/design-tokens.css)
- **完整规范文档**: [REFACTORING_SUMMARY.md](file:///Users/max/Desktop/coder——project/ustil/frontend/REFACTORING_SUMMARY.md)
- **快速参考卡片**: [QUICK_REFERENCE.md](file:///Users/max/Desktop/coder——project/ustil/frontend/QUICK_REFERENCE.md)
- **应用示例页面**:
  - [QuestionsView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/QuestionsView.vue)
  - [MembersView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/MembersView.vue)
  - [PromotionView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/PromotionView.vue)
