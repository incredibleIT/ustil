# 核心组件使用指南

## 📋 概述

本文档说明如何使用已重构的核心组件。所有组件都基于设计 Token 系统，确保一致的视觉风格和交互体验。

---

## 🧩 可用组件

### 1. AppNavbar - 导航栏组件

**位置：** `src/components/layout/AppNavbar.vue`

**功能：**
- ✅ 粘性定位，滚动时阴影变化
- ✅ 响应式设计，移动端适配
- ✅ 角色徽章显示（预备/正式/负责人）
- ✅ 通知中心徽章
- ✅ 登录/注册按钮
- ✅ 退出登录功能

**使用示例：**

```vue
<script setup lang="ts">
import AppNavbar from '@/components/layout/AppNavbar.vue'
</script>

<template>
  <div>
    <AppNavbar />
    <!-- 页面内容 -->
  </div>
</template>
```

**特性：**
- 自动处理认证状态
- 根据用户角色显示不同导航链接
- 滚动监听，动态调整阴影
- 集成通知计数

---

### 2. ContentCard - 内容卡片组件

**位置：** `src/components/content/ContentCard.vue`

**Props：**

| 属性 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| title | string | ✅ | - | 卡片标题 |
| excerpt | string | ❌ | '' | 摘要内容 |
| author | string | ❌ | '' | 作者名称 |
| date | string | ❌ | '' | 日期 |
| status | 'draft' \| 'pending' \| 'published' \| 'rejected' | ❌ | undefined | 内容状态 |
| role | 'probation' \| 'member' \| 'admin' | ❌ | undefined | 作者角色 |
| coverImage | string | ❌ | '' | 封面图片 URL |
| tags | string[] | ❌ | [] | 标签列表 |

**Events：**

| 事件名 | 参数 | 说明 |
|--------|------|------|
| click | - | 卡片点击事件 |

**使用示例：**

```vue
<script setup lang="ts">
import ContentCard from '@/components/content/ContentCard.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const handleCardClick = () => {
  router.push('/content/123')
}
</script>

<template>
  <ContentCard
    title="Vue 3 Composition API 最佳实践"
    excerpt="深入探讨 Vue 3 Composition API 的使用技巧..."
    author="张三"
    date="2026-04-08"
    role="member"
    status="published"
    :tags="['Vue', '前端']"
    @click="handleCardClick"
  />
</template>
```

**特性：**
- ✅ Hover 效果（阴影加深、标题变色）
- ✅ 角色徽章自动着色
- ✅ 状态标签自动着色
- ✅ 标签展示
- ✅ 可选封面图（带缩放动画）
- ✅ 文本截断（标题2行、摘要2行）

---

### 3. AppButton - 按钮组件

**位置：** `src/components/common/AppButton.vue`

**Props：**

| 属性 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| variant | 'primary' \| 'secondary' \| 'outline' \| 'ghost' | ❌ | 'primary' | 按钮变体 |
| size | 'sm' \| 'md' \| 'lg' | ❌ | 'md' | 按钮尺寸 |
| loading | boolean | ❌ | false | 加载状态 |
| disabled | boolean | ❌ | false | 禁用状态 |
| type | 'button' \| 'submit' \| 'reset' | ❌ | 'button' | 按钮类型 |

**Events：**

| 事件名 | 参数 | 说明 |
|--------|------|------|
| click | MouseEvent | 点击事件 |

**使用示例：**

```vue
<script setup lang="ts">
import AppButton from '@/components/common/AppButton.vue'
import { ref } from 'vue'

const isLoading = ref(false)

const handleSubmit = async () => {
  isLoading.value = true
  try {
    // 异步操作
    await someAsyncOperation()
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <!-- 主要按钮 -->
  <AppButton variant="primary" size="md" @click="handleSubmit">
    提交
  </AppButton>

  <!-- 次要按钮 -->
  <AppButton variant="secondary" size="md">
    取消
  </AppButton>

  <!-- 轮廓按钮 -->
  <AppButton variant="outline" size="md">
    查看详情
  </AppButton>

  <!-- 幽灵按钮 -->
  <AppButton variant="ghost" size="sm">
    更多
  </AppButton>

  <!-- 加载状态 -->
  <AppButton variant="primary" :loading="isLoading">
    加载中...
  </AppButton>

  <!-- 禁用状态 -->
  <AppButton variant="primary" disabled>
    禁用按钮
  </AppButton>
</template>
```

**变体说明：**

- **Primary**: 蓝色背景，白色文字，带阴影，适合主要操作
- **Secondary**: 白色背景，边框，带阴影，适合次要操作
- **Outline**: 透明背景，蓝色边框，适合辅助操作
- **Ghost**: 透明背景，无边框，适合链接式按钮

**尺寸说明：**

- **sm**: 小号按钮（px-3 py-1.5 text-sm）
- **md**: 中号按钮（px-4 py-2 text-base）- 默认
- **lg**: 大号按钮（px-6 py-3 text-lg）

**特性：**
- ✅ 加载状态动画
- ✅ 禁用状态样式
- ✅ Hover 效果（颜色变化、阴影加深）
- ✅ Focus 环（无障碍访问）
- ✅ 过渡动画

---

## 🎨 设计 Token 使用

所有组件都使用设计 Token 系统，您可以通过以下方式自定义：

### 1. 修改 TailwindCSS 配置

编辑 `tailwind.config.js`：

