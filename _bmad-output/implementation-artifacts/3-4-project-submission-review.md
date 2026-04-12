---
story_id: "3.4"
story_key: "3-4-project-submission-review"
epic: "转正考核系统"
status: done
created: "2026-04-08"
---

# Story 3.4: 项目提交与评审

As a 预备成员,
I want 提交项目作品并接受负责人评审,
so that 完成转正考核并获得项目分数。

## Acceptance Criteria

### AC1: 项目提交页面（预备成员）
**Given** 预备成员考试分数达标（>=60分），状态为 `pending_project`
**When** 访问转正页面 `/promotion`
**Then** 显示"提交项目"按钮
**And** 点击后进入项目提交表单
**And** 填写项目名称（必填，最多200字符）
**And** 填写项目描述（必填，支持多行文本，最多2000字符）
**And** 填写项目链接（可选，GitHub/在线演示链接）
**And** 提交表单后状态变为 `project_reviewing`

### AC2: 项目提交验证与状态流转
**Given** 预备成员提交项目
**When** 提交成功
**Then** 后端验证必填字段（项目名称、项目描述）
**And** 更新 promotion_applications 记录
**And** 更新状态为 `project_reviewing`
**And** 显示"项目提交成功，等待负责人评审"提示
**And** 显示预计评审时间（3-5天）

### AC3: 项目评审页面（负责人）
**Given** 负责人登录系统
**When** 访问转正审批页面 `/admin/promotion`
**Then** 显示待评审项目列表
**And** 每条记录显示：申请人姓名、项目名称、考试分数、提交时间
**And** 支持按状态筛选（pending_project, project_reviewing）
**And** 支持分页展示（每页10条）

### AC4: 项目评审操作（负责人）
**Given** 负责人查看项目详情
**When** 点击评审按钮
**Then** 显示项目完整信息（名称、描述、链接）
**And** 可以输入项目评分（0-100分，必填）
**And** 可以填写评审意见（可选，最多500字符）
**And** 点击"提交评审"后保存评审结果

### AC5: 总分计算与状态更新
**Given** 负责人提交项目评审
**When** 后端计算总分
**Then** 总分 = 考试分数 × 60% + 项目分数 × 40%
**And** 总分四舍五入取整数
**And** 更新 promotion_applications 记录（projectScore, examScore, totalScore）
**And** 更新 reviewedBy 字段为当前负责人ID
**And** 更新 reviewComment 字段为评审意见

### AC6: 评审结果通知
**Given** 项目评审完成
**When** 更新状态
**Then** 总分 >= 70 分 → 状态变为 `approved`
**And** 总分 < 70 分 → 状态变为 `rejected`
**And** 预备成员在 `/promotion` 页面看到评审结果
**And** 显示总分、各部分分数、评审意见

## Tasks / Subtasks

### Task 1: 后端 - 项目提交接口 (AC: 1, 2)
- [x] 1.1 创建 SubmitProjectRequest DTO（projectName, projectDescription, projectUrl）
- [x] 1.2 在 PromotionService 添加 submitProject 方法
- [x] 1.3 在 PromotionController 添加 POST /api/promotion/project 接口
- [x] 1.4 实现状态验证（仅 pending_project 状态可提交）
- [x] 1.5 更新状态为 project_reviewing
- [x] 1.6 添加 @PreAuthorize("hasRole('PROBATION')") 权限控制

### Task 2: 后端 - 项目评审接口 (AC: 4, 5)
- [x] 2.1 创建 ReviewProjectRequest DTO（applicationId, projectScore, reviewComment）
- [x] 2.2 在 PromotionService 添加 reviewProject 方法
- [x] 2.3 在 AdminController 添加 POST /api/admin/promotion/review 接口
- [x] 2.4 实现总分计算逻辑（examScore × 0.6 + projectScore × 0.4）
- [x] 2.5 根据总分更新状态（approved >= 70, rejected < 70）
- [x] 2.6 记录评审人ID（reviewedBy）和评审意见
- [x] 2.7 添加 @PreAuthorize("hasRole('ADMIN')") 权限控制

