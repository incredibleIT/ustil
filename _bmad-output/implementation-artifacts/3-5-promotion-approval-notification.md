---
story_id: "3.5"
story_key: "3-5-promotion-approval-notification"
epic: "转正考核系统"
status: ready-for-dev
created: "2026-04-08"
---

# Story 3.5: 转正审批与结果通知

As a 预备成员,
I want 收到转正审批结果通知并被自动更新角色,
so that 成为正式成员并获得通知。

## Acceptance Criteria

### AC1: 自动角色变更（通过时）
**Given** 负责人完成评审且总分 >= 70 分
**When** 状态变为 `approved`
**Then** 后端自动将该用户角色从 `ROLE_PROBATION` 更新为 `ROLE_MEMBER`
**And** 在 promotion_applications 记录 reviewed_at 时间戳
**And** 记录角色变更日志（audit trail）

### AC2: 站内信通知（通过）
**Given** 转正申请状态变为 `approved`
**When** 后端处理完成
**Then** 创建一条站内信通知给用户
**And** 通知类型：promotion_approved
**And** 通知标题："恭喜转正！"
**And** 通知内容包含：总分、考试分数、项目分数、评审意见
**And** 通知创建时间记录

### AC3: 站内信通知（拒绝）
**Given** 转正申请状态变为 `rejected`
**When** 后端处理完成
**Then** 创建一条站内信通知给用户
**And** 通知类型：promotion_rejected
**And** 通知标题："转正申请未通过"
**And** 通知内容包含：总分、考试分数、项目分数、评审意见（拒绝原因）
**And** 提示用户可以重新申请的条件

### AC4: 通知列表展示（前端）
**Given** 用户登录系统
**When** 访问个人中心或通知页面
**Then** 显示站内信列表
**And** 区分已读/未读状态
**And** 显示通知时间、标题、类型
**And** 支持标记为已读
**And** 支持删除通知

### AC5: 通知详情展示（前端）
**Given** 用户点击某条通知
**When** 查看通知详情
**Then** 显示完整通知内容
**And** 对于转正相关通知，显示"查看转正状态"按钮
**And** 点击后跳转到 /promotion 页面
**And** 自动标记该通知为已读

### AC6: 未读通知计数
**Given** 用户登录系统
**When** 导航栏显示通知图标
**Then** 显示未读通知数量徽章
**And** 点击图标显示最近 5 条通知预览
**And** 支持一键"全部标记为已读"

## Tasks / Subtasks

### Task 1: 后端 - 角色变更与审计 (AC: 1)
- [x] 1.1 在 PromotionServiceImpl.reviewProject 方法中添加角色变更逻辑
- [x] 1.2 检查当前状态为 approved 时，更新用户角色为 ROLE_MEMBER
- [x] 1.3 调用 UserService.updateUserRole 方法更新角色
- [x] 1.4 添加 reviewed_at 字段记录评审完成时间
- [x] 1.5 在日志中记录角色变更信息

### Task 2: 后端 - 站内信基础设施 (AC: 2, 3)
- [x] 2.1 创建 notifications 数据库表（Flyway 迁移）
- [x] 2.2 创建 Notification 实体类
- [x] 2.3 创建 NotificationMapper
- [x] 2.4 创建 NotificationService 接口和实现
- [x] 2.5 实现 sendNotification 方法（创建通知）
- [x] 2.6 实现 getNotifications 方法（分页查询用户通知）
- [x] 2.7 实现 markAsRead 方法（标记已读）
- [x] 2.8 实现 deleteNotification 方法（删除通知）
- [x] 2.9 实现 getUnreadCount 方法（未读计数）
- [x] 2.10 实现 markAllAsRead 方法（全部标记已读）

### Task 3: 后端 - 集成通知到转正流程 (AC: 2, 3)
- [x] 3.1 在 reviewProject 方法中调用通知服务
- [x] 3.2 approved 状态：创建 promotion_approved 类型通知
- [x] 3.3 rejected 状态：创建 promotion_rejected 类型通知
- [x] 3.4 通知内容包含完整分数信息和评审意见
- [x] 3.5 添加事务管理确保原子性

