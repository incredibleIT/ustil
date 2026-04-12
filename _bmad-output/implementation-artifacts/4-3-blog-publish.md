---
story_id: "4.3"
epic_id: "4"
title: "博客发布功能"
status: "ready-for-dev"
created: "2026-04-11"
last_updated: "2026-04-11T12:30:00"
---

# Story 4.3: 博客发布功能

## 📋 User Story

**As a** 社团成员,  
**I want** 发布技术博客,  
**So that** 可以分享学习心得。

---

## ✅ Acceptance Criteria

### AC1: 进入博客编辑页面

**Given** 已登录账号  
**When** 点击发布按钮选择博客  
**Then** 进入博客编辑页面  
**And** 类型自动设为博客

### AC2: 编辑博客内容

**Given** 编辑博客  
**When** 插入代码块  
**Then** 支持语法高亮（Java、Python、C/C++、JavaScript）  
**And** 可以添加标签

### AC3: 提交审核

**Given** 发布博客  
**When** 提交审核  
**Then** 内容进入审核队列  
**And** 作者信息自动关联

---

## 🎯 Technical Requirements

### 功能需求

#### P0 - 必须实现（MVP）

1. **博客发布页面**
   - 复用 Story 4.1 的 Markdown 编辑器组件
   - 复用 Story 4.2 的封面图上传功能（可选）
   - 类型自动设置为 "blog"
   - 表单验证：标题必填（5-100字符）、内容必填
   - 摘要自动生成或手动输入（最多200字符）

2. **标签管理**
   - 支持添加多个标签
   - 标签输入框（逗号分隔或回车添加）
   - 标签显示为 Tag 组件
   - 支持删除已添加的标签
   - 标签数量限制：最多5个
   - 单个标签长度限制：最多20字符

3. **代码块语法高亮**
   - 使用 marked + highlight.js 渲染 Markdown
   - 支持常见编程语言：Java, Python, C, C++, JavaScript, TypeScript, HTML, CSS
   - 代码块显示行号（可选）
   - 代码块可复制功能

4. **提交到后端**
   - 调用 POST /api/content 接口（与资讯共用）
   - 提交数据包含：title, summary, content, type="blog", coverImage（可选）, tags
   - 初始状态设置为 "pending"（审核中）
   - authorId 从 JWT Token 中获取
   - 成功后跳转到个人中心或提示发布成功

#### P1 - 后续迭代

- 草稿箱功能
- 博客分类（技术分享、学习笔记、项目总结等）
- 相关文章推荐
- 阅读数统计
- 点赞/收藏功能

### 文件结构

```
frontend/src/
├── views/
│   └── BlogPublishView.vue       # 博客发布页面（P0）
├── api/
│   └── content.ts                # 复用 Story 4.2 的 API
└── components/editor/
    └── BlogEditor.vue            # 复用 Story 4.1 的编辑器
```

### API 接口

#### 发布内容接口（与资讯共用）

**POST** `/api/content`

**Request Body:**
```json
{
  "title": "Spring Boot 实战经验分享",
  "summary": "本文介绍 Spring Boot 的核心特性和最佳实践...",
  "content": "# Spring Boot 实战\n\n## 核心特性\n\n```java\n@SpringBootApplication\npublic class Application {\n    public static void main(String[] args) {\n        SpringApplication.run(Application.class, args);\n    }\n}\n```\n\n更多内容...",
  "type": "blog",
  "coverImage": "https://example.com/cover.jpg",
  "tags": ["Spring Boot", "Java", "后端开发"]
}
```

**Response:**
```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "id": 10,
    "status": "pending"
  },
  "success": true
}
```

### 数据库字段映射

| 前端字段 | 后端字段 | 类型 | 说明 |
|---------|---------|------|------|
| title | title | VARCHAR(100) | 标题 |
| summary | summary | VARCHAR(200) | 摘要 |
| content | content | TEXT | Markdown 内容 |
| type | type | VARCHAR(10) | 固定为 "blog" |
| coverImage | cover_image | VARCHAR(255) | 封面图 URL（可选） |
| tags | tags | JSON | 标签数组（可选） |
| - | status | VARCHAR(10) | 固定为 "pending" |
| - | author_id | BIGINT | 从 JWT 获取 |

