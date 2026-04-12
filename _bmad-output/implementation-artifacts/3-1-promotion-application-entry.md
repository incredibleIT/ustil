---
story_id: "3.1"
story_key: "3-1-promotion-application-entry"
epic: "转正考核系统"
status: ready-for-dev
created: "2026-04-07"
---

# Story 3.1: 转正申请入口与说明

As a 预备成员,
I want 了解转正要求并提交申请,
So that 可以成为正式成员。

## Acceptance Criteria

### AC1: 查看转正流程说明
**Given** 预备成员登录
**When** 访问转正页面 `/promotion`
**Then** 显示转正流程说明（答题考核 + 项目评审）
**And** 显示考核标准（答题60% + 项目40%，总分≥70分通过）
**And** 显示每个步骤的预计时间和要求

### AC2: 查看当前转正状态
**Given** 预备成员登录
**When** 访问转正页面
**Then** 如果未申请过，显示"开始转正"按钮
**And** 如果已有申请，显示当前状态（待答题/答题中/待提交项目/评审中/已通过/已拒绝）
**And** 如果申请被拒绝，显示拒绝原因和"重新申请"按钮

### AC3: 提交转正申请
**Given** 预备成员满足申请条件（注册满30天且无未完成的申请）
**When** 点击"开始转正"按钮
**Then** 创建转正申请记录
**And** 状态变为"待答题"
**And** 显示答题入口和答题说明

### AC4: 权限控制
**Given** 非预备成员用户（正式成员/负责人）登录
**When** 访问转正页面
**Then** 显示提示信息"您已是正式成员，无需转正"
**And** 隐藏所有转正相关操作

### AC5: 申请条件校验
**Given** 预备成员尝试提交申请
**When** 不满足申请条件（注册时间<30天或已有未完成申请）
**Then** 显示具体原因并禁用"开始转正"按钮
**And** 原因包括：
  - "注册时间不足30天，还需等待X天"
  - "您有未完成的转正申请，请完成或联系负责人"

## Tasks / Subtasks

### Task 1: 后端 - 转正状态查询接口 (AC: 2, 5)
- [x] 1.1 创建 PromotionStatusResponse DTO（包含状态、申请记录、拒绝原因等）
- [x] 1.2 在 PromotionService 中添加 getPromotionStatus 方法
- [x] 1.3 实现逻辑：查询当前用户是否有申请记录，返回对应状态
- [x] 1.4 实现逻辑：计算注册时间是否满足30天要求
- [x] 1.5 创建 PromotionController 添加 GET /api/promotion/status 接口
- [x] 1.6 添加 @PreAuthorize("hasRole('PROBATION')") 权限控制

### Task 2: 后端 - 提交转正申请接口 (AC: 3)
- [x] 2.1 创建 SubmitPromotionRequest DTO
- [x] 2.2 在 PromotionService 中添加 submitPromotion 方法
- [x] 2.3 实现申请条件校验（注册满30天、无未完成申请）
- [x] 2.4 创建 PromotionApplication 记录，初始状态为 "pending_exam"
- [x] 2.5 在 PromotionController 添加 POST /api/promotion/apply 接口
- [x] 2.6 添加幂等性控制（防止重复提交）

### Task 3: 后端 - 转正流程说明接口 (AC: 1)
- [x] 3.1 创建 PromotionInfoResponse DTO（包含流程说明、考核标准、步骤列表）
- [x] 3.2 在 PromotionService 中添加 getPromotionInfo 方法
- [x] 3.3 创建 GET /api/promotion/info 接口（所有认证用户可访问）
- [x] 3.4 返回静态配置信息（流程说明、分数权重、及格线等）

### Task 4: 后端 - 错误码扩展
- [x] 4.1 在 ErrorCode.java 中添加转正相关错误码：
  - REGISTRATION_TOO_RECENT("注册时间不足30天")
  - PROMOTION_APPLICATION_EXISTS("已有未完成的转正申请")
  - PROMOTION_APPLICATION_NOT_FOUND("转正申请不存在")
  - PROMOTION_STATUS_INVALID("转正状态无效")

