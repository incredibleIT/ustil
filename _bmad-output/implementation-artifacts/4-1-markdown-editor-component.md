---
story_id: "4.1"
epic_id: "4"
title: "Markdown 编辑器组件"
status: "ready-for-dev"
created: "2026-04-09"
last_updated: "2026-04-09T10:50:00"
---

# Story 4.1: Markdown 编辑器组件

## 📋 User Story

**As a** 开发团队,  
**I want** 实现 Markdown 编辑器组件,  
**So that** 用户可以发布内容。

---

## ✅ Acceptance Criteria

### AC1: 基础编辑界面

**Given** 进入内容发布页面  
**When** 显示编辑器  
**Then** 包含标题输入框和正文编辑区  
**And** 工具栏支持粗体、斜体、代码、链接、图片

### AC2: 草稿自动保存

**Given** 编辑内容  
**When** 每30秒自动保存  
**Then** 保存为草稿到本地存储  
**And** 恢复时提示有未保存草稿

### AC3: 图片上传

**Given** 粘贴图片  
**When** 自动上传  
**Then** 显示上传进度  
**And** 上传成功后插入图片链接

### AC4: 实时预览

**Given** 点击预览  
**When** 切换预览模式  
**Then** 实时渲染 Markdown 内容  
**And** 代码块显示语法高亮

---

## 🎯 Technical Requirements

### 技术选型

**编辑器内核：** Tiptap (Vue 3 集成)
- 版本：`@tiptap/vue-3` ^2.x
- 扩展包：`@tiptap/starter-kit`（基础功能）、`@tiptap/extension-image`（图片支持）
- 优势：Headless、高度可定制、TypeScript 友好、社区活跃

**Markdown 解析：** marked.js
- 版本：`marked` ^12.x
- 用途：将 Markdown 文本渲染为 HTML（预览区）

**样式框架：** Tailwind CSS + Element Plus
- 遵循项目设计系统规范
- 使用 CSS 变量保持主题一致性

### 核心功能清单

#### P0 - 必须实现（MVP）

1. **基础编辑功能**
   - 标题（H1-H3）
   - 加粗（Bold）
   - 斜体（Italic）
   - 无序列表（Bullet List）
   - 有序列表（Ordered List）
   - 代码块（Code Block）
   - 引用块（Blockquote）
   - 链接（Link）
   - 图片（Image）

2. **实时预览**
   - 左右分屏布局（左侧编辑，右侧预览）
   - Markdown 实时渲染
   - 代码块语法高亮（使用 highlight.js）

3. **草稿管理**
   - localStorage 存储草稿
   - 定时自动保存（每 30 秒）
   - 页面刷新后自动恢复草稿
   - 显示"已保存"提示

4. **图片上传**
   - 拖拽上传图片
   - 粘贴上传图片（Ctrl+V）
   - 点击按钮选择图片
   - 显示上传进度条
   - 上传成功后插入 Markdown 图片语法 `![alt](url)`

#### P1 - 后续迭代

- 撤销/重做（Undo/Redo）
- 查找替换
- 字数统计
- 全屏模式
- 导出 Markdown 文件

### 文件结构

```
frontend/src/
├── components/editor/
│   ├── BlogEditor.vue          # 主编辑器组件（P0）
│   └── EditorToolbar.vue       # 工具栏组件（可选，P0）
├── composables/
│   └── useDraft.ts             # 草稿管理组合式函数（P0）
├── api/
│   └── upload.ts               # 图片上传 API（P0）
└── views/
    └── TestBlogEditorView.vue  # 测试页面（P0）
```

### API 接口

#### 图片上传接口

**POST** `/api/upload/image`

**Request:**
```typescript
interface UploadImageRequest {
  file: File // 图片文件
}
```

**Response:**
```typescript
interface UploadImageResponse {
  code: number // 200 成功
  message: string
  data: {
    url: string // 图片访问 URL
    filename: string // 文件名
  }
}
```

