---
story_id: "2.5"
story_key: "2-5-member-list-management"
epic: "用户认证与成员管理"
status: done
created: "2026-04-03"
---

# Story 2.5: 成员列表与管理（负责人）

## Story

As a 负责人,
I want 管理所有社团成员,
So that 可以维护成员信息和控制权限。

## Acceptance Criteria

### AC1: 查看成员列表
**Given** 负责人登录
**When** 访问成员管理页面
**Then** 显示所有成员列表（头像、学号、姓名、角色、加入时间、账号状态）
**And** 列表支持分页（默认每页 10 条）
**And** 默认按加入时间倒序排列

### AC2: 按角色筛选成员
**Given** 在成员列表页面
**When** 选择角色筛选条件（全部/预备成员/正式成员/负责人）
**Then** 列表实时过滤显示对应角色的成员
**And** 显示筛选后的成员总数

### AC3: 搜索成员
**Given** 在成员列表页面
**When** 输入搜索关键词（学号或姓名）
**Then** 实时过滤显示匹配的成员
**And** 支持模糊搜索
**And** 搜索框为空时显示全部成员

### AC4: 修改成员角色
**Given** 负责人查看成员列表
**When** 点击修改某成员角色
**Then** 弹出角色选择对话框（预备成员/正式成员/负责人）
**And** 确认后调用后端 API 更新角色
**And** 显示成功提示"角色已更新"
**And** 列表自动刷新显示新角色

### AC5: 禁用/启用成员账号
**Given** 负责人查看成员列表
**When** 点击禁用或启用某成员账号
**Then** 弹出确认对话框（"确定要禁用/启用该成员吗？"）
**And** 确认后调用后端 API 更新状态
**And** 显示成功提示"账号已禁用/启用"
**And** 列表自动刷新显示新状态
**And** 被禁用的成员无法登录系统

### AC6: 权限控制
**Given** 非负责人用户登录
**When** 尝试访问成员管理页面
**Then** 拒绝访问并显示"权限不足"提示
**And** 重定向到首页

## Tasks / Subtasks

### Task 1: 后端 - 成员列表查询接口 (AC: 1, 2, 3)
- [x] 1.1 创建 MemberListResponse DTO（包含分页信息）
- [x] 1.2 在 UserService 中添加分页查询成员方法（支持角色筛选、关键词搜索）
- [x] 1.3 创建 AdminController 添加 GET /api/admin/members 接口
- [x] 1.4 添加 @PreAuthorize("hasRole('ADMIN')") 权限控制
- [x] 1.5 使用 MyBatis-Plus 的 Page 对象实现分页
- [x] 1.6 使用 LambdaQueryWrapper 实现动态条件查询（role、keyword）

### Task 2: 后端 - 修改成员角色接口 (AC: 4)
- [x] 2.1 创建 UpdateUserRoleRequest DTO
- [x] 2.2 在 UserService 中添加更新角色方法
- [x] 2.3 在 AdminController 添加 PUT /api/admin/members/{userId}/role 接口
- [x] 2.4 添加权限校验（负责人权限）
- [x] 2.5 添加角色值校验（只能是 ROLE_PROBATION/ROLE_MEMBER/ROLE_ADMIN）
- [x] 2.6 添加操作日志记录

### Task 3: 后端 - 禁用/启用成员账号接口 (AC: 5)
- [x] 3.1 创建 UpdateUserStatusRequest DTO
- [x] 3.2 在 UserService 中添加更新状态方法
- [x] 3.3 在 AdminController 添加 PUT /api/admin/members/{userId}/status 接口
- [x] 3.4 添加权限校验（负责人权限）
- [x] 3.5 添加状态值校验（0-禁用，1-启用）
- [x] 3.6 添加操作日志记录

### Task 4: 前端 - 路由和权限守卫 (AC: 6)
- [x] 4.1 在 router/index.ts 中添加 /admin/members 路由
- [x] 4.2 添加 meta.requiresAuth = true
- [x] 4.3 添加 meta.requiresRole = 'ADMIN'
- [x] 4.4 在路由守卫中添加角色权限检查
- [x] 4.5 无权限用户重定向到首页并显示提示

### Task 5: 前端 - 成员列表页面 (AC: 1, 2, 3)
- [x] 5.1 创建 MembersView.vue 页面组件
- [x] 5.2 使用 Element Plus Table 组件展示成员列表
- [x] 5.3 实现分页功能（el-pagination）
- [x] 5.4 实现角色筛选下拉框（el-select）
- [x] 5.5 实现搜索框（el-input with search icon）
- [x] 5.6 添加加载状态和空状态提示
- [x] 5.7 调用后端 API 获取成员列表

