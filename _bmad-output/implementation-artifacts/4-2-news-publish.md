---
story_id: "4.2"
epic_id: "4"
title: "资讯发布功能"
status: "ready-for-dev"
created: "2026-04-10"
last_updated: "2026-04-10T09:00:00"
---

# Story 4.2: 资讯发布功能

## 📋 User Story

**As a** 社团成员,  
**I want** 发布资讯,  
**So that** 可以分享社团新闻和活动。

---

## ✅ Acceptance Criteria

### AC1: 进入资讯编辑页面

**Given** 已登录账号  
**When** 点击发布按钮选择资讯  
**Then** 进入资讯编辑页面  
**And** 类型自动设为资讯

### AC2: 编辑资讯内容

**Given** 编辑资讯  
**When** 输入标题、摘要、内容  
**Then** 可以上传封面图  
**And** 摘要自动生成或手动编辑

### AC3: 提交审核

**Given** 点击发布  
**When** 提交审核  
**Then** 内容进入审核队列  
**And** 显示"等待审核"状态

---

## 🎯 Technical Requirements

### 功能需求

#### P0 - 必须实现（MVP）

1. **资讯发布页面**
   - 复用 Story 4.1 的 Markdown 编辑器组件
   - 添加资讯特有字段：封面图上传、摘要输入
   - 类型自动设置为 "news"
   - 表单验证：标题必填（5-100字符）、内容必填

2. **封面图上传**
   - 支持拖拽上传
   - 支持点击选择图片
   - 图片预览功能
   - 图片格式限制：jpg/png/gif/webp
   - 图片大小限制：最大 5MB
   - 上传进度显示

3. **摘要处理**
   - 默认从内容前 200 字符自动生成摘要
   - 允许用户手动编辑摘要
   - 摘要长度限制：最多 200 字符

4. **提交到后端**
   - 调用 POST /api/content 接口
   - 提交数据包含：title, summary, content, type="news", coverImage
   - 初始状态设置为 "pending"（审核中）
   - 成功后跳转到个人中心或提示发布成功

#### P1 - 后续迭代

- 草稿箱功能（保存为 draft 状态）
- 定时发布
- 标签选择
- 预览模式切换

### 文件结构

```
frontend/src/
├── views/
│   └── NewsPublishView.vue       # 资讯发布页面（P0）
├── api/
│   └── content.ts                # 内容相关 API（P0）
└── components/editor/
    └── BlogEditor.vue            # 复用 Story 4.1 的编辑器
```

### API 接口

#### 发布内容接口

**POST** `/api/content`

**Request:**
```typescript
interface PublishContentRequest {
  title: string          // 标题（必填，5-100字符）
  summary?: string       // 摘要（可选，最多200字符）
  content: string        // Markdown 内容（必填）
  type: 'news' | 'blog'  // 类型：news-资讯, blog-博客
  coverImage?: string    // 封面图 URL（可选）
  tags?: string[]        // 标签数组（可选）
}
```

**Response:**
```typescript
interface PublishContentResponse {
  code: number           // 200 成功
  message: string
  data: {
    id: number           // 内容 ID
    status: string       // pending-审核中
  }
}
```

**错误处理:**
- 400: 参数验证失败（标题太短、内容为空等）
- 401: 未登录
- 500: 服务器错误

#### 图片上传接口（复用 Story 4.1）

**POST** `/api/upload/image`

已在 Story 4.1 中实现，详见 `frontend/src/api/upload.ts`

---

## 🏗️ Architecture Compliance

### 前端架构规范

1. **组件设计原则**
   - 单一职责：NewsPublishView 只负责资讯发布流程
   - 复用 BlogEditor 组件，通过 props 传递配置
   - 使用 Composition API (`<script setup>`)
   - TypeScript 类型定义完整

2. **状态管理**
   - 表单状态使用 `ref` / `reactive` 本地管理
   - 不需要 Pinia（除非需要跨页面共享）
   - 封面图上传状态独立管理

3. **样式规范**
   - 使用 Tailwind CSS utility classes
   - 自定义样式写入 `<style scoped>`
   - 遵循项目设计系统规范
   - 响应式设计：PC 优先，移动端适配

4. **错误处理**
   - 所有异步操作必须有 try-catch
   - 用户友好的错误提示（ElMessage）
   - 表单验证错误实时显示

### 后端架构规范

1. **Controller 层**
   - 创建 ContentController.java
   - 实现 POST /api/content 接口
   - 使用 @Valid 进行参数验证
   - 从 JWT Token 获取当前用户 ID 作为 authorId

2. **Service 层**
   - 创建 ContentService 接口和 ContentServiceImpl 实现类
   - 业务逻辑：验证权限、设置默认值、保存到数据库
   - 初始状态设置为 "pending"
   - 如果未提供摘要，自动生成（截取内容前 200 字符）