**错误处理:**
- 400: 文件格式不支持（仅支持 jpg/png/gif/webp）
- 413: 文件大小超过限制（最大 5MB）
- 500: 服务器错误

---

## 🏗️ Architecture Compliance

### 前端架构规范

1. **组件设计原则**
   - 单一职责：每个组件只负责一个功能
   - Props 向下传递，Events 向上传递
   - 使用 Composition API (`<script setup>`)
   - TypeScript 类型定义完整

2. **状态管理**
   - 编辑器状态使用 `ref` / `reactive` 本地管理
   - 草稿数据通过 `useDraft` composable 管理
   - 不直接使用 Pinia（除非需要跨组件共享）

3. **样式规范**
   - 使用 Tailwind CSS utility classes
   - 自定义样式写入 `<style scoped>`
   - 遵循项目 CSS 变量规范（颜色、间距、字体）
   - 响应式设计：PC 优先，移动端适配

4. **错误处理**
   - 所有异步操作必须有 try-catch
   - 用户友好的错误提示（ElMessage）
   - 控制台输出详细错误日志（便于调试）

### 代码质量要求

1. **TypeScript 严格模式**
   - 所有变量、函数参数、返回值必须有类型注解
   - 不使用 `any` 类型（除非绝对必要）
   - 接口定义清晰

2. **注释规范**
   - 复杂逻辑必须有中文注释
   - 公共函数必须有 JSDoc 注释
   - TODO 标记未完成的功能

3. **性能优化**
   - 大列表使用虚拟滚动（如果适用）
   - 图片懒加载
   - 防抖/节流处理频繁事件（如输入、滚动）

---

## 📚 Library & Framework Requirements

### NPM 依赖安装

```bash
cd frontend
npm install @tiptap/vue-3 @tiptap/starter-kit @tiptap/extension-image marked highlight.js
```

**依赖说明：**
- `@tiptap/vue-3`: Tiptap Vue 3 集成
- `@tiptap/starter-kit`: 基础编辑功能扩展集合
- `@tiptap/extension-image`: 图片支持扩展
- `marked`: Markdown 解析器
- `highlight.js`: 代码语法高亮

### 版本兼容性

| 库 | 版本 | 兼容性 |
|---|---|---|
| Vue | 3.5.31 | ✅ 已安装 |
| TypeScript | 6.0.0 | ✅ 已安装 |
| Element Plus | 2.5+ | ✅ 已安装 |
| Tailwind CSS | 3.4+ | ✅ 已安装 |
| Tiptap | 2.x | ⬜ 需安装 |
| marked | 12.x | ⬜ 需安装 |
| highlight.js | 11.x | ⬜ 需安装 |

---

## 🧪 Testing Requirements

### 单元测试（可选，P1）

测试文件位置：`frontend/src/components/editor/__tests__/BlogEditor.spec.ts`

**测试用例：**
1. 组件正确渲染
2. 标题输入框可以输入文本
3. 工具栏按钮点击触发对应命令
4. 草稿自动保存到 localStorage
5. 页面刷新后恢复草稿
6. 图片上传成功插入 Markdown 语法

### 手动测试清单

**测试环境：**
- 浏览器：Chrome 最新版
- 分辨率：1920x1080（PC）、375x667（移动端）

**测试步骤：**

1. **基础编辑功能**
   - [ ] 打开测试页面 `/test-blog-editor`
   - [ ] 输入标题（至少 5 个字符）
   - [ ] 在编辑区输入文本
   - [ ] 点击加粗按钮，选中文本变粗
   - [ ] 点击斜体按钮，选中文本变斜
   - [ ] 点击 H1 按钮，文本变为一级标题
   - [ ] 点击列表按钮，创建无序列表
   - [ ] 点击代码块按钮，插入代码块
   - [ ] 点击引用按钮，插入引用块

2. **实时预览**
   - [ ] 右侧预览区实时显示渲染效果
   - [ ] 代码块显示语法高亮
   - [ ] 列表、标题、引用等格式正确渲染