### Task 6: 前端 - 成员操作功能 (AC: 4, 5)
- [x] 6.1 添加"修改角色"按钮和对话框（el-dialog + el-select）
- [x] 6.2 添加"禁用/启用"按钮和确认对话框（ElMessageBox.confirm）
- [x] 6.3 实现操作成功后的列表刷新
- [x] 6.4 添加操作成功/失败提示（ElMessage）
- [x] 6.5 添加操作权限控制（非负责人不显示操作按钮）

### Task 7: 前端 - API 模块和状态管理 (AC: 1-5)
- [x] 7.1 创建 frontend/src/api/member.ts API 模块
- [x] 7.2 封装成员列表查询、角色更新、状态更新 API
- [x] 7.3 在 frontend/src/stores/user.ts 中添加成员管理相关状态（可选）
- [x] 7.4 添加请求拦截和错误处理

## Dev Notes

### 技术要点

**后端实现**:
- 使用 MyBatis-Plus 的 `Page<T>` 和 `IPage<T>` 实现分页
- 使用 `LambdaQueryWrapper<User>` 构建动态查询条件
- 角色枚举值：`ROLE_PROBATION`（预备成员）、`ROLE_MEMBER`（正式成员）、`ROLE_ADMIN`（负责人）
- 状态字段：`0`-禁用，`1`-启用
- 参考已有代码：Story 2.4 的 ProfileController 和 UserServiceImpl

**前端实现**:
- 使用 Element Plus Table + Pagination 组件
- 使用 Element Plus Dialog + MessageBox 实现操作确认
- 使用 Vue Router 路由守卫实现权限控制
- 参考已有代码：Story 2.4 的 ProfileView.vue 和 router/index.ts

### 权限控制策略

**后端**:
```java
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin/members")
public class AdminController {
    // 所有接口都需要 ADMIN 角色
}
```

**前端**:
```typescript
// 路由配置
{
  path: '/admin/members',
  name: 'admin-members',
  component: () => import('../views/MembersView.vue'),
  meta: { requiresAuth: true, requiresRole: 'ADMIN' }
}

// 路由守卫
if (to.meta.requiresRole && authStore.user?.role !== to.meta.requiresRole) {
  ElMessage.error('权限不足')
  next({ name: 'home' })
  return
}
```

### 数据库查询示例

**分页查询成员**:
```java
Page<User> page = new Page<>(current, size);
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

// 角色筛选
if (role != null) {
    wrapper.eq(User::getRole, role);
}

// 关键词搜索（学号或姓名）
if (keyword != null && !keyword.isBlank()) {
    wrapper.and(w -> w
        .like(User::getStudentId, keyword)
        .or()
        .like(User::getUsername, keyword)
    );
}

// 按加入时间倒序
wrapper.orderByDesc(User::getCreatedAt);

userMapper.selectPage(page, wrapper);
```

### API 接口设计

**1. 获取成员列表**
```
GET /api/admin/members?page=1&size=10&role=ROLE_MEMBER&keyword=张三
```

