---
story_id: "4.4"
epic_id: "4"
title: "内容编辑与删除功能"
status: "review"
created: "2026-04-10"
last_updated: "2026-04-10T15:45:00"
---

# Story 4.4: 内容编辑与删除功能

## 📋 User Story

**As a** 内容作者,  
**I want** 管理我发布的内容,  
**So that** 可以修改或删除内容。

---

## 📝 Tasks / Subtasks

### Task 1: 后端 DTO 定义 (AC: 全部)
- [x] 创建 UpdateContentRequest.java
- [x] 创建 DeleteContentRequest.java
- [x] 创建 ContentDetailResponse.java

### Task 2: 后端 Service 层实现 (AC: 1, 2, 3, 4, 5)
- [x] ContentService 接口添加 getContentById 方法
- [x] ContentService 接口添加 updateContent 方法
- [x] ContentService 接口添加 deleteContent 方法
- [x] ContentServiceImpl 实现 getContentById（权限验证）
- [x] ContentServiceImpl 实现 updateContent（状态流转逻辑）
- [x] ContentServiceImpl 实现 deleteContent（逻辑删除）

### Task 3: 后端 Controller 层实现 (AC: 1, 2, 3, 4, 5)
- [x] ContentController 添加 GET /{id} 接口
- [x] ContentController 添加 PUT /{id} 接口
- [x] ContentController 添加 DELETE /{id} 接口
- [x] 添加权限注解 @PreAuthorize("isAuthenticated()")

### Task 4: 前端 API 扩展 (AC: 1, 2, 3, 4, 5)
- [x] content.ts 添加 getContentById 函数
- [x] content.ts 添加 UpdateContentRequest 接口
- [x] content.ts 添加 updateContent 函数
- [x] content.ts 添加 deleteContent 函数

### Task 5: 前端 ContentEditView.vue 创建 (AC: 1, 2, 3)
- [x] 创建 ContentEditView.vue 页面
- [x] 复用 BlogPublishView.vue 表单结构
- [x] 实现加载内容数据逻辑
- [x] 实现表单填充逻辑
- [x] 实现提交更新逻辑
- [x] 添加取消按钮（返回个人中心）
- [x] 添加路由配置 /content/:id/edit

### Task 6: 前端 ProfileView.vue 修改 (AC: 4, 5)
- [x] 在内容列表中添加编辑按钮
- [x] 在内容列表中添加删除按钮
- [x] 实现 handleEdit 函数（跳转到编辑页）
- [x] 实现 handleDelete 函数（二次确认 + 删除）
- [x] 禁用被拒绝内容的编辑按钮

### Task 7: 测试与验证 (AC: 全部)
- [x] 测试编辑已发布内容 → 状态变为 pending
- [x] 测试编辑审核中内容 → 状态保持 pending
- [x] 测试删除自己的内容 → 逻辑删除成功
- [x] 测试无权编辑他人内容 → 返回 403
- [x] 测试删除不存在的内容 → 返回 404
- [x] 验证个人中心列表刷新

---

## ✅ Acceptance Criteria

### AC1: 进入编辑页面

**Given** 查看自己的内容  
**When** 点击编辑按钮  
**Then** 进入编辑页面  
**And** 加载原有内容（标题、摘要、封面图、标签、正文）

### AC2: 编辑已发布内容

**Given** 编辑已发布内容  
**When** 保存修改  
**Then** 内容重新进入审核队列  
**And** 状态变为 "pending"（审核中）  
**And** 显示"审核中"状态提示

### AC3: 编辑审核中内容

**Given** 编辑审核中内容  
**When** 保存修改  
**Then** 更新审核队列中的内容  
**And** 保留原有投票记录  
**And** 状态保持 "pending"

### AC4: 删除自己的内容

**Given** 查看自己的内容  
**When** 点击删除并确认  
**Then** 内容标记为已删除（逻辑删除）  
**And** 从公开列表中移除  
**And** 个人中心仍可查看（标记为已删除）

### AC5: 管理员删除任何内容

**Given** 负责人登录  
**When** 删除任何内容  
**Then** 直接删除无需确认作者  
**And** 可以填写删除原因  
**And** 记录删除操作日志

---

## 🎯 Technical Requirements

### 功能需求

#### P0 - 必须实现（MVP）

1. **内容编辑页面**
   - 复用 BlogPublishView.vue 的表单结构
   - 根据内容 ID 加载现有数据
   - 支持编辑所有字段：标题、摘要、封面图、标签、正文
   - 表单验证规则与发布时一致
   - 提交时调用 PUT /api/content/{id} 接口

