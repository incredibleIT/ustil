---
story_id: "7.1"
story_key: "7-1-design-system-config"
epic: "前端 UI 实现"
status: done
created: "2026-04-08"
completed: "2026-04-09"
---

# Story 7.1: 设计系统配置

As a 前端开发,
I want 配置设计系统,
So that 保持视觉一致性。

## Acceptance Criteria

### AC1: TailwindCSS 配置
**Given** 前端项目已初始化
**When** 配置 TailwindCSS
**Then** 设置主色调蓝色 #3B82F6
**And** 设置角色颜色（预备=灰、正式=蓝、负责人=金）

### AC2: Element Plus 主题定制
**Given** 配置 Element Plus
**When** 覆盖主题变量
**Then** 设置圆角 8px
**And** 设置基础字号 16px

### AC3: 全局样式系统
**Given** 设计系统配置完成
**When** 创建全局样式文件
**Then** 定义字体栈、间距系统、响应式断点

## Tasks / Subtasks

### Task 1: 创建设计 Token 系统 (AC: 1, 2, 3)
- [x] 1.1 创建 `/src/assets/design-tokens.css` 文件
- [x] 1.2 定义颜色系统（主色、角色、状态、中性色、文本、背景）
- [x] 1.3 定义间距系统（xs → 3xl，共7个层级）
- [x] 1.4 定义字体系统（大小 xs → 4xl，字重 normal → bold）
- [x] 1.5 定义圆角系统（sm → full，共5个层级）
- [x] 1.6 定义容器宽度（content: 896px, wide: 1280px）
- [x] 1.7 在 `main.css` 中引入设计 Token

### Task 2: 配置 TailwindCSS (AC: 1)
- [x] 2.1 在 `tailwind.config.js` 中扩展颜色系统
- [x] 2.2 配置自定义间距和字体大小
- [x] 2.3 设置响应式断点（md: 768px, lg: 1024px）

### Task 3: 配置 Element Plus 主题 (AC: 2)
- [x] 3.1 在 `main.ts` 中配置 Element Plus 全局样式
- [x] 3.2 覆盖默认圆角为 8px
- [x] 3.3 覆盖默认字体大小为 16px
- [x] 3.4 统一按钮、输入框、表格等组件的基础样式

### Task 4: 创建全局样式文件 (AC: 3)
- [x] 4.1 定义字体栈（系统字体优先）
- [x] 4.2 配置全局重置样式
- [x] 4.3 添加通用工具类（文本截断、Flex 布局等）
- [x] 4.4 配置滚动条样式

## Implementation Details

### 设计 Token 系统

#### 颜色系统
```css
/* 主色调 - 蓝色系 */
--color-primary: #3b82f6;
--color-primary-hover: #2563eb;
--color-primary-light: #dbeafe;

/* 角色颜色 */
--color-role-probation: #9ca3af;  /* 预备成员 - 灰色 */
--color-role-member: #3b82f6;     /* 正式成员 - 蓝色 */
--color-role-admin: #f59e0b;      /* 管理员 - 橙色 */

/* 状态颜色 */
--color-status-published: #10b981; /* 已发布/成功 - 绿色 */
--color-status-rejected: #ef4444;  /* 已拒绝/失败 - 红色 */
--color-status-draft: #6b7280;     /* 草稿 - 灰色 */

/* 中性色（10个层级） */
--color-neutral-50: #f9fafb;
--color-neutral-100: #f3f4f6;
--color-neutral-200: #e5e7eb;
--color-neutral-300: #d1d5db;
--color-neutral-400: #9ca3af;
--color-neutral-500: #6b7280;
--color-neutral-600: #4b5563;
--color-neutral-700: #374151;
--color-neutral-800: #1f2937;
--color-neutral-900: #111827;

/* 文本颜色 */
--color-text-primary: #111827;
--color-text-secondary: #4b5563;
--color-text-tertiary: #9ca3af;
--color-text-inverse: #ffffff;

/* 背景颜色 */
--color-bg-primary: #ffffff;
--color-bg-secondary: #f9fafb;
--color-bg-tertiary: #f3f4f6;
```

#### 间距系统
```css
--spacing-xs: 0.25rem;    /* 4px */
--spacing-sm: 0.5rem;     /* 8px */
--spacing-md: 1rem;       /* 16px */
--spacing-lg: 1.5rem;     /* 24px */
--spacing-xl: 2rem;       /* 32px */
--spacing-2xl: 3rem;      /* 48px */
--spacing-3xl: 4rem;      /* 64px */
```