3. **DTO 层**
   - 创建 PublishContentRequest DTO
   - 使用 Jakarta Validation 注解验证
   - 创建 ContentResponse DTO

4. **安全规范**
   - 接口需要认证（@PreAuthorize("hasRole('ROLE_MEMBER')")）
   - 防止 XSS：对内容进行转义或使用白名单过滤
   - 防止 SQL 注入：使用 MyBatis-Plus 参数化查询

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
   - 图片上传前压缩（可选，P1）
   - 防抖处理频繁事件（如摘要自动生成）
   - 懒加载非关键资源

---

## 📚 Library & Framework Requirements

### NPM 依赖

Story 4.1 已安装所需依赖，无需额外安装：
- ✅ marked（Markdown 解析）
- ✅ highlight.js（代码高亮）
- ✅ axios（HTTP 请求）

### 后端依赖

Spring Boot 项目已有依赖，无需额外添加：
- ✅ Spring Web
- ✅ Spring Security
- ✅ MyBatis-Plus
- ✅ Jakarta Validation

---

## 🧪 Testing Requirements

### 单元测试（可选，P1）

**前端测试文件位置：** `frontend/src/views/__tests__/NewsPublishView.spec.ts`

**测试用例：**
1. 页面正确渲染
2. 标题输入框可以输入文本
3. 封面图上传功能正常
4. 摘要自动生成逻辑正确
5. 表单验证正常工作
6. 提交成功后显示提示信息

**后端测试文件位置：** `backend/src/test/java/com/syit/cpc/service/ContentServiceTest.java`

**测试用例：**
1. 发布资讯成功
2. 标题为空时抛出异常
3. 内容为空时抛出异常
4. 未登录用户无法发布
5. 摘要自动生成逻辑正确

### 手动测试清单

**测试环境：**
- 浏览器：Chrome 最新版
- 分辨率：1920x1080（PC）、375x667（移动端）
- 后端服务运行在 http://localhost:8080

**测试步骤：**

1. **页面访问**
   - [ ] 登录账号（预备成员或正式成员）
   - [ ] 访问资讯发布页面（路由待配置）
   - [ ] 页面正确显示编辑器、标题输入框、封面图上传区

2. **表单验证**
   - [ ] 不输入标题，点击发布，显示错误提示
   - [ ] 标题少于 5 个字符，显示错误提示
   - [ ] 标题超过 100 个字符，自动截断或显示错误
   - [ ] 不输入内容，点击发布，显示错误提示

3. **封面图上传**
   - [ ] 点击上传按钮选择图片
   - [ ] 图片格式正确（jpg/png），上传成功
   - [ ] 图片格式错误（txt），显示错误提示
   - [ ] 图片超过 5MB，显示错误提示
   - [ ] 上传成功后显示图片预览
   - [ ] 可以删除已上传的图片

4. **摘要处理**
   - [ ] 输入内容后，摘要自动生成（前 200 字符）
   - [ ] 手动编辑摘要，覆盖自动生成的内容
   - [ ] 摘要超过 200 字符，自动截断或显示错误

5. **提交发布**
   - [ ] 填写完整表单，点击发布
   - [ ] 显示"发布中..."加载状态
   - [ ] 发布成功，显示"发布成功，等待审核"提示
   - [ ] 跳转到个人中心或首页
   - [ ] 数据库中记录状态为 "pending"

6. **响应式设计**
   - [ ] 缩小浏览器窗口到 768px 以下
   - [ ] 页面布局正常，无溢出
   - [ ] 编辑器、上传按钮可正常使用

---

## 📝 Implementation Notes

### 关键实现细节

#### 1. 前端：资讯发布页面

```vue
<!-- views/NewsPublishView.vue -->
<template>
  <div class="news-publish-page">
    <h1>发布资讯</h1>
    
    <!-- 封面图上传 -->
    <div class="cover-upload">
      <el-upload
        class="cover-uploader"
        action="/api/upload/image"
        :show-file-list="false"
        :on-success="handleCoverSuccess"
        :before-upload="beforeCoverUpload"
      >
        <img v-if="coverImage" :src="coverImage" class="cover-image" />
        <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
      </el-upload>
      <p class="upload-tip">支持 jpg/png/gif/webp，最大 5MB</p>
    </div>
    
    <!-- 标题输入 -->
    <el-input
      v-model="title"
      placeholder="请输入资讯标题（5-100个字）"
      maxlength="100"
      show-word-limit
    />
    
    <!-- 摘要输入 -->
    <el-input
      v-model="summary"
      type="textarea"
      :rows="3"
      placeholder="摘要（选填，最多200字，不填则自动生成）"
      maxlength="200"
      show-word-limit
    />
    
    <!-- Markdown 编辑器 -->
    <BlogEditor
      ref="editorRef"
      :initial-content="content"
      @change="handleContentChange"
    />
    
    <!-- 发布按钮 -->
    <div class="actions">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        发布资讯
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import BlogEditor from '@/components/editor/BlogEditor.vue'
import { publishContent } from '@/api/content'
import { uploadImage } from '@/api/upload'

const router = useRouter()
const editorRef = ref()

const title = ref('')
const summary = ref('')
const content = ref('')
const coverImage = ref('')
const submitting = ref(false)

// 自动生成摘要
function generateSummary(text: string): string {
  const plainText = text.replace(/[#*`\[\]()]/g, '').trim()
  return plainText.substring(0, 200)
}

