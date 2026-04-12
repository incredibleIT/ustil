---
story_id: "2.4"
story_key: "2-4-profile-management"
epic: "用户认证与成员管理"
status: done
created: "2026-04-03"
---

# Story 2.4: 个人资料管理

## Story

As a 社团成员,
I want 管理我的个人资料,
So that 可以展示我的信息。

## Acceptance Criteria

### AC1: 查看个人信息
**Given** 已登录账号
**When** 访问个人设置页面
**Then** 显示当前用户信息（头像、昵称、邮箱、个人简介、角色）
**And** 显示账号状态（启用/禁用）
**And** 显示加入时间

### AC2: 修改昵称和个人简介
**Given** 已登录账号
**When** 在个人设置页面修改昵称或简介
**Then** 表单验证（昵称 2-20 字符，简介不超过 200 字符）
**And** 提交成功后更新数据库
**And** 显示成功提示"个人信息已更新"
**And** 页面显示更新后的信息

### AC3: 修改密码
**Given** 已登录账号
**When** 在个人设置页面选择修改密码
**Then** 需要输入旧密码、新密码、确认新密码
**And** 旧密码验证通过（调用后端验证）
**And** 新密码强度要求（至少 8 位，包含字母和数字）
**And** 两次新密码必须一致
**And** 修改成功后显示提示"密码修改成功，请重新登录"
**And** 自动退出登录（Token 失效）

### AC4: 查看发布历史
**Given** 已登录账号
**When** 访问个人中心"我的内容"标签页
**Then** 显示我发布的所有内容列表（标题、类型、状态、发布时间）
**And** 支持按类型筛选（资讯/博客）
**And** 支持按状态筛选（草稿/审核中/已发布/已拒绝）
**And** 分页展示（每页 10 条）
**And** 点击内容可跳转至编辑或详情页

### AC5: 路由守卫保护
**Given** 未登录用户
**When** 尝试访问 /profile 页面
**Then** 自动重定向到 /login 页面
**And** 显示提示"请先登录"

### AC6: 已登录用户访问登录页
**Given** 已登录用户
**When** 访问 /login 或 /register 页面
**Then** 自动重定向到 / 首页
**And** 可选显示提示"您已登录"

## Tasks / Subtasks

### 后端任务