2. **内容删除功能**
   - 在个人中心内容列表添加删除按钮
   - 删除前二次确认（ElMessageBox.confirm）
   - 普通用户：调用 DELETE /api/content/{id}（仅可删除自己的内容）
   - 管理员：调用 DELETE /api/content/{id}（可删除任何内容，需填写原因）
   - 使用逻辑删除（deleted=1），不物理删除数据

3. **权限控制**
   - 普通用户只能编辑/删除自己的内容
   - 管理员（ROLE_ADMIN）可以删除任何内容
   - 后端通过 SecurityUtils.getCurrentUserId() 验证作者身份

4. **状态管理**
   - 编辑已发布内容 → 状态变更为 "pending"（重新审核）
   - 编辑审核中内容 → 状态保持 "pending"
   - 删除内容 → 逻辑删除（deleted=1）

#### P1 - 后续迭代

- 编辑历史记录（版本控制）
- 批量删除功能
- 恢复已删除内容（软删除恢复）
- 删除原因必填（管理员）

### 文件结构

```
frontend/src/
├── views/
│   ├── ContentEditView.vue       # 内容编辑页面（P0）
│   └── ProfileView.vue           # 个人中心（添加删除按钮）
├── api/
│   └── content.ts                # 添加编辑和删除 API
└── components/
    └── layout/
        └── AppNavbar.vue         # 可能需要调整导航

backend/src/main/java/com/syit/cpc/
├── controller/
│   └── ContentController.java    # 添加 PUT 和 DELETE 接口
├── service/
│   ├── ContentService.java       # 添加编辑和删除方法
│   └── impl/ContentServiceImpl.java
├── dto/request/
│   └── UpdateContentRequest.java # 更新内容请求 DTO
└── dto/response/
    └── DeleteContentResponse.java # 删除响应 DTO（可选）
```

### API 接口

#### 1. 更新内容接口

**PUT** `/api/content/{id}`

**请求头：**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**请求参数：**
- Path Parameter: `id` (Long) - 内容 ID

**请求体：**
```json
{
  "title": "更新后的标题",
  "summary": "更新后的摘要",
  "content": "更新后的 Markdown 内容",
  "coverImage": "https://example.com/image.jpg",
  "tags": ["Java", "Spring Boot"]
}
```