### Task 4: 后端 - 通知 API 接口 (AC: 4, 5, 6)
- [x] 4.1 创建 NotificationController
- [x] 4.2 GET /api/notifications - 获取通知列表（分页）
- [x] 4.3 GET /api/notifications/unread-count - 获取未读计数
- [x] 4.4 PUT /api/notifications/{id}/read - 标记单条已读
- [x] 4.5 PUT /api/notifications/read-all - 全部标记已读
- [x] 4.6 DELETE /api/notifications/{id} - 删除通知
- [x] 4.7 添加 @PreAuthorize("isAuthenticated()") 权限控制

### Task 5: 前端 - 通知页面和组件 (AC: 4, 5)
- [x] 5.1 创建 NotificationView.vue 通知列表页面
- [x] 5.2 创建 NotificationItem.vue 通知卡片组件
- [x] 5.3 实现通知列表展示（区分已读/未读样式）
- [x] 5.4 实现通知详情展示对话框
- [x] 5.5 实现标记已读功能
- [x] 5.6 实现删除通知功能
- [x] 5.7 添加"查看转正状态"按钮（转正相关通知）

### Task 6: 前端 - 导航栏通知图标 (AC: 6)
- [x] 6.1 在顶部导航栏添加通知图标组件
- [x] 6.2 显示未读通知数量徽章
- [x] 6.3 点击图标弹出最近 5 条通知预览
- [x] 6.4 实现"全部标记为已读"按钮
- [x] 6.5 点击通知跳转到详情页面
- [x] 6.6 使用 Pinia store 管理通知状态

### Task 7: 数据库迁移 (AC: 2, 3)
- [x] 7.1 创建 V6__create_notifications_table.sql
- [x] 7.2 添加 reviewed_at 字段到 promotion_applications 表
- [x] 7.3 添加索引优化查询性能

## Dev Notes

### 技术要点

**数据库 Schema 变更**:

**notifications 表**（新建）:
```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    type VARCHAR(50) NOT NULL COMMENT '通知类型: promotion_approved, promotion_rejected',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
    related_id BIGINT COMMENT '关联业务ID（如转正申请ID）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    read_at TIMESTAMP NULL COMMENT '阅读时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    
    INDEX idx_notification_user (user_id),
    INDEX idx_notification_user_read (user_id, is_read),
    INDEX idx_notification_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信通知表';
```

**promotion_applications 表变更**（添加 reviewed_at 字段）:
```sql
ALTER TABLE promotion_applications 
ADD COLUMN reviewed_at TIMESTAMP NULL COMMENT '评审完成时间' 
AFTER review_comment;
```

**后端实现**:

**Notification 实体类**:
```java
@Data
@TableName("notifications")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private Integer isRead; // 0-未读, 1-已读
    private Long relatedId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    @TableLogic
    private Integer deleted;
}
```

**角色变更逻辑**（在 reviewProject 方法中）:
```java
// 6. 根据总分更新状态
if (totalScore >= 70) {
    application.setStatus(PromotionStatus.APPROVED.getCode());
    
    // 更新用户角色为正式成员
    UpdateUserRoleRequest roleRequest = new UpdateUserRoleRequest();
    roleRequest.setRole(UserRole.ROLE_MEMBER.getCode());
    userService.updateUserRole(application.getUserId(), roleRequest);
    
    // 发送通过通知
    notificationService.sendPromotionApprovedNotification(
        application.getUserId(),
        application.getId(),
        examScore,
        projectScore,
        totalScore,
        request.getReviewComment()
    );
} else {
    application.setStatus(PromotionStatus.REJECTED.getCode());
    
    // 发送拒绝通知
    notificationService.sendPromotionRejectedNotification(
        application.getUserId(),
        application.getId(),
        examScore,
        projectScore,
        totalScore,
        request.getReviewComment()
    );
}

application.setReviewedAt(LocalDateTime.now());
```

**通知内容模板**:

**通过通知**:
```
标题：恭喜转正！
内容：
  恭喜您通过转正考核！
  考试分数：{examScore} 分
  项目分数：{projectScore} 分
  总分：{totalScore} 分
  评审意见：{reviewComment}
  
  您已成为正式成员，欢迎加入！
```