### Task 3: 后端 - 评审列表查询接口 (AC: 3)
- [x] 3.1 创建 PromotionReviewListResponse DTO
- [x] 3.2 在 PromotionService 添加 getPendingReviews 方法（分页查询）
- [x] 3.3 在 AdminController 添加 GET /api/admin/promotion/pending 接口
- [x] 3.4 支持按状态筛选
- [x] 3.5 关联查询申请人信息（姓名、考试分数）
- [x] 3.6 添加 @PreAuthorize("hasRole('ADMIN')") 权限控制

### Task 4: 后端 - 错误处理与验证 (AC: 2, 4)
- [x] 4.1 验证项目名称不为空且长度 <= 200
- [x] 4.2 验证项目描述不为空且长度 <= 2000
- [x] 4.3 验证项目链接格式（如果提供）
- [x] 4.4 验证项目评分范围（0-100）
- [x] 4.5 扩展 ErrorCode 添加项目评审相关错误码
- [x] 4.6 验证申请人是否有 pending_project 状态的申请
- [x] 4.7 验证负责人不能评审自己的申请

### Task 5: 前端 - 项目提交页面 (AC: 1, 2)
- [x] 5.1 创建 ProjectSubmitView.vue 页面
- [x] 5.2 实现项目提交表单（名称、描述、链接）
- [x] 5.3 添加表单验证（必填、长度限制）
- [x] 5.4 调用项目提交接口
- [x] 5.5 提交成功后显示成功提示
- [x] 5.6 跳转到转正状态页面

### Task 6: 前端 - 项目评审页面（负责人） (AC: 3, 4)
- [x] 6.1 创建 AdminPromotionReviewView.vue 页面
- [x] 6.2 显示待评审列表（表格形式）
- [x] 6.3 实现分页和状态筛选
- [x] 6.4 点击评审按钮打开评审对话框
- [x] 6.5 显示项目完整信息
- [x] 6.6 实现评分输入（0-100分滑块或输入框）
- [x] 6.7 实现评审意见输入框
- [x] 6.8 调用评审接口
- [x] 6.9 评审成功后刷新列表

### Task 7: 前端 - 评审结果展示 (AC: 6)
- [x] 7.1 在 PromotionView.vue 更新 approved/rejected 状态展示
- [x] 7.2 显示总分、考试分数、项目分数
- [x] 7.3 显示评审意见
- [x] 7.4 计算过程展示（公式可视化）

### Task 8: 前端 - 路由和导航 (AC: 1, 3)
- [x] 8.1 在 router/index.ts 添加 `/promotion/project/submit` 路由
- [x] 8.2 在 router/index.ts 添加 `/admin/promotion/review` 路由（ADMIN 角色）
- [x] 8.3 在 PromotionView.vue 添加"提交项目"按钮（pending_project 状态时）
- [x] 8.4 添加 meta.requiresAuth 和 meta.roles 权限控制

## Dev Notes

### 技术要点

**后端实现**:
- 参考 Story 3.3 的状态流转模式（pending_exam → exam_failed/pending_project）
- 本项目新增状态流转：pending_project → project_reviewing → approved/rejected
- 总分计算逻辑示例：
  ```java
  int examScore = application.getExamScore(); // 已有
  int projectScore = request.getProjectScore();
  int totalScore = Math.round(examScore * 0.6f + projectScore * 0.4f);
  ```
- 评审状态判断：
  ```java
  if (totalScore >= 70) {
      application.setStatus("approved");
  } else {
      application.setStatus("rejected");
  }
  ```
- 参考已有代码：PromotionServiceImpl（状态查询、申请提交逻辑）
- 参考已有代码：AdminController（管理员接口模式）

**前端实现**:
- 项目提交表单使用 Element Plus 表单组件
- 评审列表使用 Element Plus Table + Pagination
- 评审对话框使用 Element Plus Dialog
- 评分输入使用 el-input-number（范围 0-100）或 el-slider
- 参考已有代码：PromotionView.vue（状态展示模式）
- 参考已有代码：QuestionsView.vue（表单验证模式）