Response:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "userId": 1,
        "studentId": "2021001",
        "username": "张三",
        "email": "zhangsan@example.com",
        "role": "ROLE_MEMBER",
        "avatar": null,
        "status": 1,
        "createdAt": "2026-04-01T10:00:00"
      }
    ],
    "total": 50,
    "current": 1,
    "size": 10,
    "pages": 5
  }
}
```

**2. 修改成员角色**
```
PUT /api/admin/members/{userId}/role
Body: { "role": "ROLE_MEMBER" }
```

**3. 更新成员状态**
```
PUT /api/admin/members/{userId}/status
Body: { "status": 0 }
```

### 错误码扩展

需要在 `ErrorCode.java` 中添加：
```java
INVALID_ROLE("无效的角色值"),
USER_STATUS_UPDATE_FAILED("用户状态更新失败"),
```

### Project Structure Notes

**后端新增文件**:
- `backend/src/main/java/com/syit/cpc/controller/AdminController.java` - 管理员控制器
- `backend/src/main/java/com/syit/cpc/dto/request/UpdateUserRoleRequest.java` - 更新角色请求
- `backend/src/main/java/com/syit/cpc/dto/request/UpdateUserStatusRequest.java` - 更新状态请求
- `backend/src/main/java/com/syit/cpc/dto/response/MemberListResponse.java` - 成员列表响应

**后端修改文件**:
- `backend/src/main/java/com/syit/cpc/service/UserService.java` - 添加成员管理方法
- `backend/src/main/java/com/syit/cpc/service/impl/UserServiceImpl.java` - 实现成员管理方法
- `backend/src/main/java/com/syit/cpc/common/exception/ErrorCode.java` - 添加新错误码

**前端新增文件**:
- `frontend/src/views/MembersView.vue` - 成员列表页面
- `frontend/src/api/member.ts` - 成员管理 API 模块

**前端修改文件**:
- `frontend/src/router/index.ts` - 添加路由和权限守卫
- `frontend/src/stores/user.ts` - 添加成员管理状态（可选）

### References

- [Source: epics.md#Story 2.5] - 原始 Story 定义和 Acceptance Criteria
- [Source: docs/architecture.md#架构边界] - API 边界定义（管理员 API）
- [Source: docs/architecture.md#需求到结构映射] - 成员管理模块映射
- [Source: 2-4-profile-management.md] - 参考 UserService 和 SecurityUtils 实现模式
- [Source: frontend/src/router/index.ts] - 参考路由守卫实现

### Testing Standards

**后端测试**:
- 使用 JUnit 5 + Mockito 编写单元测试
- 测试分页查询的不同参数组合
- 测试权限控制（非 ADMIN 角色访问应被拒绝）
- 测试角色值校验和状态值校验

**前端测试**:
- 测试页面加载和数据显示
- 测试筛选和搜索功能
- 测试权限守卫（非 ADMIN 用户访问应被重定向）
- 测试操作成功/失败的用户提示

## Dev Agent Record

### Agent Choice Validation

#### Chosen Stack
- **Backend**: Java 17, Spring Boot 3.5.0, MyBatis-Plus 3.5.9, MySQL 8.0
- **Frontend**: Vue.js 3.5.22, TypeScript, Vite 7.1.5, Element Plus, Axios
- **State Management**: Pinia 3.0.3
- **Routing**: Vue Router 4.6.3
- **Validation**: Bean Validation (Hibernate Validator)

#### Choice Validation
- 创建独立的 AdminController：遵循单一职责原则，将管理员接口与普通用户接口分离
- 使用 MyBatis-Plus Page 对象：项目统一使用 MyBatis-Plus 分页插件，保持一致性
- 使用路由守卫进行前端权限控制：与 Story 2.4 的认证守卫保持一致
- 非负责人用户重定向到首页：避免显示无权限页面，提升用户体验

### Change Log
| Change | Description | Reason | Impact |
|--------|-------------|--------|--------|
| 创建 AdminController | 管理员 REST API 控制器 | 提供成员管理接口（列表、角色、状态） | 新增 3 个 API 端点 |
| 创建 MemberListResponse | 成员列表响应 DTO | 分页数据返回 | 包含 records 和分页信息 |
| 创建 UpdateUserRoleRequest | 更新角色请求 DTO | 修改成员角色 | Bean Validation 校验 |
| 创建 UpdateUserStatusRequest | 更新状态请求 DTO | 禁用/启用账号 | Bean Validation 校验 |
| 修改 UserService 接口 | 添加 3 个成员管理方法 | 扩展服务层功能 | getMemberList, updateUserRole, updateUserStatus |
| 修改 UserServiceImpl | 实现成员管理业务逻辑 | 分页查询、角色校验、状态校验 | 使用 LambdaQueryWrapper 动态查询 |
| 修改 ErrorCode | 添加 INVALID_ROLE, USER_STATUS_UPDATE_FAILED | 支持成员管理业务 | ErrorCode 枚举扩展 |
| 创建路由守卫 | 角色权限检查 | 非 ADMIN 用户禁止访问 | router/index.ts 添加 requiresRole 检查 |
| 创建 MembersView.vue | 成员列表页面 | 341 行组件，含筛选、搜索、分页、操作 | Element Plus Table + Dialog |
| 创建 member.ts API 模块 | 封装成员管理 API 调用 | 统一接口调用，添加成功提示 | 3 个 API 方法 |
| 创建 member.ts 类型文件 | TypeScript 类型定义 | 类型安全 | MemberInfo, MemberListResponse 等 |

### Additional Details

#### 编译验证
- 后端: ✅ `mvn compile` 成功（44 文件）
- 前端: ✅ `npm run build` 成功（1713 modules）

## Story Completion Status

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->