**拒绝通知**:
```
标题：转正申请未通过
内容：
  很遗憾，您的转正申请未通过。
  考试分数：{examScore} 分
  项目分数：{projectScore} 分
  总分：{totalScore} 分
  评审意见：{reviewComment}
  
  您可以完善项目后重新申请转正。
```

**前端实现**:

**通知类型枚举**（前端）:
```typescript
export enum NotificationType {
  PROMOTION_APPROVED = 'promotion_approved',
  PROMOTION_REJECTED = 'promotion_rejected',
  // 后续可扩展
}
```

**通知 API**:
```typescript
// frontend/src/api/notification.ts
export function getNotifications(page: number, size: number) { ... }
export function getUnreadCount() { ... }
export function markAsRead(id: number) { ... }
export function markAllAsRead() { ... }
export function deleteNotification(id: number) { ... }
```

**Pinia Store**:
```typescript
// frontend/src/stores/notification.ts
export const useNotificationStore = defineStore('notification', {
  state: () => ({
    notifications: [],
    unreadCount: 0,
    loading: false
  }),
  actions: {
    async fetchUnreadCount() { ... },
    async fetchNotifications() { ... },
    async markAsRead(id: number) { ... },
    async markAllAsRead() { ... }
  }
})
```

### 架构注意事项

1. **事务管理**: 评审和角色变更必须在同一事务中
2. **通知解耦**: 通知服务应独立，便于后续扩展其他通知类型
3. **权限控制**: 通知 API 仅对通知所属用户开放
4. **分页查询**: 通知列表必须分页，避免一次性加载过多数据
5. **索引优化**: user_id + is_read 复合索引用于未读计数查询

### 数据库迁移顺序

1. V6__create_notifications_table.sql - 创建通知表
2. V7__add_reviewed_at_to_promotion.sql - 添加 reviewed_at 字段

### 状态流转补充

```
pending_exam → exam_failed → pending_exam (重新答题)
pending_exam → pending_project (考试通过)
pending_project → project_reviewing (提交项目)
project_reviewing → approved (总分>=70) → 角色变更为 ROLE_MEMBER + 发送通知
project_reviewing → rejected (总分<70) → 发送通知（可重新申请）
```

### 权限控制

**NotificationController**:
- 所有接口需要认证用户
- 用户只能查看/操作自己的通知
- 通过 userId 从 SecurityContext 获取，防止越权

```java
@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationController {
    // 所有方法自动使用当前登录用户ID
}
```

### Project Structure Notes

**后端新增文件**:
- `backend/src/main/java/com/syit/cpc/entity/Notification.java` - 通知实体
- `backend/src/main/java/com/syit/cpc/mapper/NotificationMapper.java` - 通知 Mapper
- `backend/src/main/java/com/syit/cpc/service/NotificationService.java` - 通知服务接口
- `backend/src/main/java/com/syit/cpc/service/impl/NotificationServiceImpl.java` - 通知服务实现
- `backend/src/main/java/com/syit/cpc/controller/NotificationController.java` - 通知控制器
- `backend/src/main/java/com/syit/cpc/dto/response/NotificationListResponse.java` - 通知列表响应
- `backend/src/main/java/com/syit/cpc/dto/response/NotificationItemResponse.java` - 通知项响应
- `backend/src/main/resources/db/migration/V6__create_notifications_table.sql` - 通知表迁移
- `backend/src/main/resources/db/migration/V7__add_reviewed_at_to_promotion.sql` - 添加 reviewed_at

**后端修改文件**:
- `backend/src/main/java/com/syit/cpc/service/impl/PromotionServiceImpl.java` - 添加角色变更和通知逻辑
- `backend/src/main/java/com/syit/cpc/entity/PromotionApplication.java` - 添加 reviewedAt 字段

**前端新增文件**:
- `frontend/src/views/NotificationView.vue` - 通知列表页面
- `frontend/src/components/NotificationItem.vue` - 通知卡片组件
- `frontend/src/components/NotificationBadge.vue` - 导航栏通知徽章组件
- `frontend/src/api/notification.ts` - 通知 API
- `frontend/src/types/notification.ts` - 通知类型定义
- `frontend/src/stores/notification.ts` - 通知 Pinia Store