```javascript
export default {
  theme: {
    extend: {
      colors: {
        primary: {
          500: '#your-color',  // 修改主色
        },
      },
      borderRadius: {
        xl: '20px',  // 修改圆角
      },
      boxShadow: {
        card: 'your-shadow',  // 修改阴影
      },
    },
  },
}
```

### 2. 修改 CSS 变量

编辑 `src/assets/design-tokens.css`：

```css
:root {
  --color-primary-500: #your-color;
  --radius-xl: 20px;
  --shadow-card: your-shadow;
}
```

---

## 📝 最佳实践

### ✅ 推荐做法

1. **始终使用组件而非原生 HTML 元素**
   ```vue
   <!-- ✅ 好 -->
   <AppButton variant="primary">提交</AppButton>
   
   <!-- ❌ 不好 -->
   <button class="bg-blue-500 text-white px-4 py-2 rounded">提交</button>
   ```

2. **在深色/渐变背景上使用正确的按钮样式**
   ```vue
   <!-- ✅ 正确：使用 outline + 自定义样式 -->
   <AppButton
     variant="outline"
     class="border-white text-white hover:bg-white hover:text-primary-600"
   >
     白色按钮
   </AppButton>
   
   <!-- ❌ 错误：variant 与自定义样式冲突 -->
   <AppButton
     variant="primary"
     class="bg-white text-primary-600"  <!-- 会被覆盖，文字不可见！ -->
   >
     看不见的按钮
   </AppButton>
   ```
   
   **重要原则**：
   - 在深色/渐变背景上，必须确保文字颜色与背景有足够的对比度
   - 使用 `variant="outline"` 作为基础，然后通过自定义 class 调整颜色
   - 避免同时设置 variant 和冲突的 bg-/text- 类名

3. **使用语义化的 Props**
   ```vue
   <!-- ✅ 好 -->
   <ContentCard
     title="文章标题"
     role="member"
     status="published"
   />
   
   <!-- ❌ 不好 -->
   <ContentCard
     title="文章标题"
     :custom-class="'badge-blue'"
   />
   ```

4. **组合使用组件**
   ```vue
   <template>
     <AppNavbar />
     
     <main class="max-w-content mx-auto px-4 py-8">
       <ContentCard
         v-for="item in items"
         :key="item.id"
         :title="item.title"
         @click="handleClick(item.id)"
       />
       
       <AppButton variant="primary" size="lg">
         加载更多
       </AppButton>
     </main>
   </template>
   ```

### ❌ 避免的做法

1. **不要直接修改组件内部样式**
   ```vue
   <!-- ❌ 不好 -->
   <style scoped>
   :deep(.content-card) {
     background: red;  /* 破坏了设计系统 */
   }
   </style>
   ```

2. **不要硬编码颜色和尺寸**
   ```vue
   <!-- ❌ 不好 -->
   <div style="background: #3b82f6; padding: 17px;">
   
   <!-- ✅ 好 -->
   <div class="bg-primary-500 p-4">
   ```

3. **不要忽略响应式设计**
   ```vue
   <!-- ❌ 不好 -->
   <div class="grid grid-cols-3">
   
   <!-- ✅ 好 -->
   <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
   ```

---

## 🔧 扩展现有组件

如果需要扩展组件功能，请遵循以下步骤：

### 1. 添加新的 Props

```vue
<script setup lang="ts">
interface Props {
  // 现有 props
  title: string
  
  // 新增 props
  featured?: boolean  // 是否精选
}

const props = withDefaults(defineProps<Props>(), {
  featured: false
})
</script>

<template>
  <article :class="{ 'border-2 border-primary-500': featured }">
    <!-- 组件内容 -->
  </article>
</template>
```

### 2. 添加新的 Events

```vue
<script setup lang="ts">
const emit = defineEmits<{
  click: []
  favorite: [id: number]  // 新增事件
}>()

const handleFavorite = () => {
  emit('favorite', props.id)
}
</script>

<template>
  <button @click="handleFavorite">
    <slot name="favorite-icon">❤️</slot>
  </button>
</template>
```

### 3. 添加新的 Slots

```vue
<template>
  <article>
    <header>
      <slot name="header">
        <h3>{{ title }}</h3>
      </slot>
    </header>
    
    <main>
      <slot></slot>
    </main>
    
    <footer>
      <slot name="footer">
        <time>{{ date }}</time>
      </slot>
    </footer>
  </article>
</template>
```

---

## 🆘 常见问题

### Q: 如何自定义组件样式？

**A:** 
1. 优先使用 Props（如果组件支持）
2. 使用 TailwindCSS 类覆盖（通过外层容器）
3. 修改设计 Token（影响全局）
4. 不建议使用 `:deep()` 或 `!important`

### Q: 组件不支持我需要的功能怎么办？

**A:**
1. 检查是否有合适的 Props 或 Slots
2. 考虑扩展组件（见上方"扩展现有组件"部分）
3. 如果功能通用，可以提 PR 添加到组件库
4. 如果只是特定场景使用，可以创建新组件

### Q: 如何确保组件性能？

**A:**
1. 使用 `v-if` 而非 `v-show` 控制条件渲染
2. 为大列表使用虚拟滚动
3. 避免在模板中使用复杂计算
4. 使用 `computed` 缓存计算结果

---

## 📚 相关资源

- [设计 Token 使用指南](./design-tokens-guide.md)
- [UX 设计规范](../../_bmad-output/planning-artifacts/ux-design-specification.md)
- [TailwindCSS 官方文档](https://tailwindcss.com/docs)
- [Vue 3 官方文档](https://vuejs.org/)

---

**最后更新：** 2026-04-08  
**维护者：** 前端开发团队