- [x] Task 1: 创建用户信息响应 DTO (AC: #1)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/dto/response/UserProfileResponse.java`
  - [x] 包含字段：userId, studentId, username, email, role, avatar, bio, status, createdAt
  - [x] 添加 Swagger 注解（@Schema）

- [x] Task 2: 创建修改个人信息请求 DTO (AC: #2)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/dto/request/UpdateProfileRequest.java`
  - [x] 包含字段：username（可选）, bio（可选）
  - [x] 添加校验注解（@Size, @Pattern）
  - [x] 添加 Swagger 注解

- [x] Task 3: 创建修改密码请求 DTO (AC: #3)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/dto/request/ChangePasswordRequest.java`
  - [x] 包含字段：oldPassword, newPassword, confirmPassword
  - [x] 添加校验注解（@NotBlank, @Size, @Pattern）
  - [x] 添加自定义校验注解验证两次密码一致

- [x] Task 4: 扩展 UserService 添加个人信息管理方法 (AC: #1, #2, #3)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/service/UserService.java` 接口
  - [x] 创建 `backend/src/main/java/com/syit/cpc/service/impl/UserServiceImpl.java` 实现类
  - [x] 实现 getUserProfile(Long userId) 方法
  - [x] 实现 updateProfile(Long userId, UpdateProfileRequest request) 方法
  - [x] 实现 changePassword(Long userId, ChangePasswordRequest request) 方法
  - [x] 注入 UserMapper, PasswordUtil
  - [x] 使用 @Transactional 注解
  - [x] 修改密码逻辑：验证旧密码 → 加密新密码 → 更新数据库

- [x] Task 5: 创建 ProfileController (AC: #1, #2, #3)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/controller/ProfileController.java`
  - [x] 添加 GET /api/profile 接口 - 获取当前用户信息
  - [x] 添加 PUT /api/profile 接口 - 修改个人信息
  - [x] 添加 PUT /api/profile/password 接口 - 修改密码
  - [x] 所有接口需要认证（@PreAuthorize("isAuthenticated()")）
  - [x] 从 SecurityUtils 获取当前用户 ID
  - [x] 添加 Swagger 注解

- [x] Task 6: 添加路由守卫配置 (AC: #5, #6)
  - [x] 无需后端改动，由前端路由守卫实现

### 前端任务

- [x] Task 7: 实现路由守卫 (AC: #5, #6)
  - [x] 修改 `frontend/src/router/index.ts`
  - [x] 导入 auth store
  - [x] 在 beforeEach 中检查 isAuthenticated
  - [x] 未登录用户访问 /profile 重定向到 /login
  - [x] 已登录用户访问 /login 或 /register 重定向到 /
  - [x] 公开路由（/, /login, /register）无需认证

- [x] Task 8: 创建个人资料页面组件 (AC: #1, #2)
  - [x] 创建 `frontend/src/views/ProfileView.vue`
  - [x] 使用 Element Plus 布局（el-card, el-form, el-input）
  - [x] 显示用户信息表单（头像、昵称、邮箱、简介、角色）
  - [x] 邮箱和角色字段只读
  - [x] 昵称和简介可编辑
  - [x] 添加"保存"按钮
  - [x] 表单验证规则
  - [x] 调用后端 API 获取和更新信息

- [x] Task 9: 创建修改密码组件 (AC: #3)
  - [x] 在 ProfileView.vue 中添加"修改密码"标签页（el-tabs）
  - [x] 创建密码修改表单（旧密码、新密码、确认密码）
  - [x] 表单验证（旧密码必填、新密码强度、两次密码一致）
  - [x] 调用后端 API 修改密码
  - [x] 成功后退出登录并跳转到 /login

- [x] Task 10: 创建"我的内容"标签页 (AC: #4)
  - [x] 在 ProfileView.vue 中添加"我的内容"标签页
  - [x] 显示内容列表（标题、类型、状态、时间）
  - [x] 添加筛选器（类型、状态）
  - [x] 添加分页组件（el-pagination）
  - [x] 点击内容跳转至详情或编辑页
  - [x] 调用后端 API 获取用户发布的内容（MVP 阶段可先返回空列表）

- [x] Task 11: 创建用户 API 模块 (AC: #1, #2, #3)
  - [x] 创建 `frontend/src/api/profile.ts`
  - [x] 添加 getProfile() 方法
  - [x] 添加 updateProfile(data) 方法
  - [x] 添加 changePassword(data) 方法
  - [x] 使用统一的 request 实例

- [x] Task 12: 更新首页动态按钮 (AC: #6)
  - [x] 修改 `frontend/src/views/HomeView.vue`
  - [x] 导入 auth store
  - [x] 根据 isAuthenticated 显示不同按钮
  - [x] 未登录：显示"加入我们"和"登录"
  - [x] 已登录：显示"个人资料"和"退出登录"
  - [x] 点击"个人资料"跳转到 /profile
  - [x] 点击"退出登录"调用 authStore.logout() 并刷新页面

## Dev Notes

### 架构模式与约束

**后端架构**:
- 使用 Spring Boot 3.5.0 + MyBatis-Plus 3.5.9
- Controller → Service → Mapper 三层架构
- 统一响应格式：`ApiResponse<T>` (code, message, data)
- 全局异常处理：`GlobalExceptionHandler` + `BusinessException`
- JWT 认证：通过 `JwtAuthenticationFilter` 提取用户 ID
- 密码加密：使用 Spring Security 的 `BCryptPasswordEncoder`

**前端架构**:
- Vue 3 + TypeScript + Vite
- Element Plus 2.5+ 组件库
- Pinia 状态管理（auth store 已存在）
- Axios 拦截器处理 Token 和 401 自动刷新

### 源树组件

**后端需要修改/创建的文件**:
```
backend/src/main/java/com/syit/cpc/
├── dto/
│   ├── request/
│   │   ├── UpdateProfileRequest.java (新建)
│   │   └── ChangePasswordRequest.java (新建)
│   └── response/
│       └── UserProfileResponse.java (新建)
├── service/
│   ├── UserService.java (新建)
│   └── impl/
│       └── UserServiceImpl.java (新建)
└── controller/
    └── ProfileController.java (新建)
```

**前端需要修改/创建的文件**:
```
frontend/src/
├── router/
│   └── index.ts (修改 - 添加路由守卫)
├── views/
│   ├── ProfileView.vue (新建)
│   └── HomeView.vue (修改 - 动态按钮)
└── api/
    └── profile.ts (新建)
```

### 技术要点

1. **获取当前用户 ID**:
   - 后端从 `SecurityContextHolder.getContext().getAuthentication()` 获取
   - JWT Token 中包含 userId，已在 `JwtAuthenticationFilter` 中设置到 SecurityContext

2. **密码验证**:
   - 使用 `PasswordEncoder.matches(rawPassword, encodedPassword)` 验证旧密码
   - 新密码使用 `PasswordEncoder.encode()` 加密后存储

3. **表单验证**:
   - 后端使用 Bean Validation（@Valid, @NotBlank, @Size 等）
   - 前端使用 Element Plus 表单验证规则

4. **路由守卫**:
   - 使用 `router.beforeEach` 实现
   - 检查 `authStore.isAuthenticated`
   - 注意避免循环重定向

### 测试标准

- 后端：手动测试 API 接口（Swagger UI 或 Postman）
- 前端：手动测试页面功能和表单验证
- 集成测试：验证前后端联调（注册 → 登录 → 查看/修改资料 → 修改密码）

### 参考资料

- [Source: epics.md#Story 2.4: 个人信息管理](file:///Users/max/Desktop/coder——project/ustil/_bmad-output/planning-artifacts/epics.md#L311-L332)
- [Source: architecture.md#API Patterns](file:///Users/max/Desktop/coder——project/ustil/docs/architecture.md#L200-L250)
- [Source: 2-3-user-login.md](file:///Users/max/Desktop/coder——project/ustil/_bmad-output/implementation-artifacts/2-3-user-login.md) - 参考登录功能的实现模式
- [Source: 2-2-user-registration.md](file:///Users/max/Desktop/coder——project/ustil/_bmad-output/implementation-artifacts/2-2-user-registration.md) - 参考注册功能的 DTO 和验证模式

## Dev Agent Record

### Agent Model Used

Qwen Coder (Claude-like)

### Debug Log References

- 无特殊调试问题

### Completion Notes List

- ✅ 已完成所有后端任务（Task 1-6）
- ✅ 已完成所有前端任务（Task 7-12）
- 后端创建了 UserProfileResponse、UpdateProfileRequest、ChangePasswordRequest DTO
- 后端创建了 UserService 接口和实现类
- 后端创建了 ProfileController 与三个 API 接口
- 后端创建了 SecurityUtils 工具类用于从 JWT Token 中获取 userId
- 后端扩展了 ErrorCode 枚举添加了 OLD_PASSWORD_ERROR 和 PASSWORD_NOT_MATCH_CONFIRM
- 前端实现了路由守卫，保护需要认证的页面
- 前端创建了 ProfileView.vue 包含个人信息、修改密码、我的内容三个标签页
- 前端创建了 profile.ts API 模块
- 前端更新了 HomeView.vue 实现动态按钮显示

### File List

- `backend/src/main/java/com/syit/cpc/dto/response/UserProfileResponse.java` (新建)
- `backend/src/main/java/com/syit/cpc/dto/request/UpdateProfileRequest.java` (新建)
- `backend/src/main/java/com/syit/cpc/dto/request/ChangePasswordRequest.java` (新建)
- `backend/src/main/java/com/syit/cpc/service/UserService.java` (新建)
- `backend/src/main/java/com/syit/cpc/service/impl/UserServiceImpl.java` (新建)
- `backend/src/main/java/com/syit/cpc/controller/ProfileController.java` (新建)
- `backend/src/main/java/com/syit/cpc/security/SecurityUtils.java` (新建)
- `backend/src/main/java/com/syit/cpc/security/JwtAuthenticationFilter.java` (修改 - 使用 userId 作为 principal)
- `backend/src/main/java/com/syit/cpc/common/validation/PasswordMatch.java` (新建)
- `backend/src/main/java/com/syit/cpc/common/validation/PasswordMatchValidator.java` (新建)
- `backend/src/main/java/com/syit/cpc/common/exception/ErrorCode.java` (修改 - 添加新错误码)
- `frontend/src/router/index.ts` (修改 - 添加路由守卫)
- `frontend/src/views/ProfileView.vue` (新建)
- `frontend/src/api/profile.ts` (新建)
- `frontend/src/views/HomeView.vue` (修改 - 动态按钮)

### Change Log

#### 初始开发 (2026-04-03)
- 完成 Story 2.4 个人资料管理功能的全栈开发
- 后端：3 个 DTO、UserService、ProfileController、SecurityUtils
- 前端：路由守卫、ProfileView.vue（326 行）、profile.ts API 模块、HomeView 动态按钮

#### 代码审查修复 (2026-04-03)

**高优先级修复**:
1. **H1 - 优化 SecurityUtils 避免重复解析 JWT** ✅
   - 在 JwtAuthenticationFilter 中将 userId 存储为 Authentication.principal
   - SecurityUtils 直接从 Authentication.getName() 获取 userId
   - 移除 SecurityUtils 对 JwtTokenProvider 的依赖

2. **H2 - 使用自定义校验注解验证密码一致性** ✅
   - 创建 @PasswordMatch 自定义校验注解
   - 创建 PasswordMatchValidator 反射实现字段比较
   - 从 UserServiceImpl 移除密码一致性验证代码

**中优先级修复**:
3. **M3 - 优化路由守卫的 restoreFromStorage 调用** ✅
   - 仅在未认证且无 token 时调用 restoreFromStorage
   - 减少不必要的 localStorage 读取操作

**编译验证**:
- 后端: ✅ `mvn clean compile` 成功（40 文件）
- 前端: ✅ 构建成功（1685 模块）

## Story Completion Status

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->