### 数据库 Schema

**promotion_applications 表**（已存在，无需迁移）:
```sql
CREATE TABLE IF NOT EXISTS promotion_applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    project_name VARCHAR(200) COMMENT '项目名称',
    project_description TEXT COMMENT '项目描述',
    project_url VARCHAR(500) COMMENT '项目链接',
    project_score INT COMMENT '项目评分(0-100)',
    exam_score INT COMMENT '考试分数(0-100)',
    total_score INT COMMENT '总分数(0-100)',
    status VARCHAR(20) DEFAULT 'pending_exam' COMMENT '状态: pending_exam-待答题, exam_in_progress-答题中, exam_failed-答题未通过, pending_project-待提交项目, project_reviewing-评审中, approved-已通过, rejected-已拒绝',
    reviewed_by BIGINT COMMENT '审核人ID',
    review_comment TEXT COMMENT '审核意见',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    
    INDEX idx_promotion_user (user_id),
    INDEX idx_promotion_status (status),
    INDEX idx_promotion_created_at (created_at),
    INDEX idx_promotion_total_score (total_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='转正申请表';
```

**状态流转图**:
```
pending_exam (待答题)
    ↓ 考试通过
pending_project (待提交项目)
    ↓ 提交项目
project_reviewing (评审中)
    ↓ 评审通过 (总分>=70)
approved (已通过)
    
pending_exam → exam_failed (考试未通过) → pending_exam (重新答题)
project_reviewing → rejected (评审未通过, 总分<70)
```

### API 接口设计

**1. 提交项目（预备成员）**
```
POST /api/promotion/project
Authorization: Bearer {token}
Content-Type: application/json

{
  "projectName": "个人博客系统",
  "projectDescription": "使用Spring Boot + Vue开发的个人博客系统，实现了文章发布、评论、标签分类等功能。项目采用前后端分离架构，后端使用RESTful API设计，前端使用Vue3 + Element Plus。",
  "projectUrl": "https://github.com/username/blog-system"
}
```

Response:
```json
{
  "code": 200,
  "message": "项目提交成功，等待负责人评审",
  "data": {
    "applicationId": 1,
    "status": "project_reviewing",
    "estimatedReviewDays": "3-5天"
  }
}
```

**2. 获取待评审列表（负责人）**
```
GET /api/admin/promotion/pending?page=1&size=10&status=pending_project
Authorization: Bearer {token}
```

Response:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 15,
    "pages": 2,
    "current": 1,
    "size": 10,
    "records": [
      {
        "applicationId": 1,
        "applicantName": "张三",
        "projectName": "个人博客系统",
        "examScore": 85,
        "submittedAt": "2026-04-08T14:30:00",
        "status": "pending_project"
      }
    ]
  }
}
```

**3. 评审项目（负责人）**
```
POST /api/admin/promotion/review
Authorization: Bearer {token}
Content-Type: application/json

{
  "applicationId": 1,
  "projectScore": 80,
  "reviewComment": "项目结构清晰，代码规范，功能完整。建议增加单元测试覆盖。"
}
```

Response:
```json
{
  "code": 200,
  "message": "评审完成",
  "data": {
    "applicationId": 1,
    "examScore": 85,
    "projectScore": 80,
    "totalScore": 83,
    "status": "approved",
    "reviewComment": "项目结构清晰，代码规范，功能完整。建议增加单元测试覆盖。"
  }
}
```

### 权限控制策略

**后端**:
```java
// 预备成员接口
@RestController
@RequestMapping("/api/promotion")
public class PromotionController {
    @PostMapping("/project")
    @PreAuthorize("hasRole('PROBATION')")  // 仅预备成员
    public ApiResponse<?> submitProject(...) { ... }
}

// 管理员接口
@RestController
@RequestMapping("/api/admin/promotion")
public class AdminController {  // 或新的 AdminPromotionController
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")  // 仅负责人
    public ApiResponse<?> getPendingReviews(...) { ... }
    