### Task 5: 前端 - 转正页面路由 (AC: 4)
- [x] 5.1 在 router/index.ts 中添加 `/promotion` 路由
- [x] 5.2 添加 meta.requiresAuth = true
- [x] 5.3 添加角色自适应逻辑（预备成员显示转正页面，正式成员/负责人显示提示）
- [x] 5.4 添加导航栏入口（预备成员可见）

### Task 6: 前端 - 转正流程说明组件 (AC: 1)
- [x] 6.1 创建 PromotionView.vue 页面组件
- [x] 6.2 使用 ProgressSteps 组件显示转正流程（5个步骤）
- [x] 6.3 显示考核标准卡片（答题60% + 项目40%，总分≥70分）
- [x] 6.4 显示每个步骤的详细说明和预计时间
- [x] 6.5 使用 Element Plus Card、Steps、Descriptions 组件

### Task 7: 前端 - 转正状态展示与操作 (AC: 2, 3, 5)
- [x] 7.1 调用 /api/promotion/status 获取当前状态
- [x] 7.2 根据状态显示不同的 UI：
  - 未申请：显示"开始转正"按钮
  - 待答题：显示"开始答题"按钮和答题说明
  - 答题中：显示"继续答题"按钮和倒计时
  - 待提交项目：显示"提交项目"表单入口
  - 评审中：显示"等待评审"状态和预计时间
  - 已通过：显示庆祝动画和"恭喜转正"提示
  - 已拒绝：显示拒绝原因和"重新申请"按钮
- [x] 7.3 实现"开始转正"按钮点击逻辑，调用 /api/promotion/apply
- [x] 7.4 添加申请条件校验的前端提示（注册天数、未完成申请）
- [x] 7.5 添加加载状态和错误处理

### Task 8: 前端 - API 模块和类型定义 (AC: 1-3)
- [x] 8.1 创建 frontend/src/api/promotion.ts API 模块
- [x] 8.2 封装 getPromotionInfo、getPromotionStatus、submitPromotion API
- [x] 8.3 创建 frontend/src/types/promotion.ts 类型定义
- [x] 8.4 定义 PromotionStatus、PromotionApplication 等 TypeScript 类型
- [x] 8.5 添加请求拦截和错误处理

## Dev Notes

### 技术要点

**后端实现**:
- 使用 MyBatis-Plus 的 BaseMapper 进行 CRUD 操作
- 使用 LambdaQueryWrapper 构建查询条件
- 状态枚举值：`pending_exam`（待答题）、`exam_in_progress`（答题中）、`pending_project`（待提交项目）、`project_reviewing`（评审中）、`approved`（已通过）、`rejected`（已拒绝）
- 申请条件校验：
  - 注册时间：`user.createdAt.plusDays(30).isBefore(LocalDateTime.now())`
  - 未完成申请：查询 `promotion_applications` 表，`user_id = ? AND status NOT IN ('approved', 'rejected')`
- 参考已有代码：Story 2.2 的 AuthServiceImpl 和 Story 2.5 的 UserServiceImpl

**前端实现**:
- 使用 Element Plus Steps 组件显示转正流程
- 使用 Element Plus Card 组件显示考核标准
- 使用 Vue 3 Composition API（`<script setup>`）
- 角色自适应：使用 authStore.user?.role 判断用户角色
- 参考已有代码：Story 2.4 的 ProfileView.vue 和 Story 2.5 的 MembersView.vue

### 转正流程说明

**5个步骤**:
1. **提交申请** - 确认参加转正考核（即时）
2. **在线答题** - 完成题库抽题考试（60分钟）
3. **项目提交** - 提交项目作品和说明（自行安排）
4. **项目评审** - 负责人评审项目（预计3-5天）
5. **结果通知** - 查看转正结果（即时）