#### 字体系统
```css
/* 字体大小 */
--font-size-xs: 0.75rem;    /* 12px */
--font-size-sm: 0.875rem;   /* 14px */
--font-size-base: 1rem;     /* 16px */
--font-size-lg: 1.125rem;   /* 18px */
--font-size-xl: 1.25rem;    /* 20px */
--font-size-2xl: 1.5rem;    /* 24px */
--font-size-3xl: 1.875rem;  /* 30px */
--font-size-4xl: 2.25rem;   /* 36px */

/* 字重 */
--font-weight-normal: 400;
--font-weight-medium: 500;
--font-weight-semibold: 600;
--font-weight-bold: 700;
```

#### 圆角系统
```css
--radius-sm: 0.25rem;    /* 4px */
--radius-md: 0.375rem;   /* 6px */
--radius-lg: 0.5rem;     /* 8px */
--radius-xl: 0.75rem;    /* 12px */
--radius-full: 9999px;   /* 圆形 */
```

#### 容器宽度
```css
--max-width-content: 896px;  /* 内容区域最大宽度 */
--max-width-wide: 1280px;    /* 宽屏最大宽度 */
```

### 关键决策

1. **PC 优先策略**：内容区域最大宽度设置为 896px，确保在大屏幕上有良好的阅读体验
2. **角色颜色映射**：预备成员（灰色）、正式成员（蓝色）、管理员（橙色），符合直觉
3. **间距倍数**：采用 4px 基准，所有间距都是 4 的倍数，便于对齐
4. **字体层级**：定义了 8 个字体大小层级，满足从辅助文本到标题的所有需求
5. **CSS 变量命名**：使用语义化命名（如 `--color-text-primary` 而非 `--color-gray-900`）

### 技术实现

**文件位置**: `/src/assets/design-tokens.css`

**引入方式**: 在 `/src/assets/main.css` 中通过 `@import` 引入

**使用方式**:
```css
.container {
  max-width: var(--max-width-content);
  padding: var(--spacing-2xl) var(--spacing-base);
  color: var(--color-text-primary);
}
```

## Testing

### 测试场景

1. **颜色变量测试**
   - Given: 在任何页面中使用 CSS 变量
   - When: 检查浏览器开发者工具
   - Then: 所有颜色变量正确解析

2. **间距一致性测试**
   - Given: 多个页面使用相同的间距变量
   - When: 比较不同页面的内边距和外边距
   - Then: 间距完全一致

3. **响应式测试**
   - Given: 在不同屏幕尺寸下查看页面
   - When: 调整浏览器窗口大小
   - Then: 布局正确适配

### 验证结果

✅ 所有设计 Token 正常工作  
✅ 颜色系统完整覆盖所有场景  
✅ 间距系统统一且易于使用  
✅ 字体系统满足所有文本层级需求  
✅ 圆角系统提供足够的灵活性  

## Notes

### 经验教训

1. **设计先行**：先建立完整的设计 Token 系统，再进行页面开发，避免后期返工
2. **语义化命名**：使用 `--color-text-primary` 而非 `--color-gray-900`，提高可维护性
3. **文档同步**：及时更新规范文档，方便团队成员查阅
4. **渐进式迁移**：对于已有页面，逐步替换硬编码值为 CSS 变量

### 后续优化建议

1. 考虑添加暗色模式支持（Dark Mode）
2. 为主题切换功能预留接口
3. 添加更多动画相关的 Token（时长、缓动函数）
4. 建立设计 Token 的自动化测试

## References

- **设计 Token 文件**: [/src/assets/design-tokens.css](file:///Users/max/Desktop/coder——project/ustil/frontend/src/assets/design-tokens.css)
- **全局样式文件**: [/src/assets/main.css](file:///Users/max/Desktop/coder——project/ustil/frontend/src/assets/main.css)
- **完整规范文档**: [REFACTORING_SUMMARY.md](file:///Users/max/Desktop/coder——project/ustil/frontend/REFACTORING_SUMMARY.md)
- **快速参考卡片**: [QUICK_REFERENCE.md](file:///Users/max/Desktop/coder——project/ustil/frontend/QUICK_REFERENCE.md)