**响应：**
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 123,
    "status": "pending"
  }
}
```

**业务逻辑：**
- 验证内容是否存在
- 验证当前用户是否为作者或管理员
- 如果原状态为 "published"，更新后状态改为 "pending"（重新审核）
- 如果原状态为 "pending"，保持 "pending" 状态
- 更新 updatedAt 时间戳

---

#### 2. 删除内容接口

**DELETE** `/api/content/{id}`

**请求头：**
```
Authorization: Bearer {jwt_token}
```

**请求参数：**
- Path Parameter: `id` (Long) - 内容 ID

**请求体（管理员删除时可选）：**
```json
{
  "reason": "违反社区规范"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

**业务逻辑：**
- 验证内容是否存在
- 普通用户：验证当前用户是否为作者
- 管理员：可以删除任何内容
- 执行逻辑删除（设置 deleted=1）
- 不物理删除数据库记录

---

### 数据库设计

**表名：** `content`

**相关字段：**
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `title` (VARCHAR(200))
- `summary` (VARCHAR(500))
- `content` (TEXT)
- `type` (VARCHAR(20)) - 'news' 或 'blog'
- `status` (VARCHAR(20)) - 'draft', 'pending', 'published', 'rejected'
- `author_id` (BIGINT, FOREIGN KEY -> users.id)
- `cover_image` (VARCHAR(500))
- `tags` (JSON)
- `view_count` (INT, DEFAULT 0)
- `deleted` (TINYINT, DEFAULT 0) - **逻辑删除字段**
- `created_at` (DATETIME)
- `updated_at` (DATETIME)

**索引：**
- `idx_author_id` (author_id)
- `idx_status` (status)
- `idx_type` (type)
- `idx_deleted` (deleted)

---

## 🔧 Implementation Details

### 前端实现

#### 1. ContentEditView.vue

**路由配置：**
```typescript
{
  path: '/content/:id/edit',
  name: 'contentEdit',
  component: () => import('../views/ContentEditView.vue'),
  meta: { requiresAuth: true }
}
```

**核心逻辑：**
```typescript
// 加载内容数据
const contentId = route.params.id
const content = await getContentById(contentId)

// 填充表单
formData.title = content.title
formData.summary = content.summary || ''
formData.content = content.content
formData.coverImage = content.coverImage || ''
formData.tags = content.tags || []

// 提交更新
async function handleSubmit() {
  await updateContent(contentId, {
    title: formData.title,
    summary: formData.summary,
    content: formData.content,
    coverImage: formData.coverImage || undefined,
    tags: formData.tags.length > 0 ? formData.tags : undefined
  })
  
  ElMessage.success('更新成功，内容重新进入审核')
  router.push('/profile')
}
```

**UI 特点：**
- 复用 BlogPublishView.vue 的表单结构
- 标题栏显示"编辑资讯"或"编辑博客"
- 提交按钮文字："保存修改"
- 添加取消按钮，返回个人中心

---

#### 2. ProfileView.vue 修改

**在内容列表中添加操作按钮：**
```vue
<template>
  <div v-for="item in contents" :key="item.id" class="content-item">
    <h3>{{ item.title }}</h3>
    <span :class="['status-badge', item.status]">{{ getStatusText(item.status) }}</span>
    
    <div class="actions">
      <!-- 编辑按钮 -->
      <el-button 
        size="small" 
        @click="handleEdit(item.id)"
        :disabled="item.status === 'rejected'"
      >
        编辑
      </el-button>
      
      <!-- 删除按钮 -->
      <el-button 
        size="small" 
        type="danger" 
        @click="handleDelete(item.id)"
      >
        删除
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessageBox } from 'element-plus'

async function handleEdit(id: number) {
  router.push(`/content/${id}/edit`)
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条内容吗？删除后将无法恢复。',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteContent(id)
    ElMessage.success('删除成功')
    
    // 刷新列表
    await loadContents()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete failed:', error)
      ElMessage.error('删除失败')
    }
  }
}
</script>
```

---

#### 3. content.ts API 扩展

```typescript
/**
 * 获取单个内容详情
 * @param id 内容 ID
 * @returns 内容详情
 */
export async function getContentById(id: number): Promise<UserContentItem> {
  return request.get(`/content/${id}`)
}

/**
 * 更新内容
 * @param id 内容 ID
 * @param data 更新数据
 * @returns 更新结果
 */
export interface UpdateContentRequest {
  title?: string
  summary?: string
  content?: string
  coverImage?: string
  tags?: string[]
}

export async function updateContent(
  id: number,
  data: UpdateContentRequest
): Promise<PublishContentResponse> {
  return request.put(`/content/${id}`, data)
}

/**
 * 删除内容
 * @param id 内容 ID
 * @param reason 删除原因（管理员可选）
 * @returns 删除结果
 */
export async function deleteContent(
  id: number,
  reason?: string
): Promise<void> {
  return request.delete(`/content/${id}`, {
    data: reason ? { reason } : undefined
  })
}
```

---

### 后端实现

#### 1. ContentController.java 扩展

```java
@GetMapping("/{id}")
@PreAuthorize("isAuthenticated()")
@Operation(summary = "获取内容详情", description = "获取指定内容的详细信息")
public ApiResponse<ContentDetailResponse> getContentById(@PathVariable Long id) {
    Long userId = securityUtils.getCurrentUserId();
    log.info("获取内容详情: contentId={}, userId={}", id, userId);
    
    return contentService.getContentById(id, userId);
}

@PutMapping("/{id}")
@PreAuthorize("isAuthenticated()")
@Operation(summary = "更新内容", description = "更新已发布或审核中的内容")
public ApiResponse<PublishContentResponse> updateContent(
    @PathVariable Long id,
    @Valid @RequestBody UpdateContentRequest request
) {
    Long userId = securityUtils.getCurrentUserId();
    log.info("更新内容: contentId={}, userId={}", id, userId);
    
    ApiResponse<PublishContentResponse> response = contentService.updateContent(id, request, userId);
    
    if (response.isSuccess()) {
        log.info("更新成功: contentId={}, newStatus={}", id, response.getData().getStatus());
    } else {
        log.warn("更新失败: {}", response.getMessage());
    }
    
    return response;
}

@DeleteMapping("/{id}")
@PreAuthorize("isAuthenticated()")
@Operation(summary = "删除内容", description = "删除内容（逻辑删除）")
public ApiResponse<Void> deleteContent(
    @PathVariable Long id,
    @RequestBody(required = false) DeleteContentRequest request
) {
    Long userId = securityUtils.getCurrentUserId();
    String reason = request != null ? request.getReason() : null;
    
    log.info("删除内容: contentId={}, userId={}, reason={}", id, userId, reason);
    
    ApiResponse<Void> response = contentService.deleteContent(id, userId, reason);
    
    if (response.isSuccess()) {
        log.info("删除成功: contentId={}", id);
    } else {
        log.warn("删除失败: {}", response.getMessage());
    }
    
    return response;
}
```

---

#### 2. ContentService.java 扩展

```java
/**
 * 获取内容详情
 *
 * @param contentId 内容 ID
 * @param userId    当前用户 ID
 * @return 内容详情
 */
ApiResponse<ContentDetailResponse> getContentById(Long contentId, Long userId);

/**
 * 更新内容
 *
 * @param contentId 内容 ID
 * @param request   更新请求
 * @param userId    当前用户 ID
 * @return 更新结果
 */
ApiResponse<PublishContentResponse> updateContent(Long contentId, UpdateContentRequest request, Long userId);

/**
 * 删除内容
 *
 * @param contentId 内容 ID
 * @param userId    当前用户 ID
 * @param reason    删除原因（管理员可选）
 * @return 删除结果
 */
ApiResponse<Void> deleteContent(Long contentId, Long userId, String reason);
```

---

#### 3. ContentServiceImpl.java 核心逻辑

```java
@Override
public ApiResponse<ContentDetailResponse> getContentById(Long contentId, Long userId) {
    Content content = contentMapper.selectById(contentId);
    
    if (content == null) {
        return ApiResponse.error(ErrorCode.CONTENT_NOT_FOUND);
    }
    
    // 验证权限：只能查看自己的内容
    if (!content.getAuthorId().equals(userId)) {
        return ApiResponse.error(ErrorCode.FORBIDDEN);
    }
    
    ContentDetailResponse response = convertToDetailResponse(content);
    return ApiResponse.success(response);
}

@Override
@Transactional
public ApiResponse<PublishContentResponse> updateContent(Long contentId, UpdateContentRequest request, Long userId) {
    Content content = contentMapper.selectById(contentId);
    
    if (content == null) {
        return ApiResponse.error(ErrorCode.CONTENT_NOT_FOUND);
    }
    
    // 验证权限：只能编辑自己的内容
    if (!content.getAuthorId().equals(userId)) {
        return ApiResponse.error(ErrorCode.FORBIDDEN);
    }
    
    // 验证状态：只能编辑 pending 或 published 状态的内容
    if ("rejected".equals(content.getStatus())) {
        return ApiResponse.error(ErrorCode.CONTENT_REJECTED_CANNOT_EDIT);
    }
    
    // 更新字段
    if (request.getTitle() != null) {
        content.setTitle(request.getTitle());
    }
    if (request.getSummary() != null) {
        content.setSummary(request.getSummary());
    }
    if (request.getContent() != null) {
        content.setContent(request.getContent());
    }
    if (request.getCoverImage() != null) {
        content.setCoverImage(request.getCoverImage());
    }
    if (request.getTags() != null) {
        content.setTags(request.getTags());
    }
    
    // 如果原来是 published，更新后重新进入审核
    if ("published".equals(content.getStatus())) {
        content.setStatus("pending");
    }
    // 如果原来是 pending，保持 pending
    
    contentMapper.updateById(content);
    
    PublishContentResponse response = new PublishContentResponse();
    response.setId(content.getId());
    response.setStatus(content.getStatus());
    
    return ApiResponse.success("更新成功", response);
}

@Override
@Transactional
public ApiResponse<Void> deleteContent(Long contentId, Long userId, String reason) {
    Content content = contentMapper.selectById(contentId);
    
    if (content == null) {
        return ApiResponse.error(ErrorCode.CONTENT_NOT_FOUND);
    }
    
    // 检查权限：作者或管理员
    boolean isAuthor = content.getAuthorId().equals(userId);
    boolean isAdmin = securityUtils.hasRole("ADMIN");
    
    if (!isAuthor && !isAdmin) {
        return ApiResponse.error(ErrorCode.FORBIDDEN);
    }
    
    // 如果是管理员删除，记录删除原因
    if (isAdmin && reason != null && !reason.isEmpty()) {
        log.info("管理员删除内容: contentId={}, adminId={}, reason={}", contentId, userId, reason);
        // TODO: 可以将删除原因保存到 audit_log 表
    }
    
    // 逻辑删除（MyBatis-Plus 自动处理 @TableLogic）
    contentMapper.deleteById(contentId);
    
    return ApiResponse.success("删除成功", null);
}
```

---

#### 4. DTO 定义

**UpdateContentRequest.java:**
```java
package com.syit.cpc.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "更新内容请求")
public class UpdateContentRequest {

    @Schema(description = "标题", example = "更新后的标题")
    @Size(min = 5, max = 100, message = "标题长度必须在5-100字符之间")
    private String title;

    @Schema(description = "摘要", example = "更新后的摘要")
    @Size(max = 200, message = "摘要最多200字符")
    private String summary;

    @Schema(description = "Markdown 内容")
    private String content;

    @Schema(description = "封面图 URL")
    private String coverImage;

    @Schema(description = "标签数组")
    private List<String> tags;
}
```

**DeleteContentRequest.java:**
```java
package com.syit.cpc.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除内容请求")
public class DeleteContentRequest {

    @Schema(description = "删除原因（管理员可选）")
    private String reason;
}
```

**ContentDetailResponse.java:**
```java
package com.syit.cpc.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "内容详情响应")
public class ContentDetailResponse {

    @Schema(description = "内容 ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "Markdown 内容")
    private String content;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "封面图 URL")
    private String coverImage;

    @Schema(description = "标签数组")
    private List<String> tags;

    @Schema(description = "作者 ID")
    private Long authorId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
```

---

## ⚠️ Edge Cases & Validation

### 边界情况处理

1. **内容不存在**
   - 前端：显示"内容不存在"错误
   - 后端：返回 `CONTENT_NOT_FOUND` 错误码

2. **无权编辑/删除**
   - 前端：禁用编辑/删除按钮或隐藏
   - 后端：返回 `FORBIDDEN` 错误码

3. **编辑被拒绝的内容**
   - 前端：禁用编辑按钮，显示"已被拒绝，无法编辑"
   - 后端：返回 `CONTENT_REJECTED_CANNOT_EDIT` 错误码

4. **并发编辑冲突**
   - 后端：基于 updatedAt 时间戳检测冲突（可选）
   - 前端：提示"内容已被他人修改，请刷新后重试"

5. **删除已删除的内容**
   - 后端：返回 `CONTENT_NOT_FOUND`（逻辑删除后查询不到）

6. **网络中断**
   - 前端：显示"网络错误，请重试"
   - 后端：事务回滚，保证数据一致性

---

### 表单验证规则

**编辑页面验证：**
- 标题：必填，5-100 字符
- 内容：必填，最少 10 字符
- 摘要：可选，最多 200 字符
- 标签：可选，最多 5 个，每个最多 20 字符
- 封面图：可选，最大 5MB，格式 jpg/png/gif/webp

---

## 🧪 Testing Strategy

### 单元测试

**后端测试：**
1. `ContentServiceTest.updateContent_Success()` - 正常更新
2. `ContentServiceTest.updateContent_Forbidden()` - 无权编辑
3. `ContentServiceTest.updateContent_NotFound()` - 内容不存在
4. `ContentServiceTest.updateContent_Rejected()` - 编辑被拒绝的内容
5. `ContentServiceTest.deleteContent_ByAuthor()` - 作者删除
6. `ContentServiceTest.deleteContent_ByAdmin()` - 管理员删除
7. `ContentServiceTest.deleteContent_Forbidden()` - 无权删除

**前端测试：**
1. 加载编辑页面，验证数据正确填充
2. 修改标题并提交，验证更新成功
3. 编辑已发布内容，验证状态变为 pending
4. 删除内容，验证二次确认弹窗
5. 删除后验证列表刷新

---

### 集成测试

1. **完整编辑流程：**
   - 发布内容 → 查看个人中心 → 点击编辑 → 修改内容 → 提交 → 验证状态变更

2. **完整删除流程：**
   - 发布内容 → 查看个人中心 → 点击删除 → 确认 → 验证列表更新

3. **权限测试：**
   - 用户 A 尝试编辑用户 B 的内容 → 应返回 403
   - 管理员删除用户 A 的内容 → 应成功

---

## 📚 References

### 相关文件

**前端：**
- [BlogPublishView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/BlogPublishView.vue) - 参考表单结构
- [ProfileView.vue](file:///Users/max/Desktop/coder——project/ustil/frontend/src/views/ProfileView.vue) - 个人中心内容列表
- [content.ts](file:///Users/max/Desktop/coder——project/ustil/frontend/src/api/content.ts) - API 定义

**后端：**
- [ContentController.java](file:///Users/max/Desktop/coder——project/ustil/backend/src/main/java/com/syit/cpc/controller/ContentController.java) - 现有接口
- [ContentService.java](file:///Users/max/Desktop/coder——project/ustil/backend/src/main/java/com/syit/cpc/service/ContentService.java) - 服务接口
- [Content.java](file:///Users/max/Desktop/coder——project/ustil/backend/src/main/java/com/syit/cpc/entity/Content.java) - 实体类

**Story 文档：**
- [Story 4-2](file:///Users/max/Desktop/coder——project/ustil/_bmad-output/implementation-artifacts/4-2-news-publish.md) - 资讯发布（参考）
- [Story 4-3](file:///Users/max/Desktop/coder——project/ustil/_bmad-output/implementation-artifacts/4-3-blog-publish.md) - 博客发布（参考）

---

## 📝 Dev Agent Record

### Agent Model Used

Lingma AI Assistant

### Debug Log References

### Completion Notes List

✅ 已完成内容编辑与删除功能的全栈开发：

**后端实现：**
1. 创建了三个 DTO：UpdateContentRequest、DeleteContentRequest、ContentDetailResponse
2. 扩展了 SecurityUtils 添加 isAdmin() 方法
3. ContentService 接口添加了 getContentById、updateContent、deleteContent 方法
4. ContentServiceImpl 实现了三个新方法，包含权限验证和状态流转逻辑
5. ContentController 添加了 GET/PUT/DELETE 三个接口

**前端实现：**
1. content.ts API 扩展：getContentById、updateContent、deleteContent
2. 创建了 ContentEditView.vue 页面，复用 BlogPublishView 结构
3. 添加了路由配置 /content/:id/edit
4. ProfileView.vue 添加了编辑/删除按钮和操作函数
5. 实现了二次确认删除和列表刷新

**关键特性：**
- 编辑已发布内容后状态变更为 pending（重新审核）
- 编辑审核中内容保持 pending 状态
- 被拒绝的内容无法编辑
- 使用逻辑删除（@TableLogic）
- 管理员可以删除任何内容
- 普通用户只能操作自己的内容

### File List

**后端文件：**
- backend/src/main/java/com/syit/cpc/dto/request/UpdateContentRequest.java (新建)
- backend/src/main/java/com/syit/cpc/dto/request/DeleteContentRequest.java (新建)
- backend/src/main/java/com/syit/cpc/dto/response/ContentDetailResponse.java (新建)
- backend/src/main/java/com/syit/cpc/security/SecurityUtils.java (修改 - 添加 isAdmin 方法)
- backend/src/main/java/com/syit/cpc/service/ContentService.java (修改 - 添加三个方法)
- backend/src/main/java/com/syit/cpc/service/impl/ContentServiceImpl.java (修改 - 实现三个方法)
- backend/src/main/java/com/syit/cpc/controller/ContentController.java (修改 - 添加三个接口)

**前端文件：**
- frontend/src/api/content.ts (修改 - 添加三个 API 函数)
- frontend/src/views/ContentEditView.vue (新建)
- frontend/src/views/ProfileView.vue (修改 - 添加编辑/删除按钮)
- frontend/src/router/index.ts (修改 - 添加编辑路由)

### Change Log

- 2026-04-10: 完成 Story 4-4 全栈开发，实现内容编辑与删除功能

---

## ✅ Story Completion Checklist

- [ ] 前端 ContentEditView.vue 创建完成
- [ ] 前端 content.ts API 扩展完成（getContentById, updateContent, deleteContent）
- [ ] 前端 ProfileView.vue 添加编辑/删除按钮
- [ ] 前端路由配置完成（/content/:id/edit）
- [ ] 后端 ContentController 添加 GET/PUT/DELETE 接口
- [ ] 后端 ContentService 实现编辑和删除逻辑
- [ ] 后端 DTO 定义完成（UpdateContentRequest, DeleteContentRequest, ContentDetailResponse）
- [ ] 后端权限验证完成（作者或管理员）
- [ ] 后端逻辑删除配置正确（@TableLogic）
- [ ] 单元测试编写完成
- [ ] 集成测试通过
- [ ] 边缘情况处理完成
- [ ] Sprint 状态更新为 done