---

## 🎨 UX/UI Design

### 页面布局

```
┌─────────────────────────────────────────────┐
│  发布博客                              [取消] │
├─────────────────────────────────────────────┤
│  标题: [_________________________________]   │
│                                              │
│  封面图:                                     │
│  ┌──────────┐                                │
│  │ + 上传图片│  (可选，最大5MB)               │
│  └──────────┘                                │
│                                              │
│  标签:                                       │
│  [Spring Boot ×] [Java ×] [后端开发 ×] [+]  │
│  (最多5个标签，按回车添加)                    │
│                                              │
│  摘要:                                       │
│  ┌──────────────────────────────────────┐   │
│  │ 本文介绍 Spring Boot 的核心特性...    │   │
│  └──────────────────────────────────────┘   │
│  (0/200) 自动生成                            │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │                                      │   │
│  │      Markdown 编辑器区域              │   │
│  │                                      │   │
│  │  工具栏: B I ` [] () ![]()           │   │
│  │                                      │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  [预览]          [保存草稿]  [发布博客]      │
└─────────────────────────────────────────────┘
```

### 交互细节

1. **标签输入**
   - 输入标签文本后按回车或逗号添加
   - 显示为 Element Plus Tag 组件
   - 点击 Tag 上的 × 删除
   - 超过5个标签时禁用输入框

2. **摘要生成**
   - 默认从内容前200字符自动生成
   - 用户可手动编辑
   - 实时显示字符计数 (0/200)
   - 超过200字符时变红提示

3. **代码块高亮**
   - 编辑器中显示原始 Markdown 语法
   - 预览模式下应用 highlight.js 高亮
   - 支持语言标识：```java, ```python 等

4. **表单验证**
   - 标题：5-100字符，必填
   - 内容：不能为空
   - 摘要：最多200字符
   - 标签：最多5个，每个最多20字符
   - 验证失败时显示红色边框和错误提示

---

## 🔧 Implementation Notes

### 前端实现要点

#### 1. 路由配置

在 `router/index.ts` 中添加：

```typescript
{
  path: '/blog/publish',
  name: 'blogPublish',
  component: () => import('../views/BlogPublishView.vue'),
  meta: { requiresAuth: true }, // 临时移除角色限制
}
```

#### 2. 导航入口

在 `AppNavbar.vue` 中添加"发布博客"链接（可与"发布资讯"合并为下拉菜单或分开显示）。

#### 3. 标签组件实现

使用 Element Plus 的 Tag 和 Input 组件：

```vue
<template>
  <div class="tags-input">
    <el-tag
      v-for="tag in tags"
      :key="tag"
      closable
      @close="removeTag(tag)"
    >
      {{ tag }}
    </el-tag>
    <el-input
      v-if="inputVisible"
      ref="inputRef"
      v-model="inputValue"
      size="small"
      @keyup.enter="handleInputConfirm"
      @blur="handleInputConfirm"
      style="width: 100px"
    />
    <el-button
      v-else
      size="small"
      @click="showInput"
    >
      + 添加标签
    </el-button>
  </div>
</template>
```

#### 4. 代码高亮集成

安装依赖：
```bash
npm install highlight.js
```

在预览时使用：
```typescript
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