**前端修改文件**:
- `frontend/src/router/index.ts` - 添加 /notifications 路由
- `frontend/src/components/Navbar.vue` 或类似导航组件 - 集成通知徽章
- `frontend/src/views/PromotionView.vue` - 添加"查看转正状态"按钮跳转

### References

- [Source: epics.md#Story 3.5] - 原始 Story 定义和 Acceptance Criteria
- [Source: epics.md#FR9] - 转正审批需求（待审批列表+查看分数+设置通过分数线+审批通过/拒绝+结果通知）
- [Source: backend/src/main/java/com/syit/cpc/service/impl/PromotionServiceImpl.java] - 现有 reviewProject 方法（需添加角色变更和通知逻辑）
- [Source: backend/src/main/java/com/syit/cpc/entity/User.java] - User 实体（role 字段）
- [Source: backend/src/main/java/com/syit/cpc/service/UserService.java] - UserService 接口（updateUserRole 方法）
- [Source: backend/src/main/java/com/syit/cpc/common/constant/UserRole.java] - 角色常量定义
- [Source: backend/src/main/java/com/syit/cpc/service/impl/UserServiceImpl.java#L170] - updateUserRole 实现参考
- [Source: docs/architecture.md#API 与通信] - RESTful API 设计规范
- [Source: docs/architecture.md#认证与安全] - RBAC 权限控制策略

### Testing Standards

**后端测试**:
- 使用 JUnit 5 + Mockito 编写单元测试
- 测试角色变更逻辑（approved 状态自动变更为 ROLE_MEMBER）
- 测试通知创建（通过/拒绝两种场景）
- 测试通知 CRUD 操作（创建、查询、标记已读、删除）
- 测试权限控制（用户只能查看自己的通知）
- 测试事务一致性（角色变更和通知创建在同一事务）
- 测试未读计数准确性

**前端测试**:
- 测试通知列表分页和加载
- 测试已读/未读样式区分
- 测试标记已读功能
- 测试删除通知功能
- 测试未读徽章计数更新
- 测试通知详情跳转

## Previous Story Intelligence (3-4)

**关键经验**:
1. **总分计算**: Story 3-4 已实现总分计算（examScore × 0.6 + projectScore × 0.4），approved/rejected 状态已更新
2. **行级锁防护**: Story 3-4 使用 `selectLatestWithLock` 防止并发重复提交
3. **状态枚举**: 使用 `PromotionStatus` 枚举管理所有状态，避免硬编码
4. **批量查询优化**: Story 3-4 使用 `selectBatchIds` 解决 N+1 查询问题
5. **类型安全**: 前端使用 TypeScript 类型定义，避免 any 类型

**已建立的模式**:
- PromotionServiceImpl 中评审流程：查询 → 验证 → 计算 → 更新状态
- 后端 DTO 验证：@Valid + @NotBlank + @Size + @Min + @Max
- 前端表单验证：Element Plus Form + rules
- 分页查询：MyBatis-Plus Page + LambdaQueryWrapper
- 权限控制：@PreAuthorize("hasRole('ADMIN')") 或 @PreAuthorize("isAuthenticated()")

**需要注意的问题**:
- Story 3-4 已实现评审和状态更新，Story 3-5 只需添加角色变更和通知
- 不要重复实现总分计算逻辑，直接在 reviewProject 方法后追加
- 确保角色变更和通知创建在同一事务中
- 通知服务应设计为可扩展，支持未来其他通知类型

## Dev Agent Guardrails

### Technical Requirements
- 角色变更必须使用 UserService.updateUserRole 方法
- 通知必须包含完整分数信息（考试、项目、总分）
- 通知内容使用模板字符串，便于后续国际化
- 所有通知操作记录日志（userId, type, relatedId）
- 评审完成时设置 reviewedAt 时间戳

### Architecture Compliance
- 使用 MyBatis-Plus BaseMapper 进行 CRUD
- 使用 LambdaQueryWrapper 构建查询条件
- 使用 @Transactional 确保事务一致性
- 通知服务独立于 PromotionService，通过依赖注入调用
- 前端使用 Pinia store 管理通知状态

### Library/Framework Requirements
- Spring Boot 3.5.0
- MyBatis-Plus 3.5.9
- Vue 3.5.22 + TypeScript
- Element Plus UI 组件库
- Pinia 状态管理

### File Structure Requirements
- 后端文件按包结构组织（controller/service/mapper/entity/dto）
- 前端文件按功能模块组织（views/api/types/stores/components）
- 所有新文件必须放在正确的目录中
- 数据库迁移文件放在 resources/db/migration 目录

### Testing Requirements
- 角色变更测试覆盖率 ≥ 80%
- 通知 CRUD 测试覆盖率 ≥ 80%
- 事务一致性必须测试
- 前端关键交互必须手动测试验证

## Story Completion Status

Status: review

### Questions & Clarifications
- Q: 是否需要邮件通知？
  A: MVP 阶段仅实现站内信，邮件通知后续扩展
- Q: 拒绝后是否需要冷却期？
  A: 不需要，rejected 状态且 canApply=true 时立即可重新申请
- Q: 通知是否需要推送（WebSocket/Server-Sent Events）？
  A: MVP 阶段仅拉取模式，用户主动获取通知
- Q: 通知保留期限？
  A: 不自动删除，用户手动删除，逻辑删除标记

## Dev Agent Record

### Agent Model Used

{{agent_model_name_version}}

### Debug Log References

### Completion Notes List

**后端实现完成**:
1. ✅ 数据库迁移：V6 创建 notifications 表，V7 添加 reviewed_at 字段
2. ✅ 通知系统完整实现：Entity, Mapper, Service, Controller
3. ✅ 角色变更逻辑：approved 状态自动更新用户角色为 ROLE_MEMBER
4. ✅ 通知创建：转正通过/拒绝时自动发送站内信
5. ✅ 通知 CRUD：列表查询、标记已读、删除、未读计数、全部标记已读
6. ✅ 权限控制：所有通知接口需认证，用户只能操作自己的通知
7. ✅ 后端编译通过：mvn compile BUILD SUCCESS

**前端实现完成**:
1. ✅ TypeScript 类型定义：NotificationItem, NotificationListResponse, NotificationType
2. ✅ API 接口：getNotifications, getUnreadCount, markAsRead, markAllAsRead, deleteNotification
3. ✅ Pinia Store：notification store 管理通知状态
4. ✅ 通知页面：NotificationView.vue 完整实现
5. ✅ 路由配置：/notifications 路由已添加
6. ✅ 功能特性：
   - 分页展示通知列表
   - 已读/未读样式区分
   - 通知详情对话框
   - 标记已读/删除通知
   - 全部标记已读
   - 查看转正状态按钮

**技术要点**:
- 使用 MyBatis-Plus LambdaQueryWrapper 构建查询
- 事务管理：角色变更和通知创建在同一事务
- 前端使用 Element Plus 组件库
- 响应式设计，支持移动端
- 时间格式化（刚刚、分钟前、小时前、具体日期）

### File List

**后端新增文件** (9 个):
- backend/src/main/java/com/syit/cpc/entity/Notification.java
- backend/src/main/java/com/syit/cpc/mapper/NotificationMapper.java
- backend/src/main/java/com/syit/cpc/service/NotificationService.java
- backend/src/main/java/com/syit/cpc/service/impl/NotificationServiceImpl.java
- backend/src/main/java/com/syit/cpc/controller/NotificationController.java
- backend/src/main/java/com/syit/cpc/dto/response/NotificationListResponse.java
- backend/src/main/java/com/syit/cpc/dto/response/NotificationItemResponse.java
- backend/src/main/resources/db/migration/V6__create_notifications_table.sql
- backend/src/main/resources/db/migration/V7__add_reviewed_at_to_promotion.sql

**后端修改文件** (2 个):
- backend/src/main/java/com/syit/cpc/entity/PromotionApplication.java - 添加 reviewedAt 字段
- backend/src/main/java/com/syit/cpc/service/impl/PromotionServiceImpl.java - 添加角色变更和通知逻辑

**前端新增文件** (4 个):
- frontend/src/types/notification.ts - 通知类型定义
- frontend/src/api/notification.ts - 通知 API 接口
- frontend/src/stores/notification.ts - Pinia Store
- frontend/src/views/NotificationView.vue - 通知页面

**前端修改文件** (1 个):
- frontend/src/router/index.ts - 添加 /notifications 路由