3. **草稿保存**
   - [ ] 编辑内容后等待 30 秒
   - [ ] 查看 localStorage 中是否有 `blog_draft` 键
   - [ ] 刷新页面，内容自动恢复
   - [ ] 显示"已恢复草稿"提示

4. **图片上传**
   - [ ] 复制一张图片，在编辑区按 Ctrl+V
   - [ ] 显示上传进度条
   - [ ] 上传成功后插入 `![image](url)` 语法
   - [ ] 预览区正确显示图片

5. **响应式设计**
   - [ ] 缩小浏览器窗口到 768px 以下
   - [ ] 预览区隐藏或切换到下方
   - [ ] 工具栏按钮正常显示

---

## 📝 Implementation Notes

### 关键实现细节

#### 1. Tiptap 初始化

```typescript
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'

const editor = useEditor({
  content: '',
  extensions: [
    StarterKit,
    Image.configure({
      inline: true,
      allowBase64: false // 不允许 base64，必须上传
    })
  ],
  onUpdate: ({ editor }) => {
    const html = editor.getHTML()
    emit('change', html)
  }
})
```

#### 2. 草稿管理 Composable

```typescript
// composables/useDraft.ts
import { ref, watch } from 'vue'

export function useDraft(key: string = 'blog_draft') {
  const draft = ref<string>('')
  const lastSaved = ref<number>(0)

  // 加载草稿
  function loadDraft() {
    const saved = localStorage.getItem(key)
    if (saved) {
      try {
        const data = JSON.parse(saved)
        draft.value = data.content || ''
        lastSaved.value = data.timestamp || 0
        return true
      } catch (e) {
        console.error('Failed to parse draft:', e)
      }
    }
    return false
  }

  // 保存草稿
  function saveDraft(content: string) {
    const data = {
      content,
      timestamp: Date.now()
    }
    localStorage.setItem(key, JSON.stringify(data))
    lastSaved.value = data.timestamp
  }

  // 自动保存（每 30 秒）
  function startAutoSave(getContent: () => string) {
    setInterval(() => {
      const content = getContent()
      if (content.trim()) {
        saveDraft(content)
        ElMessage.success('草稿已自动保存')
      }
    }, 30000)
  }

  // 清除草稿
  function clearDraft() {
    localStorage.removeItem(key)
    draft.value = ''
    lastSaved.value = 0
  }

  return {
    draft,
    lastSaved,
    loadDraft,
    saveDraft,
    startAutoSave,
    clearDraft
  }
}
```

#### 3. 图片上传处理

```typescript
// api/upload.ts
import axios from 'axios'

export async function uploadImage(file: File): Promise<string> {
  const formData = new FormData()
  formData.append('file', file)

  const response = await axios.post('/api/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: (progressEvent) => {
      const percent = Math.round((progressEvent.loaded * 100) / (progressEvent.total || 1))
      console.log(`Upload progress: ${percent}%`)
    }
  })

  if (response.data.code === 200) {
    return response.data.data.url
  } else {
    throw new Error(response.data.message || 'Upload failed')
  }
}
```

#### 4. 分屏布局 CSS

```css
.editor-container {
  display: flex;
  gap: 16px;
  height: calc(100vh - 200px);
}

.editor-pane {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 8px;
}

.preview-pane {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .editor-container {
    flex-direction: column;
  }
  
  .preview-pane {
    display: none; /* 移动端隐藏预览区 */
  }
}
```

### 常见问题与解决方案

#### Q1: Tiptap 与 marked.js 如何协同工作？

**A:** Tiptap 负责编辑区的富文本编辑（内部使用 ProseMirror），marked.js 负责将 Markdown 文本渲染为 HTML（预览区）。两者独立工作，通过共享 Markdown 字符串同步内容。

#### Q2: 如何处理图片上传失败？

**A:** 
1. 捕获上传异常
2. 显示错误提示（ElMessage.error）
3. 移除编辑器中的占位符
4. 允许用户重新上传

