---
story_id: "1.2"
story_key: "1-2-frontend-project-init"
epic: "项目初始化与基础架构搭建"
status: done
created: "2026-04-03"
---

# Story 1.2: 前端项目初始化

## Story

As a 开发团队,
I want 初始化 Vue.js 项目并配置基础依赖,
So that 可以开始前端开发。

## Acceptance Criteria

### AC1: Vue.js 项目初始化
**Given** 使用 npm create vue@latest
**When** 创建项目
**Then** 选择 TypeScript, Router, Pinia, ESLint, Prettier
**And** 安装 Element Plus 和 TailwindCSS

### AC2: 项目配置
**Given** 项目创建完成
**When** 配置 vite.config.ts
**Then** 设置 Element Plus 自动导入和 TailwindCSS 集成
**And** 配置路由和 Pinia Store 结构

## Tasks / Subtasks

- [x] Task 1: 使用 npm create vue@latest 创建项目 (AC: #1)
  - [x] 运行 `npm create vue@latest frontend`
  - [x] 选择 TypeScript: Yes
  - [x] 选择 Vue Router: Yes
  - [x] 选择 Pinia: Yes
  - [x] 选择 ESLint: Yes
  - [x] 选择 Prettier: Yes
  - [x] 进入 frontend 目录运行 `npm install`

- [x] Task 2: 安装 Element Plus (AC: #1)
  - [x] 安装 Element Plus: `npm install element-plus`
  - [x] 安装自动导入插件: `npm install -D unplugin-vue-components unplugin-auto-import`
  - [x] 配置 vite.config.ts 添加 Element Plus 自动导入

- [x] Task 3: 安装和配置 TailwindCSS (AC: #1)
  - [x] 安装 TailwindCSS: `npm install -D tailwindcss@3.4.17 postcss autoprefixer`
  - [x] 初始化配置: 手动创建 tailwind.config.js 和 postcss.config.js
  - [x] 配置 tailwind.config.js 内容路径
  - [x] 创建 src/styles/index.css 导入 Tailwind 指令

- [x] Task 4: 配置路由结构 (AC: #2)
  - [x] 创建 src/router/index.ts 基础配置
  - [x] 配置路由守卫（预留）
  - [x] 创建基础路由: /, /login, /register

- [x] Task 5: 配置 Pinia Store 结构 (AC: #2)
  - [x] 创建 src/stores/index.ts 导出所有 stores
  - [x] 创建 src/stores/auth.ts（预留）
  - [x] 创建 src/stores/user.ts（预留）
  - [x] 创建 src/stores/content.ts（预留）
  - [x] 创建 src/stores/review.ts（预留）
  - [x] 在 main.ts 中注册 Pinia

- [x] Task 6: 创建项目目录结构 (AC: #2)
  - [x] 创建 src/api/ 目录
  - [x] 创建 src/components/common/, layout/, auth/, member/, content/, review/ 目录
  - [x] 创建 src/composables/ 目录
  - [x] 创建 src/types/ 目录
  - [x] 创建 src/utils/ 目录
  - [x] 创建 src/views/ 目录
  - [x] 创建 src/styles/ 目录

- [x] Task 7: 验证项目构建 (AC: #2)
  - [x] 运行 `npm run build` 构建成功
  - [x] 运行 `npm run dev` 开发服务器启动成功
  - [x] 访问 http://localhost:5173 页面正常显示

## Dev Notes

### 技术栈要求

**必须使用的技术版本** [Source: docs/architecture.md]:
- Vue.js 3.4.x
- TypeScript 5.x
- Vite 5.x
- Vue Router 4.x
- Pinia 2.x
- Element Plus 2.5+
- TailwindCSS 3.4+

### 项目初始化命令

```bash
npm create vue@latest frontend
```

**Node.js 版本要求**:
- 推荐 Node.js 18.x 或 20.x LTS 版本
- 最低要求 Node.js 16.x

**nvm 版本管理（如 npm 版本过低）**:
```bash
# 查看已安装的 Node.js 版本
nvm ls

# 查看可下载的版本
nvm ls-remote

# 安装特定版本（如 20.x LTS）
nvm install 20

# 切换到指定版本
nvm use 20

# 设置默认版本
nvm alias default 20
```

**选择配置**:
- Project name: `frontend`
- TypeScript: `Yes`
- JSX Support: `No`
- Vue Router: `Yes`
- Pinia: `Yes`
- Vitest: `No` (后续按需添加)
- Cypress/E2E: `No` (后续按需添加)
- ESLint: `Yes`
- Prettier: `Yes`

### Element Plus 配置

**安装依赖**:
```bash
cd frontend
npm install element-plus
npm install -D unplugin-vue-components unplugin-auto-import
```

**vite.config.ts 配置**:
```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router', 'pinia']
    }),
    Components({
      resolvers: [ElementPlusResolver()]
    })
  ]
})
```

### TailwindCSS 配置

**安装依赖**:
```bash
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p
```

**tailwind.config.js**:
```javascript
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}"
  ],
  theme: {
    extend: {}
  },
  plugins: []
}
```

**src/styles/index.css**:
```css
@tailwind base;
@tailwind components;
@tailwind utilities;
```

**main.ts 导入样式**:
```typescript
import './styles/index.css'
```

### 项目结构要求

**必须遵循的目录结构** [Source: docs/architecture.md]:
```
frontend/
├── src/
│   ├── api/                # API 请求封装
│   ├── assets/             # 静态资源
│   ├── components/         # 组件
│   │   ├── common/         # 通用组件
│   │   ├── layout/         # 布局组件
│   │   ├── auth/           # 认证相关
│   │   ├── member/         # 成员管理
│   │   ├── content/        # 内容相关
│   │   └── review/         # 审核相关
│   ├── composables/        # 组合式函数
│   ├── router/             # 路由配置
│   │   └── index.ts
│   ├── stores/             # Pinia 状态管理
│   │   ├── index.ts
│   │   ├── auth.ts
│   │   ├── user.ts
│   │   ├── content.ts
│   │   └── review.ts
│   ├── styles/             # 全局样式
│   │   └── index.css
│   ├── types/              # TypeScript 类型
│   ├── utils/              # 工具函数
│   └── views/              # 页面视图
│       ├── HomeView.vue
│       ├── LoginView.vue
│       └── RegisterView.vue
├── public/
├── Dockerfile
├── package.json
├── tsconfig.json
└── vite.config.ts
```

### 路由基础配置

**src/router/index.ts**:
```typescript
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue')
    }
  ]
})

export default router
```

### Pinia Store 基础结构

**src/stores/index.ts**:
```typescript
export * from './auth'
export * from './user'
export * from './content'
export * from './review'
```

**src/stores/auth.ts**（预留）:
```typescript
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // 预留实现
  const token = ref('')
  
  return { token }
})
```

### 命名规范

**必须遵守的命名规范** [Source: docs/architecture.md]:
- 组件名: PascalCase (如 `UserProfile.vue`, `ContentCard.vue`)
- 组合式函数: camelCase 前缀 use (如 `useAuth`, `useContent`)
- Store: camelCase (如 `auth.ts`, `user.ts`)
- 类型/接口: PascalCase 前缀 I (如 `IUser`, `IContent`)
- 工具函数: camelCase (如 `formatDate`, `parseToken`)

### 依赖版本锁定

**package.json 关键依赖版本**:
```json
{
  "dependencies": {
    "vue": "^3.4.x",
    "vue-router": "^4.x",
    "pinia": "^2.x",
    "element-plus": "^2.5.x"
  },
  "devDependencies": {
    "typescript": "^5.x",
    "tailwindcss": "^3.4.x",
    "unplugin-vue-components": "^0.x",
    "unplugin-auto-import": "^0.x"
  }
}
```

### 测试要求

- 项目必须能通过 `npm run build` 构建成功
- 开发服务器 `npm run dev` 启动无错误
- 页面在浏览器中正常显示

## References

- [Source: docs/architecture.md] - 技术栈决策、项目结构、命名规范
- [Source: _bmad-output/planning-artifacts/epics.md] - Story 1.2 原始需求
- Vue.js 3 官方文档: https://vuejs.org/
- Element Plus 文档: https://element-plus.org/
- TailwindCSS 文档: https://tailwindcss.com/
- Pinia 文档: https://pinia.vuejs.org/

## Dev Agent Record

### Agent Model Used

{{agent_model_name_version}}

### Debug Log References

### Completion Notes List

- 2026-04-03: 代码审查修复完成
  - 为 content store 添加 IContent 类型定义
  - 为 review store 添加 IReviewQueueItem 和 IReviewVote 类型定义
  - 为路由守卫添加 RouteLocationNormalized 和 NavigationGuardNext 类型
  - 创建 .env 和 .env.example 环境变量配置文件
  - 安装 axios 并创建 request.ts API 请求封装
  - 创建 types 目录和类型定义文件
  - 项目构建验证通过

### File List

- frontend/package.json
- frontend/vite.config.ts
- frontend/tailwind.config.js
- frontend/postcss.config.js
- frontend/tsconfig.json
- frontend/.env
- frontend/.env.example
- frontend/src/main.ts
- frontend/src/router/index.ts
- frontend/src/stores/index.ts
- frontend/src/stores/auth.ts
- frontend/src/stores/user.ts
- frontend/src/stores/content.ts
- frontend/src/stores/review.ts
- frontend/src/types/index.ts
- frontend/src/types/content.ts
- frontend/src/types/review.ts
- frontend/src/types/user.ts
- frontend/src/api/index.ts
- frontend/src/api/request.ts
- frontend/src/styles/index.css
- frontend/src/views/HomeView.vue
- frontend/src/views/LoginView.vue
- frontend/src/views/RegisterView.vue