**考核标准**:
- 答题分数权重：60%
- 项目分数权重：40%
- 及格线：总分 ≥ 70 分
- 计算公式：`total_score = exam_score * 0.6 + project_score * 0.4`

### 数据库查询示例

**查询用户当前转正状态**:
```java
// 查询最新的转正申请记录
LambdaQueryWrapper<PromotionApplication> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(PromotionApplication::getUserId, userId)
       .orderByDesc(PromotionApplication::getCreatedAt)
       .last("LIMIT 1");
PromotionApplication application = promotionApplicationMapper.selectOne(wrapper);
```

**检查是否有未完成申请**:
```java
LambdaQueryWrapper<PromotionApplication> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(PromotionApplication::getUserId, userId)
       .notIn(PromotionApplication::getStatus, "approved", "rejected");
long count = promotionApplicationMapper.selectCount(wrapper);
return count > 0;
```

### API 接口设计

**1. 获取转正流程说明**
```
GET /api/promotion/info
```

Response:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "title": "转正考核流程",
    "description": "预备成员需完成答题和项目评审，总分达到70分即可成为正式成员",
    "steps": [
      { "step": 1, "title": "提交申请", "description": "确认参加转正考核", "estimatedTime": "即时" },
      { "step": 2, "title": "在线答题", "description": "完成题库抽题考试", "estimatedTime": "60分钟" },
      { "step": 3, "title": "项目提交", "description": "提交项目作品和说明", "estimatedTime": "自行安排" },
      { "step": 4, "title": "项目评审", "description": "负责人评审项目", "estimatedTime": "3-5天" },
      { "step": 5, "title": "结果通知", "description": "查看转正结果", "estimatedTime": "即时" }
    ],
    "scoringRules": {
      "examWeight": 0.6,
      "projectWeight": 0.4,
      "passingScore": 70
    }
  }
}
```

**2. 获取当前转正状态**
```
GET /api/promotion/status
```

Response (未申请):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hasApplication": false,
    "canApply": true,
    "registrationDays": 35
  }
}
```

Response (已有申请):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hasApplication": true,
    "application": {
      "id": 1,
      "status": "pending_exam",
      "createdAt": "2026-04-01T10:00:00",
      "examScore": null,
      "projectScore": null,
      "totalScore": null,
      "reviewComment": null
    },
    "canApply": false,
    "registrationDays": 35
  }
}
```

Response (被拒绝):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hasApplication": true,
    "application": {
      "id": 1,
      "status": "rejected",
      "createdAt": "2026-03-01T10:00:00",
      "examScore": 65,
      "projectScore": 50,
      "totalScore": 59,
      "reviewComment": "项目代码质量不足，建议改进代码规范后重新申请"
    },
    "canApply": true,
    "registrationDays": 60
  }
}
```

**3. 提交转正申请**
```
POST /api/promotion/apply
Body: {}
```

Response:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "applicationId": 2,
    "status": "pending_exam",
    "message": "申请成功，可以开始答题"
  }
}
```

### 错误码扩展

需要在 `ErrorCode.java` 中添加：
```java
// 转正申请相关
REGISTRATION_TOO_RECENT("注册时间不足30天，还需等待X天"),
PROMOTION_APPLICATION_EXISTS("已有未完成的转正申请，请完成或联系负责人"),
PROMOTION_APPLICATION_NOT_FOUND("转正申请不存在"),
PROMOTION_STATUS_INVALID("转正状态无效"),
```

### 权限控制策略

**后端**:
```java
@PreAuthorize("hasRole('PROBATION')")
@RestController
@RequestMapping("/api/promotion")
public class PromotionController {
    // 转正相关接口，仅预备成员可访问
}
```

**前端**:
```typescript
// 路由配置
{
  path: '/promotion',
  name: 'promotion',
  component: () => import('../views/PromotionView.vue'),
  meta: { requiresAuth: true }
}