    @PostMapping("/review")
    @PreAuthorize("hasRole('ADMIN')")  // 仅负责人
    public ApiResponse<?> reviewProject(...) { ... }
}
```

### 总分计算规则

- **考试权重**: 60% (0.6)
- **项目权重**: 40% (0.4)
- **及格线**: 70 分
- **计算公式**: `totalScore = round(examScore * 0.6 + projectScore * 0.4)`
- **示例**:
  - 考试 85 分 + 项目 80 分 → 总分 = round(85×0.6 + 80×0.4) = round(51 + 32) = 83 分 → 通过
  - 考试 60 分 + 项目 50 分 → 总分 = round(60×0.6 + 50×0.4) = round(36 + 20) = 56 分 → 不通过
  - 考试 70 分 + 项目 70 分 → 总分 = round(70×0.6 + 70×0.4) = round(42 + 28) = 70 分 → 通过

## Project Structure Notes

**后端新增文件**:
- `backend/src/main/java/com/syit/cpc/dto/request/SubmitProjectRequest.java` - 提交项目请求
- `backend/src/main/java/com/syit/cpc/dto/request/ReviewProjectRequest.java` - 评审项目请求
- `backend/src/main/java/com/syit/cpc/dto/response/PromotionReviewListResponse.java` - 待评审列表响应

**后端修改文件**:
- `backend/src/main/java/com/syit/cpc/service/PromotionService.java` - 添加 submitProject 和 reviewProject 方法
- `backend/src/main/java/com/syit/cpc/service/impl/PromotionServiceImpl.java` - 实现项目提交和评审逻辑
- `backend/src/main/java/com/syit/cpc/controller/PromotionController.java` - 添加 POST /api/promotion/project 接口
- `backend/src/main/java/com/syit/cpc/controller/AdminController.java` - 添加评审列表和评审接口
- `backend/src/main/java/com/syit/cpc/common/exception/ErrorCode.java` - 添加项目评审相关错误码

**前端新增文件**:
- `frontend/src/views/ProjectSubmitView.vue` - 项目提交页面
- `frontend/src/views/AdminPromotionReviewView.vue` - 负责人评审页面

**前端修改文件**:
- `frontend/src/api/promotion.ts` - 添加 submitProject、getPendingReviews、reviewProject 函数
- `frontend/src/types/promotion.ts` - 添加项目提交和评审相关类型定义
- `frontend/src/router/index.ts` - 添加 /promotion/project/submit 和 /admin/promotion/review 路由
- `frontend/src/views/PromotionView.vue` - 在 pending_project 状态时显示"提交项目"按钮并导航

## References

- [Source: epics.md#Story 3.4] - 原始 Story 定义和 Acceptance Criteria
- [Source: 3-3-online-exam.md] - 参考考试状态流转和总分计算设计
- [Source: docs/architecture.md#API 与通信] - RESTful API 设计规范
- [Source: docs/architecture.md#认证与安全] - RBAC 权限控制策略
- [Source: backend/src/main/resources/db/migration/V1__init_schema.sql#L91-L118] - promotion_applications 表 Schema
- [Source: backend/src/main/resources/db/migration/V5__expand_promotion_status_enum.sql] - status 字段扩展为 VARCHAR
- [Source: backend/src/main/java/com/syit/cpc/entity/PromotionApplication.java] - PromotionApplication 实体（已包含项目字段）
- [Source: backend/src/main/java/com/syit/cpc/service/PromotionService.java] - 现有 PromotionService 接口
- [Source: backend/src/main/java/com/syit/cpc/service/impl/PromotionServiceImpl.java] - 现有服务实现
- [Source: backend/src/main/java/com/syit/cpc/controller/PromotionController.java] - 现有 PromotionController
- [Source: backend/src/main/java/com/syit/cpc/controller/AdminController.java] - 现有 AdminController（参考管理员接口模式）
- [Source: frontend/src/views/PromotionView.vue] - 现有转正页面（已展示 pending_project 状态，按钮占位）
- [Source: frontend/src/api/promotion.ts] - 现有 promotion API 模块
- [Source: frontend/src/types/promotion.ts] - 现有 TypeScript 类型定义

## Testing Standards

**后端测试**:
- 使用 JUnit 5 + Mockito 编写单元测试
- 测试项目提交流程（状态验证、字段验证、状态更新）
- 测试评审流程（总分计算、状态更新、评审人记录）
- 测试边界条件（评分 0 分、100 分、总分 69 分、70 分）
- 测试权限控制（非 PROBATION 角色提交项目应被拒绝，非 ADMIN 角色评审应被拒绝）
- 测试重复提交防护（project_reviewing 状态不可再次提交）
- 测试评审人不能评审自己的申请

**前端测试**:
- 测试项目提交表单验证（必填、长度限制）
- 测试评审列表分页和筛选
- 测试评审对话框交互
- 测试总分计算展示（公式可视化）
- 测试不同状态下的页面展示

## Previous Story Intelligence (3-3)

**关键经验**:
1. **状态流转**: Story 3.3 中实现了 pending_exam → exam_failed/pending_project 的状态流转。Story 3.4 需继续 pending_project → project_reviewing → approved/rejected 的流转。
2. **时区处理**: Story 3.3 遇到 UTC/本地时间问题，使用本地时间格式而非 ISO 8601。Story 3.4 中时间字段需遵循相同模式。
3. **重复提交防护**: Story 3.3 实现了考试重复提交防护。Story 3.4 中项目提交也需防护（project_reviewing 状态不可重复提交）。
4. **分数计算**: Story 3.3 中考试分数已记录到 exam_score 字段。Story 3.4 需读取 exam_score 并计算总分。
5. **及格线常量**: Story 3.3 使用 PASSING_SCORE_THRESHOLD = 60（考试及格线）。Story 3.4 使用 PASSING_TOTAL_SCORE = 70（转正总分及格线）。

**已建立的模式**:
- MyBatis-Plus 分页查询模式
- 全局异常处理（BusinessException）
- JWT Token 解析（JwtTokenProvider.getUserIdFromToken）
- 前后端 API 封装模式（Axios + TypeScript 类型）
- Element Plus 组件使用规范
- 表单验证模式（@Valid + DTO）

**需要注意的问题**:
- promotion_applications 表的 status 字段已从 ENUM 改为 VARCHAR（V5 迁移），支持更多状态值
- exam_score 字段已在 Story 3.3 中写入，Story 3.4 直接读取即可
- reviewed_by 和 review_comment 字段已存在，直接更新

## Dev Agent Guardrails

### Technical Requirements
- 严格遵循现有代码风格和架构模式
- 所有 API 接口必须添加 Swagger 注解（@Operation）
- DTO 必须使用 @Valid 验证（@NotBlank, @Size, @Min, @Max）
- 总分计算必须在后端实现，不可依赖前端
- 评审结果必须记录到数据库（reviewedBy, reviewComment, totalScore）
- 状态流转必须验证当前状态（防止非法状态跳转）

### Architecture Compliance
- 使用 MyBatis-Plus BaseMapper 进行 CRUD
- 使用 LambdaQueryWrapper 构建查询条件
- 使用 @PreAuthorize 进行权限控制（PROBATION / ADMIN）
- 使用 BusinessException 抛出业务异常
- 使用 ApiResponse 统一响应格式
- 管理员接口放在 AdminController 或新的 AdminPromotionController

### Library/Framework Requirements
- Spring Boot 3.5.0
- MyBatis-Plus 3.5.9
- Vue 3.5.22 + TypeScript
- Element Plus UI 组件库
- Pinia 状态管理（如需）

### File Structure Requirements
- 后端文件按包结构组织（controller/service/mapper/entity/dto）
- 前端文件按功能模块组织（views/api/types）
- 所有新文件必须放在正确的目录中
- DTO 按 request/response 子包组织

### Testing Requirements
- 关键业务逻辑必须编写单元测试
- 总分计算测试覆盖率 ≥ 80%
- 前端关键交互必须手动测试验证
- 测试边界条件（0 分、100 分、69 分、70 分）

## Story Completion Status

Status: review

### Questions & Clarifications
- Q: 项目评审是否需要多人评审？
  A: MVP 阶段仅单人评审（负责人），后续可支持多人评审
- Q: 评审被拒绝后是否可以重新提交项目？
  A: 可以，rejected 状态且 canApply=true 时允许重新申请
- Q: 是否需要项目文件上传功能？
  A: MVP 阶段仅支持链接和文字描述，不支持文件上传
- Q: 评审是否需要通知机制（邮件/站内信）？
  A: MVP 阶段不实现通知，用户在 /promotion 页面查看结果
- Q: 总分 70 分是否可配置？
  A: MVP 阶段硬编码 70 分，后续可配置化

## Dev Agent Record

### Agent Model Used

{{agent_model_name_version}}

### Debug Log References

### Completion Notes List

**✅ 已完成的功能**:
1. **后端核心功能**:
   - POST /api/promotion/project - 预备成员提交项目接口
   - POST /api/admin/promotion/review - 负责人评审项目接口
   - GET /api/admin/promotion/pending - 负责人获取待评审列表（分页）
   - 总分计算逻辑：examScore × 0.6 + projectScore × 0.4，四舍五入取整
   - 状态流转：pending_project → project_reviewing → approved/rejected
   - 重复提交防护（project_reviewing 状态不可再次提交）
   - 状态验证（仅 pending_project 可提交，仅 project_reviewing 可评审）

2. **前端功能**:
   - ProjectSubmitView.vue - 项目提交页面（表单验证、字数限制）
   - AdminPromotionReviewView.vue - 负责人评审页面（列表、分页、筛选、对话框评分）
   - PromotionView.vue 更新 pending_project 状态按钮
   - 评审对话框实时显示总分计算结果
   - 总分 >= 70 显示"通过"，< 70 显示"不通过"

3. **DTO 和类型定义**:
   - SubmitProjectRequest DTO（后端）
   - ReviewProjectRequest DTO（后端）
   - PromotionReviewListResponse DTO（后端）
   - PromotionReviewListResponse / ReviewItem / ReviewProjectRequest 类型（前端）

4. **错误码扩展**:
   - PROJECT_ALREADY_SUBMITTED (2005)
   - INVALID_PROJECT_SUBMISSION_STATUS (2006)
   - PROJECT_NOT_PENDING_REVIEW (2007)

### File List

**新增文件**:
- `backend/src/main/java/com/syit/cpc/dto/request/SubmitProjectRequest.java`
- `backend/src/main/java/com/syit/cpc/dto/request/ReviewProjectRequest.java`
- `backend/src/main/java/com/syit/cpc/dto/response/PromotionReviewListResponse.java`
- `frontend/src/views/ProjectSubmitView.vue`
- `frontend/src/views/AdminPromotionReviewView.vue`

**修改文件**:
- `backend/src/main/java/com/syit/cpc/service/PromotionService.java` - 添加 submitProject、reviewProject、getPendingReviews 方法
- `backend/src/main/java/com/syit/cpc/service/impl/PromotionServiceImpl.java` - 实现项目提交、评审、列表查询逻辑
- `backend/src/main/java/com/syit/cpc/controller/PromotionController.java` - 添加 POST /api/promotion/project 接口
- `backend/src/main/java/com/syit/cpc/controller/AdminController.java` - 添加评审列表和评审接口，修改 @RequestMapping 为 /api/admin
- `backend/src/main/java/com/syit/cpc/common/exception/ErrorCode.java` - 添加 PROJECT_ALREADY_SUBMITTED、INVALID_PROJECT_SUBMISSION_STATUS、PROJECT_NOT_PENDING_REVIEW
- `frontend/src/api/promotion.ts` - 添加 submitProject、getPendingReviews、reviewProject 函数
- `frontend/src/types/promotion.ts` - 添加 PromotionReviewListResponse、ReviewItem、ReviewProjectRequest 类型
- `frontend/src/router/index.ts` - 添加 /promotion/project/submit 和 /admin/promotion/review 路由
- `frontend/src/views/PromotionView.vue` - 在 pending_project 状态时显示"提交项目"按钮并导航