#### Q3: 草稿冲突如何处理？

**A:** 
1. 检查 localStorage 中是否有旧草稿
2. 如果有，显示确认对话框："检测到未保存的草稿，是否恢复？"
3. 用户选择"是"则恢复，"否"则清除旧草稿
4. 每次保存新草稿时覆盖旧草稿

#### Q4: 如何实现代码块语法高亮？

**A:** 
1. 在预览区使用 marked.js 的 `highlight` 选项
2. 集成 highlight.js 自动检测语言
3. 添加对应的 CSS 主题（如 github.css）

```typescript
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

marked.setOptions({
  highlight: function(code, lang) {
    const language = hljs.getLanguage(lang) ? lang : 'plaintext'
    return hljs.highlight(code, { language }).value
  }
})
```

---

## 🔗 Dependencies

### 前置 Story

- ✅ Epic 1: 项目初始化与基础架构搭建（已完成）
- ✅ Epic 2: 用户认证与成员管理（已完成）
- ✅ Epic 3: 转正考核系统（已完成）
- ✅ Epic 7: 前端 UI 实现（已完成）

### 后续 Story

- ⏭️ Story 4.2: 资讯发布功能（依赖本 Story）
- ⏭️ Story 4.3: 博客发布功能（依赖本 Story）
- ⏭️ Story 4.4: 内容编辑与删除（依赖本 Story）

### 外部依赖

- 后端图片上传接口（需后端团队提供）
- 如果没有后端接口，可以先使用 base64 临时方案（P1 再改为上传）

---

## 📊 Success Metrics

### 功能完整性

- [ ] 所有 P0 功能实现并通过测试
- [ ] 无 TypeScript 编译错误
- [ ] 无 ESLint 警告
- [ ] 手动测试清单全部通过

### 性能指标

- [ ] 编辑器初始化时间 < 500ms
- [ ] 输入延迟 < 50ms（无明显卡顿）
- [ ] 图片上传成功率 > 95%
- [ ] 草稿保存成功率 100%

### 用户体验

- [ ] 界面美观，符合 CSDN 风格
- [ ] 操作流畅，无闪烁
- [ ] 错误提示清晰友好
- [ ] 响应式设计良好

---

## 🚀 Next Steps

完成本 Story 后：

1. **运行代码审查**
   ```bash
   /bmad:code-review
   ```

2. **更新 Sprint 状态**
   - 将 `4-1-markdown-editor-component` 状态改为 `done`

3. **开始下一个 Story**
   - Story 4.2: 资讯发布功能
   - 或 Story 4.3: 博客发布功能

---

## 📝 Dev Notes

**实施建议：**

1. **先搭建框架**：创建组件文件结构，安装依赖
2. **实现基础编辑**：先让 Tiptap 跑起来，能输入文本
3. **添加工具栏**：逐个实现工具栏按钮功能
4. **实现预览**：集成 marked.js，实现左右分屏
5. **草稿功能**：实现 localStorage 存储和恢复
6. **图片上传**：最后实现，可以先用 base64 占位
7. **优化样式**：调整布局、颜色、间距，参考 CSDN 设计
8. **测试验证**：按照测试清单逐项验证

**注意事项：**

- ⚠️ Tiptap 的学习曲线较陡，建议先看官方文档示例
- ⚠️ 图片上传需要后端配合，如果后端未完成，先用 mock 数据
- ⚠️ 移动端适配要单独测试，确保体验良好
- ⚠️ 草稿保存频率不要太高，避免性能问题

**参考资料：**

- Tiptap 官方文档：https://tiptap.dev/docs
- marked.js 文档：https://marked.js.org/
- highlight.js 文档：https://highlightjs.org/
- CSDN 编辑器参考：https://mp.csdn.net/mp_blog/creation/editor

---

**Status:** ready-for-dev  
**Last Updated:** 2026-04-09T10:50:00  
**Created By:** BMad Create Story Workflow