// 渲染 Markdown 并应用高亮
const renderedContent = marked(markdownContent, {
  highlight: function(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value
    }
    return hljs.highlightAuto(code).value
  }
})
```

#### 5. 提交逻辑

```typescript
async function handleSubmit() {
  // 表单验证
  if (!validateForm()) return
  
  submitting.value = true
  
  try {
    await publishContent({
      title: title.value,
      summary: summary.value || generateSummary(content.value),
      content: content.value,
      type: 'blog',
      coverImage: coverImage.value || undefined,
      tags: tags.value.length > 0 ? tags.value : undefined
    })
    
    ElMessage.success('发布成功，等待审核')
    router.push('/profile')
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '发布失败'
    ElMessage.error(message)
  } finally {
    submitting.value = false
  }
}
```

### 后端实现要点

#### 1. 复用现有接口

博客发布与资讯发布共用同一个接口 `POST /api/content`，只需将 `type` 字段设置为 `"blog"`。

#### 2. 标签存储

Content 实体已有 `tags` 字段（JSON 类型），MyBatis-Plus 会自动处理序列化和反序列化。

#### 3. 权限控制

与资讯发布相同，使用 `@PreAuthorize("isAuthenticated()")`（临时放宽）或 `@PreAuthorize("hasAnyRole('MEMBER', 'PROBATION')")`（生产环境）。

---

## 🧪 Testing Guidelines

### 功能测试

1. **基本发布流程**
   - [ ] 填写标题、内容，不填其他字段，能成功发布
   - [ ] 标题少于5字符，显示错误提示
   - [ ] 标题超过100字符，显示错误提示
   - [ ] 内容为空，显示错误提示
   - [ ] 摘要超过200字符，显示错误提示

2. **标签功能**
   - [ ] 添加1个标签，正常显示
   - [ ] 添加5个标签，输入框禁用
   - [ ] 尝试添加第6个标签，阻止添加
   - [ ] 删除标签，输入框恢复可用
   - [ ] 标签包含特殊字符，正常保存

3. **封面图上传**
   - [ ] 上传图片成功，显示预览
   - [ ] 上传非图片文件，显示错误
   - [ ] 上传超过5MB的文件，显示错误
   - [ ] 不上传封面图，能正常发布

4. **代码高亮**
   - [ ] 插入 Java 代码块，预览时正确高亮
   - [ ] 插入 Python 代码块，预览时正确高亮
   - [ ] 不指定语言，自动检测高亮
   - [ ] 代码块包含特殊字符，正确转义

5. **提交验证**
   - [ ] 提交后数据库中 type="blog"
   - [ ] 提交后数据库中 status="pending"
   - [ ] 提交后 author_id 正确
   - [ ] 提交后 tags 正确保存为 JSON
   - [ ] 个人中心能看到刚发布的博客

### 边界测试

- [ ] 标签为空数组，能正常发布
- [ ] 标签为 null，能正常发布
- [ ] 内容为纯代码块，摘要正确生成
- [ ] 内容包含大量 Markdown 格式，摘要清理正确
- [ ] 快速连续点击发布按钮，只提交一次

### 集成测试

- [ ] 发布后在个人中心"我的内容"中显示
- [ ] 类型显示为"博客"
- [ ] 状态显示为"审核中"（黄色标签）
- [ ] 标签正确显示
- [ ] 封面图正确显示（如果有）

---

## 📚 Related Documents

- **Story 4.1**: Markdown 编辑器组件
- **Story 4.2**: 资讯发布功能
- **Epic 4**: 内容发布与管理
- **PRD**: FR11 - 博客发布
- **UX Design**: 内容发布页面设计规范

---

## 📝 Developer Notes

### 与 Story 4.2 的区别

1. **类型字段**：type="blog" vs type="news"
2. **标签支持**：博客需要标签，资讯不需要
3. **封面图**：博客的封面图是可选的，资讯建议上传
4. **代码高亮**：博客更强调代码块的展示

### 可复用的部分

1. **Markdown 编辑器**：完全复用 Story 4.1
2. **API 接口**：完全复用 Story 4.2 的 POST /api/content
3. **封面图上传**：完全复用 Story 4.2 的逻辑
4. **摘要生成**：完全复用 Story 4.2 的逻辑
5. **表单验证**：大部分规则相同

### 新增的部分

1. **标签管理组件**：需要新建
2. **代码高亮集成**：需要配置 highlight.js
3. **路由和导航**：新的页面路径

### 注意事项

1. **标签存储**：确保后端 Content 实体的 tags 字段类型为 JSON
2. **代码高亮样式**：选择合适的主题（github.css 或 atom-one-dark.css）
3. **性能优化**：如果博客很多，考虑分页加载标签云
4. **SEO 友好**：博客标题和摘要应该对搜索引擎友好

---

**Story Created:** 2026-04-11  
**Status:** ready-for-dev  
**Priority:** P0 (MVP)  
**Estimated Effort:** 4-6 hours