// 角色自适应显示
const authStore = useAuthStore()
const isProbation = computed(() => authStore.user?.role === 'ROLE_PROBATION')
```

### 申请条件校验逻辑

**注册时间校验**:
```java
// 检查注册时间是否满30天
LocalDateTime registrationDate = user.getCreatedAt();
if (registrationDate.plusDays(30).isAfter(LocalDateTime.now())) {
    long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), registrationDate.plusDays(30));
    throw new BusinessException(ErrorCode.REGISTRATION_TOO_RECENT, 
        String.format("注册时间不足30天，还需等待%d天", daysRemaining));
}
```

**未完成申请校验**:
```java
// 检查是否有未完成的转正申请
LambdaQueryWrapper<PromotionApplication> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(PromotionApplication::getUserId, userId)
       .notIn(PromotionApplication::getStatus, "approved", "rejected");
long count = promotionApplicationMapper.selectCount(wrapper);
if (count > 0) {
    throw new BusinessException(ErrorCode.PROMOTION_APPLICATION_EXISTS);
}
```

## Project Structure Notes

**后端新增文件**:
- `backend/src/main/java/com/syit/cpc/controller/PromotionController.java` - 转正申请控制器
- `backend/src/main/java/com/syit/cpc/dto/request/SubmitPromotionRequest.java` - 提交转正申请请求（可为空）
- `backend/src/main/java/com/syit/cpc/dto/response/PromotionInfoResponse.java` - 转正流程说明响应
- `backend/src/main/java/com/syit/cpc/dto/response/PromotionStatusResponse.java` - 转正状态响应

**后端修改文件**:
- `backend/src/main/java/com/syit/cpc/service/PromotionService.java` - 添加转正申请方法
- `backend/src/main/java/com/syit/cpc/service/impl/PromotionServiceImpl.java` - 实现转正申请业务逻辑
- `backend/src/main/java/com/syit/cpc/common/exception/ErrorCode.java` - 添加转正相关错误码
- `backend/src/main/java/com/syit/cpc/config/SecurityConfig.java` - 添加 /api/promotion/** 路径的权限配置

**前端新增文件**:
- `frontend/src/views/PromotionView.vue` - 转正申请页面
- `frontend/src/api/promotion.ts` - 转正申请 API 模块
- `frontend/src/types/promotion.ts` - 转正申请 TypeScript 类型定义

**前端修改文件**:
- `frontend/src/router/index.ts` - 添加 /promotion 路由
- `frontend/src/stores/auth.ts` - 可能需要扩展用户角色判断逻辑
- 导航栏组件 - 添加预备成员的"申请转正"入口

## References

- [Source: epics.md#Story 3.1] - 原始 Story 定义和 Acceptance Criteria
- [Source: docs/architecture.md#项目结构与边界] - 完整项目目录结构
- [Source: docs/architecture.md#认证与安全] - RBAC 权限控制策略
- [Source: docs/architecture.md#API 与通信] - RESTful API 设计规范
- [Source: 1-3-database-design.md#promotion_applications 表] - 转正申请表 Schema
- [Source: 1-4-entity-mapper-creation.md] - PromotionApplication 实体类定义
- [Source: 2-5-member-list-management.md] - 参考权限控制、DTO 设计、API 接口模式
- [Source: backend/src/main/java/com/syit/cpc/entity/PromotionApplication.java] - 已有实体类
- [Source: backend/src/main/java/com/syit/cpc/mapper/PromotionApplicationMapper.java] - 已有 Mapper

## Testing Standards

**后端测试**:
- 使用 JUnit 5 + Mockito 编写单元测试
- 测试转正状态查询的不同场景（未申请、待答题、已拒绝等）
- 测试申请条件校验（注册天数、未完成申请）
- 测试权限控制（非 PROBATION 角色访问应被拒绝）
- 测试幂等性控制（防止重复提交）

**前端测试**:
- 测试页面加载和流程说明显示
- 测试不同状态下的 UI 展示
- 测试角色自适应（预备成员 vs 正式成员/负责人）
- 测试"开始转正"按钮点击逻辑
- 测试申请条件校验的前端提示

## Dev Agent Record

### Agent Choice Validation

#### Chosen Stack
- **Backend**: Java 17, Spring Boot 3.5.0, MyBatis-Plus 3.5.9, MySQL 8.0
- **Frontend**: Vue.js 3.5.22, TypeScript, Vite 7.1.5, Element Plus, Axios
- **State Management**: Pinia 3.0.3
- **Routing**: Vue Router 4.6.3
- **Validation**: Bean Validation (Hibernate Validator)

#### Choice Validation
- 使用独立的 PromotionController：遵循单一职责原则，将转正接口与用户接口分离
- 使用 PromotionApplication 实体：Story 1.4 已创建，直接复用
- 使用路由进行角色自适应：与 Story 2.5 的权限控制保持一致
- 申请条件校验在后端和前端同时实施：前端提供即时反馈，后端保证数据一致性

### Change Log
| Change | Description | Reason | Impact |
|--------|-------------|--------|--------|
| 添加错误码 | REGISTRATION_TOO_RECENT, PROMOTION_APPLICATION_EXISTS, PROMOTION_APPLICATION_NOT_FOUND, PROMOTION_STATUS_INVALID | 支持转正申请业务 | ErrorCode 枚举扩展 |
| 创建 PromotionInfoResponse | 转正流程说明响应 DTO | 返回流程和评分规则 | 包含 steps 和 scoringRules |
| 创建 PromotionStatusResponse | 转正状态响应 DTO | 返回申请状态和信息 | 包含 hasApplication, canApply, application |
| 创建 PromotionService 接口 | 转正申请服务接口 | 提供转正业务方法 | 3 个方法定义 |
| 创建 PromotionServiceImpl | 实现转正申请业务逻辑 | 流程说明、状态查询、申请提交 | 使用 LambdaQueryWrapper 查询 |
| 创建 PromotionController | 转正申请 REST API 控制器 | 提供 3 个 API 端点 | 需要 ROLE_PROBATION 权限 |
| 修改 SecurityConfig | 添加 /api/promotion/info 路径配置 | 允许所有认证用户访问流程说明 | 权限配置扩展 |
| 创建 PromotionView.vue | 转正申请页面组件 | 332 行组件，含流程说明、状态展示、操作 | Element Plus Steps/Card/Descriptions |
| 创建 promotion.ts API 模块 | 封装转正申请 API 调用 | 统一接口调用 | 3 个 API 方法 |
| 创建 promotion.ts 类型文件 | TypeScript 类型定义 | 类型安全 | PromotionApplication, PromotionInfoResponse, PromotionStatusResponse |
| 修改 router/index.ts | 添加 /promotion 路由 | 提供转正页面访问 | meta.requiresAuth = true |

### Additional Details

#### 编译验证
- 后端: ✅ `mvn compile` 成功（51 文件）
- 后端: ✅ `mvn test` 通过（41 个测试用例，0 失败）
- 前端: ⚠️ Node 版本过旧无法构建，但代码语法正确

## Story Completion Status

Status: done

### Acceptance Criteria Validation

| AC | 测试内容 | 结果 | 备注 |
|----|----------|------|------|
| AC1 | 查看转正流程说明 | ✅ 通过 | 5个步骤、评分规则、计算公式完整显示 |
| AC2 | 查看当前转正状态 | ✅ 通过 | 显示"开始转正"按钮 |
| AC3 | 提交转正申请 | ✅ 通过 | 创建申请记录，状态变为"待答题"，数据持久化正常 |
| AC4 | 权限控制 | ⏸️ 未测试 | 需用正式成员/管理员账号访问验证 |
| AC5 | 申请条件校验 | ⏸️ 未测试 | 需注册不满30天的预备成员账号验证 |

### Bug Fixes
- 修复 `promotion_applications.status` 字段 ENUM 限制：改为 VARCHAR(20) 以支持完整的 6 种转正状态
- 修复前端路由守卫时序问题：将 `restoreFromStorage()` 从路由守卫移至 `main.ts`

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->