// 监听内容变化，自动生成摘要
function handleContentChange(newContent: string) {
  content.value = newContent
  
  // 如果用户没有手动编辑摘要，则自动生成
  if (!summary.value.trim()) {
    summary.value = generateSummary(newContent)
  }
}

// 封面图上传前验证
function beforeCoverUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  return true
}

// 封面图上传成功
async function handleCoverSuccess(response: any, file: File) {
  try {
    const url = await uploadImage(file)
    coverImage.value = url
    ElMessage.success('封面图上传成功')
  } catch (error: any) {
    ElMessage.error(error.message || '上传失败')
  }
}

// 提交发布
async function handleSubmit() {
  // 表单验证
  if (title.value.length < 5) {
    ElMessage.warning('标题至少需要 5 个字')
    return
  }
  if (!content.value.trim()) {
    ElMessage.warning('请输入内容')
    return
  }
  
  submitting.value = true
  
  try {
    const response = await publishContent({
      title: title.value,
      summary: summary.value || generateSummary(content.value),
      content: content.value,
      type: 'news',
      coverImage: coverImage.value || undefined
    })
    
    if (response.code === 200) {
      ElMessage.success('发布成功，等待审核')
      router.push('/profile') // 跳转到个人中心
    } else {
      ElMessage.error(response.message || '发布失败')
    }
  } catch (error: any) {
    console.error('Publish failed:', error)
    ElMessage.error(error.message || '发布失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 取消
function handleCancel() {
  router.back()
}
</script>

<style scoped>
.news-publish-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.cover-upload {
  margin-bottom: 24px;
}

.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 300px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
```

#### 2. 前端：内容 API

```typescript
// api/content.ts
import request from './request'

export interface PublishContentRequest {
  title: string
  summary?: string
  content: string
  type: 'news' | 'blog'
  coverImage?: string
  tags?: string[]
}

export interface PublishContentResponse {
  code: number
  message: string
  data: {
    id: number
    status: string
  }
}

/**
 * 发布内容
 */
export async function publishContent(
  data: PublishContentRequest
): Promise<PublishContentResponse> {
  return request.post('/api/content', data)
}
```

#### 3. 后端：Content Controller

```java
// controller/ContentController.java
package com.syit.cpc.controller;

import com.syit.cpc.common.ApiResponse;
import com.syit.cpc.dto.PublishContentRequest;
import com.syit.cpc.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {
    
    private final ContentService contentService;
    
    /**
     * 发布内容
     */
    @PostMapping
    public ApiResponse<?> publishContent(
            @Valid @RequestBody PublishContentRequest request,
            Authentication authentication) {
        Long authorId = getCurrentUserId(authentication);
        return contentService.publishContent(request, authorId);
    }
    
    private Long getCurrentUserId(Authentication authentication) {
        // 从 JWT Token 中获取用户 ID
        // 实现略
        return 1L;
    }
}
```

#### 4. 后端：Content Service

```java
// service/ContentService.java
package com.syit.cpc.service;

import com.syit.cpc.common.ApiResponse;
import com.syit.cpc.dto.PublishContentRequest;

public interface ContentService {
    ApiResponse<?> publishContent(PublishContentRequest request, Long authorId);
}

// service/impl/ContentServiceImpl.java
package com.syit.cpc.service.impl;

import com.syit.cpc.common.ApiResponse;
import com.syit.cpc.dto.PublishContentRequest;
import com.syit.cpc.entity.Content;
import com.syit.cpc.mapper.ContentMapper;
import com.syit.cpc.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {
    
    private final ContentMapper contentMapper;
    
    @Override
    @Transactional
    public ApiResponse<?> publishContent(PublishContentRequest request, Long authorId) {
        Content content = new Content();
        content.setTitle(request.getTitle());
        content.setSummary(request.getSummary() != null ? 
            request.getSummary() : generateSummary(request.getContent()));
        content.setContent(request.getContent());
        content.setType(request.getType());
        content.setCoverImage(request.getCoverImage());
        content.setStatus("pending"); // 初始状态：审核中
        content.setAuthorId(authorId);
        content.setViewCount(0);
        content.setCreatedAt(LocalDateTime.now());
        content.setUpdatedAt(LocalDateTime.now());
        
        contentMapper.insert(content);
        
        return ApiResponse.success("发布成功", Map.of(
            "id", content.getId(),
            "status", content.getStatus()
        ));
    }
    
    /**
     * 自动生成摘要（截取内容前 200 字符）
     */
    private String generateSummary(String markdownContent) {
        // 移除 Markdown 标记
        String plainText = markdownContent.replaceAll("[#*`\\[\\]()]", "")
                                          .trim();
        // 截取前 200 字符
        return plainText.length() > 200 ? 
            plainText.substring(0, 200) : plainText;
    }
}
```

#### 5. 后端：DTO

```java
// dto/PublishContentRequest.java
package com.syit.cpc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PublishContentRequest {
    
    @NotBlank(message = "标题不能为空")
    @Size(min = 5, max = 100, message = "标题长度为 5-100 个字符")
    private String title;
    
    @Size(max = 200, message = "摘要最多 200 个字符")
    private String summary;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    @NotBlank(message = "类型不能为空")
    private String type; // news 或 blog
    
    private String coverImage;
    
    private List<String> tags;
}
```

### 常见问题与解决方案

#### Q1: 如何处理大图片上传？

**A:** 
1. 前端上传前压缩图片（使用 canvas）
2. 后端限制文件大小（Spring Boot 配置：`spring.servlet.multipart.max-file-size=5MB`）
3. 使用 CDN 存储图片（Phase 2）

#### Q2: 如何防止 XSS 攻击？

**A:** 
1. 后端使用 Jsoup 库清理 HTML 内容
2. 前端渲染时使用 `v-html` 要小心，确保内容已净化
3. 设置 Content-Security-Policy 响应头

#### Q3: 摘要自动生成的时机？

**A:** 
1. 前端实时生成：用户输入内容时，如果摘要为空则自动生成
2. 后端兜底：如果前端未传摘要，后端自动生成
3. 用户可以随时手动编辑摘要，覆盖自动生成的内容

#### Q4: 如何区分资讯和博客？

**A:** 
1. 通过 `type` 字段区分：`news` 或 `blog`
2. 资讯通常有封面图，博客更注重代码块
3. 前端根据类型显示不同的发布入口

---

## 🔗 Dependencies

### 前置 Story

- ✅ Story 4.1: Markdown 编辑器组件（已完成）
- ✅ Epic 1: 项目初始化与基础架构搭建（已完成）
- ✅ Epic 2: 用户认证与成员管理（已完成）

### 后续 Story

- ⏭️ Story 4.3: 博客发布功能（类似实现，可复用大部分代码）
- ⏭️ Story 4.4: 内容编辑与删除（依赖本 Story 的发布功能）
- ⏭️ Epic 5: 内容审核机制（审核本 Story 发布的内容）

### 外部依赖

- 后端图片上传接口（Story 4.1 已实现）
- 后端内容发布接口（本 Story 需实现）
- JWT 认证中间件（Epic 2 已实现）

---

## 📊 Success Metrics

### 功能完整性

- [ ] 所有 P0 功能实现并通过测试
- [ ] 无 TypeScript 编译错误
- [ ] 无 ESLint 警告
- [ ] 后端接口返回正确
- [ ] 手动测试清单全部通过

### 性能指标

- [ ] 页面加载时间 < 2 秒
- [ ] 图片上传成功率 > 95%
- [ ] 表单提交响应时间 < 1 秒
- [ ] 无内存泄漏

### 用户体验

- [ ] 界面美观，符合设计规范
- [ ] 操作流程顺畅，无卡顿
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
   - 将 `4-2-news-publish` 状态改为 `done`

3. **开始下一个 Story**
   - Story 4.3: 博客发布功能（可复用大部分代码）

---

## 📝 Dev Notes

**实施建议：**

1. **先实现后端接口**：创建 ContentController、ContentService、DTO
2. **再实现前端页面**：创建 NewsPublishView，复用 BlogEditor 组件
3. **集成测试**：前后端联调，确保数据流转正确
4. **优化体验**：添加加载状态、错误提示、表单验证

**注意事项：**

- ⚠️ 确保用户已登录才能访问发布页面（路由守卫）
- ⚠️ 封面图上传要限制格式和大小
- ⚠️ 摘要自动生成要考虑 Markdown 标记的去除
- ⚠️ 后端要进行参数验证，防止非法数据

**参考资料：**

- Element Plus Upload 组件：https://element-plus.org/zh-CN/component/upload.html
- Spring Boot 文件上传：https://spring.io/guides/gs/uploading-files/
- MyBatis-Plus 文档：https://baomidou.com/

---

**Status:** ready-for-dev  
**Last Updated:** 2026-04-10T09:00:00  
**Created By:** BMad Create Story Workflow
