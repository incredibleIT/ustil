---
stepsCompleted: [1]
inputDocuments:
  - PRD-syit-cpc.md
  - product-brief-syit-cpc.md
  - product-brief-syit-cpc-distillate.md
  - ux-design-specification.md
  - architecture.md
---

# 沈阳工业大学计算机程序设计社团官方网站 - Epic Breakdown

## Overview

本文档提供沈阳工业大学计算机程序设计社团官方网站的完整 Epic 和 Story 分解，将 PRD、UX 设计和架构文档中的需求分解为可实施的 Stories。

---

## Requirements Inventory

### Functional Requirements

**成员管理系统:**
- FR1: 用户注册（学号+姓名+邮箱+验证+初始角色预备成员）
- FR2: 用户登录（邮箱+密码+记住我7天+5次失败锁定15分钟+密码找回）
- FR3: 个人信息管理（修改头像、昵称、简介、密码、绑定邮箱+查看发布历史+查看审核记录）
- FR4: 成员列表管理（负责人查看所有成员+按角色筛选+搜索成员+修改成员角色）
- FR5: 账号禁用/启用（负责人权限）

**转正考核系统:**
- FR6: 转正申请提交（申请页面展示考核说明+提交项目作品GitHub链接或描述+状态追踪）
- FR7: 在线答题（题库管理+随机抽题+60分钟倒计时+客观题自动判卷+简答题人工评分）
- FR8: 项目评审提交（项目名称、描述、技术栈、链接+负责人评分百分制+评审意见）
- FR9: 转正审批（待审批列表+查看分数+设置通过分数线默认答题60%+项目40%总分≥70+审批通过/拒绝+结果通知）

**内容发布系统:**
- FR10: 资讯发布（富文本编辑器+标题摘要封面图+选择类型资讯/博客+草稿保存+预览）
- FR11: 博客发布（共用编辑器+代码块语法高亮+标签支持+作者自动关联）
- FR12: 内容编辑与删除（作者可编辑自己的内容重新进入审核+作者可删除+负责人可删除任何内容）

**内容审核系统:**
- FR13: 审核队列展示（待审核内容列表+按类型筛选+按时间排序+分页）
- FR14: 审核投票（内容详情页+通过/拒绝按钮+已投票状态标记+当前投票统计+超过20%通过自动发布+超过20%拒绝自动驳回+每人每篇文章只能投一票+负责人投票直接决定）
- FR15: 审核记录（个人审核历史+内容审核历史+审核统计通过率）

**内容展示:**
- FR16: 首页（社团介绍+最新资讯前5条+热门博客前5条+快捷入口+社团统计数据）
- FR17: 资讯列表页（列表展示封面标题摘要作者发布时间+分页每页10条+按时间排序）
- FR18: 博客列表页（列表展示标题摘要作者发布时间阅读数+分页每页10条+按时间排序）
- FR19: 内容详情页（标题作者发布时间+富文本内容渲染+代码块语法高亮+阅读数统计+分享功能）
- FR20: 个人主页（个人资料展示+该成员发布的内容列表+加入时间）

### Non-Functional Requirements

**性能需求:**
- NFR1: 页面加载时间 < 2秒
- NFR2: 支持并发用户 50+
- NFR3: 图片懒加载

**安全需求:**
- NFR4: 密码加密存储（bcrypt）
- NFR5: SQL注入防护
- NFR6: XSS防护
- NFR7: CSRF防护
- NFR8: 敏感操作需确认

**可用性需求:**
- NFR9: 响应式设计（支持移动端访问）
- NFR10: 浏览器兼容（Chrome、Firefox、Safari、Edge 最新两个版本）
- NFR11: 微信内置浏览器适配（页面正常显示、快速加载、分享卡片样式）
- NFR12: 错误提示友好

**可维护性需求:**
- NFR13: 代码注释完整
- NFR14: API文档完整（SpringDoc OpenAPI）
- NFR15: 数据库变更脚本（Flyway）

### Additional Requirements (from Architecture)

**技术架构要求:**
- 后端使用 Spring Boot 3.2.8 + Java 17 + Maven
- 数据访问使用 MyBatis-Plus 3.5.x（非 JPA）
- 数据库使用 MySQL 8.0
- 数据库迁移使用 Flyway
- 认证使用 JWT + Refresh Token（Access Token 24小时 + Refresh Token 7天）
- 授权使用 RBAC（ROLE_PROBATION / ROLE_MEMBER / ROLE_ADMIN）
- API 设计使用 RESTful + 统一响应格式
- 前端使用 Vue.js 3.4.x + TypeScript 5.x + Vite
- 状态管理使用 Pinia 分模块（auth/user/content/review）
- UI 组件库使用 Element Plus 2.5+ + TailwindCSS 3.4+
- 部署使用 Docker + Docker Compose

**缓存策略:**
- MVP 阶段无缓存，直接数据库查询
- Service 层预留 @Cacheable 注解
- Phase 2 触发条件：慢查询>200ms 或 QPS>100 或 日活>500

**数据一致性:**
- 数据库索引策略（users.email, content.status+created_at, review_votes.content_id+user_id）
- 缓存 Phase 2 使用 Cache-Aside 策略

### UX Design Requirements

**布局与导航:**
- UX-DR1: 内容优先布局（顶部极简导航 + 居中内容区 max-width: 768px）
- UX-DR2: 角色自适应界面（根据用户角色动态展示功能和信息）
- UX-DR3: 移动端底部标签栏导航（首页 | 发现 | +发布 | 审核 | 我的）
- UX-DR4: 微信环境适配（快速加载、分享卡片样式）

**核心组件:**
- UX-DR5: ContentCard 内容卡片组件（标题、摘要、作者、时间、状态）
- UX-DR6: ReviewCard 审核投票卡片（内容摘要、通过/拒绝按钮、实时统计、进度条）
- UX-DR7: RoleBadge 角色徽章组件（预备=灰色、正式=蓝色、负责人=金色）
- UX-DR8: MarkdownEditor 编辑器（极简工具栏、自动保存每30秒、代码块、图片上传）
- UX-DR9: ProgressSteps 进度步骤条（转正流程可视化、5个节点、当前高亮）

**交互体验:**
- UX-DR10: 转正流程可视化（步骤条 + 百分比 + 预计时间）
- UX-DR11: 审核投票 1秒完成设计（Optimistic UI、实时更新、3秒可撤销）
- UX-DR12: 内容发布 3步内完成（快速入口、极简编辑器、一键发布）
- UX-DR13: 成长仪式感设计（转正成功小庆祝动画、徽章授予）

**视觉设计:**
- UX-DR14: 主色调蓝色 #3B82F6（技术感）
- UX-DR15: 角色颜色标识（预备#9ca3af、正式#3b82f6、负责人#f59e0b）
- UX-DR16: 代码高亮（Prism.js 或 highlight.js）
- UX-DR17: 响应式断点（手机<768px、平板768-1024px、电脑>1024px）

---

## Epic List

1. **Epic 1: 项目初始化与基础架构搭建** - 建立开发环境、项目结构、数据库和基础配置
2. **Epic 2: 用户认证与成员管理** - 实现用户注册、登录、角色管理和个人信息
3. **Epic 3: 转正考核系统** - 实现转正申请、在线答题、项目提交和审批流程
4. **Epic 4: 内容发布与管理** - 实现资讯/博客的发布、编辑、草稿和预览功能
5. **Epic 5: 内容审核机制** - 实现 20%通过制的审核投票和审核记录
6. **Epic 6: 内容展示与发现** - 实现首页、列表页、详情页和个人主页
7. **Epic 7: 前端 UI 实现** - 实现设计系统、组件库和页面布局

---

## Epic 1: 项目初始化与基础架构搭建

**目标:** 建立完整的开发环境、项目结构、数据库和基础配置，为后续开发奠定基础。

### Story 1.1: 后端项目初始化

As a 开发团队,
I want 初始化 Spring Boot 项目并配置基础依赖,
So that 可以开始后端开发。

**Acceptance Criteria:**

**Given** 使用 Spring Initializr
**When** 创建项目并添加依赖
**Then** 生成包含 web, mysql, security, validation, lombok, devtools 的 Maven 项目
**And** 手动添加 MyBatis-Plus 3.5.5 和 Flyway 依赖

**Given** 项目创建完成
**When** 配置 application.yml
**Then** 设置数据库连接、MyBatis-Plus、JWT、Swagger 配置
**And** 创建 application-dev.yml 和 application-prod.yml 环境配置

### Story 1.2: 前端项目初始化

As a 开发团队,
I want 初始化 Vue.js 项目并配置基础依赖,
So that 可以开始前端开发。

**Acceptance Criteria:**

**Given** 使用 npm create vue@latest
**When** 创建项目
**Then** 选择 TypeScript, Router, Pinia, ESLint, Prettier
**And** 安装 Element Plus 和 TailwindCSS

**Given** 项目创建完成
**When** 配置 vite.config.ts
**Then** 设置 Element Plus 自动导入和 TailwindCSS 集成
**And** 配置路由和 Pinia Store 结构

### Story 1.3: 数据库设计与迁移脚本

As a 开发团队,
I want 设计数据库 Schema 并创建 Flyway 迁移脚本,
So that 数据库结构版本化管理。

**Acceptance Criteria:**

**Given** 根据 PRD 设计数据库
**When** 创建 V1__init_schema.sql
**Then** 包含 users, content, review_votes, promotion_applications, questions 表
**And** 设置主键、外键、索引和约束

**Given** 数据库表创建完成
**When** 创建 V2__seed_data.sql
**Then** 插入初始角色数据和测试数据
**And** 配置 Flyway 在应用启动时自动执行

### Story 1.4: 基础实体与 Mapper 创建

As a 开发团队,
I want 创建 MyBatis-Plus 实体类和 Mapper 接口,
So that 可以进行数据访问。

**Acceptance Criteria:**

**Given** 数据库表已设计
**When** 创建 Entity 类
**Then** 包含 User, Content, ReviewVote, PromotionApplication, Question 实体
**And** 使用 Lombok 简化代码

**Given** 实体类创建完成
**When** 创建 Mapper 接口
**Then** 继承 BaseMapper 获得基础 CRUD 能力
**And** 创建对应的 XML 映射文件用于复杂查询

### Story 1.5: 统一响应格式与异常处理

As a 开发团队,
I want 实现统一 API 响应格式和全局异常处理,
So that 前后端交互规范统一。

**Acceptance Criteria:**

**Given** 后端项目已初始化
**When** 创建 ApiResponse 包装类
**Then** 包含 code, message, data 字段
**And** 支持泛型数据类型

**Given** 响应格式已定义
**When** 创建 GlobalExceptionHandler
**Then** 使用 @ControllerAdvice 捕获所有异常
**And** 定义 BusinessException 和错误码枚举

---

## Epic 2: 用户认证与成员管理

**目标:** 实现用户注册、登录、JWT 认证、角色管理和个人信息功能。

### Story 2.1: JWT 认证基础设施

As a 开发团队,
I want 实现 JWT Token 生成和验证机制,
So that 可以保护 API 安全。

**Acceptance Criteria:**

**Given** 用户登录成功
**When** 生成 JWT Token
**Then** Access Token 有效期 24 小时
**And** Refresh Token 有效期 7 天

**Given** 请求受保护 API
**When** 验证 JWT Token
**Then** 解析 Token 获取用户信息
**And** Token 过期返回 401 未授权

**Given** Token 即将过期
**When** 使用 Refresh Token 换取新 Token
**Then** 生成新的 Access Token
**And** 保持用户登录状态

### Story 2.2: 用户注册功能

As a 预备成员,
I want 通过学号、姓名、邮箱注册账号,
So that 可以成为社团成员。

**Acceptance Criteria:**

**Given** 访问注册页面
**When** 填写学号、姓名、邮箱、密码
**Then** 密码强度要求至少8位包含字母和数字
**And** 邮箱格式验证

**Given** 提交注册表单
**When** 邮箱验证邮件发送
**Then** 点击验证链接激活账号
**And** 初始角色设置为预备成员

**Given** 邮箱已注册
**When** 再次使用相同邮箱注册
**Then** 返回错误提示邮箱已存在

### Story 2.3: 用户登录功能

As a 社团成员,
I want 通过邮箱和密码登录系统,
So that 可以访问我的账号。

**Acceptance Criteria:**

**Given** 访问登录页面
**When** 输入正确的邮箱和密码
**Then** 登录成功并返回 JWT Token
**And** 跳转到首页

**Given** 登录时选择"记住我"
**When** 登录成功
**Then** Refresh Token 有效期延长至 7 天
**And** 下次访问自动登录

**Given** 连续5次登录失败
**When** 第6次尝试登录
**Then** 账号锁定15分钟
**And** 显示锁定倒计时

### Story 2.4: 个人信息管理

As a 社团成员,
I want 管理我的个人资料,
So that 可以展示我的信息。

**Acceptance Criteria:**

**Given** 已登录账号
**When** 访问个人设置页面
**Then** 可以修改头像、昵称、个人简介
**And** 可以修改密码（需要旧密码验证）

**Given** 修改邮箱
**When** 输入新邮箱
**Then** 发送验证邮件到新邮箱
**And** 验证成功后更新邮箱

**Given** 访问个人中心
**When** 查看我的内容
**Then** 显示我发布的所有内容列表
**And** 显示我的审核投票历史

### Story 2.5: 成员列表与管理（负责人）

As a 负责人,
I want 管理所有社团成员,
So that 可以维护成员信息。

**Acceptance Criteria:**

**Given** 负责人登录
**When** 访问成员管理页面
**Then** 显示所有成员列表（头像、姓名、角色、加入时间）
**And** 支持按角色筛选

**Given** 在成员列表页面
**When** 搜索成员姓名
**Then** 实时过滤显示匹配结果

**Given** 查看成员详情
**When** 修改成员角色
**Then** 可以选择预备成员、正式成员、负责人
**And** 确认后更新角色

**Given** 成员管理页面
**When** 禁用某个成员账号
**Then** 该成员无法登录
**And** 可以重新启用账号

---

## Epic 3: 转正考核系统

**目标:** 实现转正申请、在线答题、项目提交和负责人审批的完整流程。

### Story 3.1: 转正申请入口与说明

As a 预备成员,
I want 了解转正要求并提交申请,
So that 可以成为正式成员。

**Acceptance Criteria:**

**Given** 预备成员登录
**When** 访问转正页面
**Then** 显示转正流程说明（答题+项目）
**And** 显示当前转正状态

**Given** 满足申请条件
**When** 点击开始转正
**Then** 创建转正申请记录
**And** 状态变为"答题中"

### Story 3.2: 题库管理与随机抽题

As a 负责人,
I want 维护转正考核题库,
So that 可以评估预备成员水平。

**Acceptance Criteria:**

**Given** 负责人登录
**When** 访问题库管理页面
**Then** 可以添加、编辑、删除题目
**And** 题目类型支持单选、多选、判断、简答

**Given** 题库中有题目
**When** 开始答题
**Then** 随机抽取指定数量题目生成试卷
**And** 每人每次题目不同

**Given** 简答题提交
**When** 负责人评分
**Then** 可以输入分数和评语

### Story 3.3: 在线答题功能

As a 预备成员,
I want 完成转正答题,
So that 获得答题分数。

**Acceptance Criteria:**

**Given** 开始答题
**When** 进入答题页面
**Then** 显示倒计时（默认60分钟）
**And** 显示当前题目序号

**Given** 答题过程中
**When** 选择答案
**Then** 客观题自动保存答案
**And** 简答题支持富文本输入

**Given** 时间结束
**When** 自动提交试卷
**Then** 客观题自动判卷
**And** 显示得分和答题结果

**Given** 答题分数不达标
**When** 查看错题解析
**Then** 显示正确答案和解析
**And** 可以重新答题

### Story 3.4: 项目提交与评审

As a 预备成员,
I want 提交项目作品,
So that 完成转正考核。

**Acceptance Criteria:**

**Given** 答题分数达标
**When** 进入项目提交页面
**Then** 填写项目名称、描述、技术栈
**And** 提供 GitHub 链接或详细文字描述

**Given** 提交项目
**When** 等待评审
**Then** 显示项目评审状态
**And** 显示预计评审时间

**Given** 负责人评审
**When** 查看项目详情
**Then** 可以评分（百分制）
**And** 可以填写评审意见

### Story 3.5: 转正审批与结果通知

As a 负责人,
I want 审批转正申请,
So that 决定预备成员是否转正。

**Acceptance Criteria:**

**Given** 负责人登录
**When** 访问转正审批页面
**Then** 显示待审批列表
**And** 显示申请者答题分数和项目分数

**Given** 查看申请详情
**When** 计算总分（答题60%+项目40%）
**Then** 默认及格线 70 分
**And** 可以调整分数线

**Given** 审批转正申请
**When** 点击通过
**Then** 申请者角色变为正式成员
**And** 发送通知（邮件+站内信）

**Given** 审批拒绝
**When** 点击拒绝
**Then** 填写拒绝原因
**And** 申请者可以查看反馈并重新申请

---

## Epic 4: 内容发布与管理

**目标:** 实现资讯/博客的发布、编辑、草稿保存和预览功能。

### Story 4.1: Markdown 编辑器组件

As a 开发团队,
I want 实现 Markdown 编辑器组件,
So that 用户可以发布内容。

**Acceptance Criteria:**

**Given** 进入内容发布页面
**When** 显示编辑器
**Then** 包含标题输入框和正文编辑区
**And** 工具栏支持粗体、斜体、代码、链接、图片

**Given** 编辑内容
**When** 每30秒自动保存
**Then** 保存为草稿到本地存储
**And** 恢复时提示有未保存草稿

**Given** 粘贴图片
**When** 自动上传
**Then** 显示上传进度
**And** 上传成功后插入图片链接

**Given** 点击预览
**When** 切换预览模式
**Then** 实时渲染 Markdown 内容
**And** 代码块显示语法高亮

### Story 4.2: 资讯发布功能

As a 社团成员,
I want 发布资讯,
So that 可以分享社团新闻和活动。

**Acceptance Criteria:**

**Given** 已登录账号
**When** 点击发布按钮选择资讯
**Then** 进入资讯编辑页面
**And** 类型自动设为资讯

**Given** 编辑资讯
**When** 输入标题、摘要、内容
**Then** 可以上传封面图
**And** 摘要自动生成或手动编辑

**Given** 点击发布
**When** 提交审核
**Then** 内容进入审核队列
**And** 显示"等待审核"状态

### Story 4.3: 博客发布功能

As a 社团成员,
I want 发布技术博客,
So that 可以分享学习心得。

**Acceptance Criteria:**

**Given** 已登录账号
**When** 点击发布按钮选择博客
**Then** 进入博客编辑页面
**And** 类型自动设为博客

**Given** 编辑博客
**When** 插入代码块
**Then** 支持语法高亮（Java、Python、C/C++、JavaScript）
**And** 可以添加标签

**Given** 发布博客
**When** 提交审核
**Then** 内容进入审核队列
**And** 作者信息自动关联

### Story 4.4: 内容编辑与删除

As a 内容作者,
I want 管理我发布的内容,
So that 可以修改或删除内容。

**Acceptance Criteria:**

**Given** 查看自己的内容
**When** 点击编辑
**Then** 进入编辑页面
**And** 加载原有内容

**Given** 编辑已发布内容
**When** 保存修改
**Then** 内容重新进入审核队列
**And** 显示"审核中"状态

**Given** 编辑审核中内容
**When** 保存修改
**Then** 更新审核队列中的内容
**And** 保留原有投票记录

**Given** 删除自己的内容
**When** 点击删除并确认
**Then** 内容标记为已删除
**And** 从公开列表中移除

**Given** 负责人登录
**When** 删除任何内容
**Then** 直接删除无需确认作者
**And** 可以填写删除原因

---

## Epic 5: 内容审核机制

**目标:** 实现 20%通过制的审核投票和审核记录功能。

### Story 5.1: 审核队列展示

As a 正式成员,
I want 查看待审核的内容列表,
So that 可以参与审核。

**Acceptance Criteria:**

**Given** 正式成员登录
**When** 访问审核中心
**Then** 显示待审核内容列表
**And** 显示标题、作者、类型、提交时间

**Given** 审核列表页面
**When** 按类型筛选
**Then** 可以筛选资讯或博客
**And** 可以按时间排序

**Given** 列表内容较多
**When** 分页展示
**Then** 每页显示10条
**And** 支持翻页

### Story 5.2: 审核投票功能

As a 正式成员,
I want 对内容进行投票,
So that 参与社区内容审核。

**Acceptance Criteria:**

**Given** 查看待审核内容
**When** 点击内容卡片
**Then** 显示内容详情
**And** 显示当前投票统计

**Given** 查看内容详情
**When** 点击通过按钮
**Then** 立即更新 UI 显示已投票
**And** 通过数+1

**Given** 点击拒绝按钮
**When** 3秒内
**Then** 显示撤销按钮
**And** 可以撤销投票

**Given** 已投票内容
**When** 再次查看
**Then** 显示"已通过"或"已拒绝"标记
**And** 禁用投票按钮

**Given** 通过票数超过20%
**When** 达到阈值
**Then** 内容自动发布
**And** 通知作者审核通过

**Given** 拒绝票数超过20%
**When** 达到阈值
**Then** 内容退回修改
**And** 通知作者审核拒绝

### Story 5.3: 负责人终审功能

As a 负责人,
I want 直接决定内容通过或拒绝,
So that 可以快速处理内容。

**Acceptance Criteria:**

**Given** 负责人登录
**When** 查看待审核内容
**Then** 投票按钮显示"直接通过"和"直接拒绝"

**Given** 点击直接通过
**When** 确认
**Then** 内容立即发布
**And** 无需等待20%阈值

**Given** 内容3天未达阈值
**When** 自动转负责人终审
**Then** 负责人收到待终审提醒
**And** 投票权重 = 5票

### Story 5.4: 审核记录与统计

As a 社团成员,
I want 查看审核记录,
So that 了解审核历史。

**Acceptance Criteria:**

**Given** 正式成员登录
**When** 访问个人中心
**Then** 显示"我的审核"标签
**And** 显示我投过的所有票

**Given** 查看内容详情
**When** 内容已通过或拒绝
**Then** 显示该内容的审核历史
**And** 显示每个投票人和投票结果

**Given** 正式成员查看统计
**When** 访问审核统计页面
**Then** 显示个人审核通过率
**And** 显示审核数量排名

---

## Epic 6: 内容展示与发现

**目标:** 实现首页、列表页、详情页和个人主页的内容展示功能。

### Story 6.1: 首页实现

As a 访客,
I want 访问首页,
So that 了解社团和查看最新内容。

**Acceptance Criteria:**

**Given** 访问首页
**When** 页面加载
**Then** 显示社团介绍区域
**And** 显示最新资讯前5条

**Given** 首页加载完成
**When** 滚动页面
**Then** 显示热门博客前5条
**And** 显示社团统计数据（成员数、内容数）

**Given** 不同角色访问
**When** 首页加载
**Then** 预备成员显示"申请转正"入口
**And** 正式成员显示"待审核数量"提醒
**And** 负责人显示"数据概览"

**Given** 点击快捷操作
**When** 点击发布按钮
**Then** 跳转到发布页面
**And** 点击审核中心跳转到审核页面

### Story 6.2: 资讯列表页

As a 访客,
I want 浏览所有资讯,
So that 了解社团动态。

**Acceptance Criteria:**

**Given** 访问资讯页面
**When** 页面加载
**Then** 显示资讯列表
**And** 每条显示封面、标题、摘要、作者、发布时间

**Given** 列表内容较多
**When** 分页展示
**Then** 每页10条
**And** 默认按时间倒序

**Given** 点击资讯卡片
**When** 进入详情页
**Then** 显示完整内容
**And** 显示阅读数

### Story 6.3: 博客列表页

As a 访客,
I want 浏览所有博客,
So that 学习技术知识。

**Acceptance Criteria:**

**Given** 访问博客页面
**When** 页面加载
**Then** 显示博客列表
**And** 每条显示标题、摘要、作者、发布时间、阅读数

**Given** 博客列表页面
**When** 按标签筛选
**Then** 可以筛选特定标签的博客
**And** 显示标签云

**Given** 点击博客标题
**When** 进入详情页
**Then** 显示完整内容
**And** 代码块语法高亮

### Story 6.4: 内容详情页

As a 访客,
I want 阅读完整内容,
So that 获取详细信息。

**Acceptance Criteria:**

**Given** 进入内容详情页
**When** 页面加载
**Then** 显示标题、作者、发布时间
**And** 显示富文本内容

**Given** 内容包含代码
**When** 渲染代码块
**Then** 显示语法高亮
**And** 支持复制代码

**Given** 阅读内容
**When** 滚动页面
**Then** 顶部显示阅读进度条
**And** 显示阅读数统计

**Given** 点击分享
**When** 选择分享方式
**Then** 可以复制链接
**And** 微信分享显示卡片样式

### Story 6.5: 个人主页

As a 访客,
I want 查看成员主页,
So that 了解该成员和TA的内容。

**Acceptance Criteria:**

**Given** 访问个人主页
**When** 页面加载
**Then** 显示个人资料（头像、昵称、简介、角色）
**And** 显示加入时间

**Given** 查看个人主页
**When** 滚动页面
**Then** 显示该成员发布的内容列表
**And** 可以按类型筛选

**Given** 点击内容
**When** 进入详情页
**Then** 正常显示内容
**And** 可以返回个人主页

---

## Epic 7: 前端 UI 实现

**目标:** 实现设计系统、组件库和页面布局。

### Story 7.1: 设计系统配置

As a 前端开发,
I want 配置设计系统,
So that 保持视觉一致性。

**Acceptance Criteria:**

**Given** 前端项目已初始化
**When** 配置 TailwindCSS
**Then** 设置主色调蓝色 #3B82F6
**And** 设置角色颜色（预备=灰、正式=蓝、负责人=金）

**Given** 配置 Element Plus
**When** 覆盖主题变量
**Then** 设置圆角 8px
**And** 设置基础字号 16px

**Given** 设计系统配置完成
**When** 创建全局样式文件
**Then** 定义字体栈、间距系统、响应式断点

### Story 7.2: 基础组件开发

As a 前端开发,
I want 开发基础 UI 组件,
So that 可以复用构建页面。

**Acceptance Criteria:**

**Given** 设计系统已配置
**When** 开发 RoleBadge 组件
**Then** 根据角色显示不同颜色
**And** 支持 sm/md/lg 尺寸

**Given** 开发 ContentCard 组件
**When** 传入内容数据
**Then** 显示标题、摘要、作者、时间
**And** 悬停显示阴影效果

**Given** 开发 ReviewCard 组件
**When** 传入审核数据
**Then** 显示内容摘要和投票按钮
**And** 实时显示投票统计和进度条

**Given** 开发 ProgressSteps 组件
**When** 传入步骤和当前进度
**Then** 显示步骤条
**And** 当前步骤高亮，已完成步骤打勾

### Story 7.3: 布局组件开发

As a 前端开发,
I want 开发布局组件,
So that 构建页面结构。

**Acceptance Criteria:**

**Given** 开发 AppHeader 组件
**When** 页面加载
**Then** 显示 Logo 和导航链接
**And** 滚动时半透明背景

**Given** 开发 BottomTabBar 组件
**When** 在移动端显示
**Then** 显示首页、发现、发布、审核、我的
**And** 根据角色显示/隐藏审核入口

**Given** 开发 MainLayout 组件
**When** 包裹页面内容
**Then** 居中显示内容区（max-width: 768px）
**And** 适配移动端和桌面端

### Story 7.4: 页面实现

As a 前端开发,
I want 实现所有页面,
So that 用户可以访问功能。

**Acceptance Criteria:**

**Given** 实现首页
**When** 访问 /
**Then** 显示社团介绍、最新内容、快捷入口
**And** 根据角色显示不同内容

**Given** 实现登录/注册页
**When** 访问 /login 或 /register
**Then** 显示表单
**And** 表单验证和错误提示

**Given** 实现内容发布页
**When** 访问 /publish
**Then** 显示 Markdown 编辑器
**And** 支持资讯/博客类型选择

**Given** 实现审核中心页
**When** 访问 /review
**Then** 显示待审核列表
**And** 正式成员可见

**Given** 实现个人中心页
**When** 访问 /profile
**Then** 显示个人信息和发布内容
**And** 支持编辑个人资料

### Story 7.5: 响应式适配

As a 前端开发,
I want 实现响应式设计,
So that 支持多设备访问。

**Acceptance Criteria:**

**Given** 在桌面端访问
**When** 屏幕宽度 > 1024px
**Then** 显示顶部导航栏
**And** 内容区居中 768px

**Given** 在平板端访问
**When** 屏幕宽度 768px-1024px
**Then** 导航栏可折叠
**And** 适当调整间距

**Given** 在手机端访问
**When** 屏幕宽度 < 768px
**Then** 显示底部标签栏
**And** 单列布局

**Given** 在微信内置浏览器访问
**When** 页面加载
**Then** 快速加载（< 2秒）
**And** 分享功能正常

---

## Summary

### Epic 统计

| Epic | Story 数量 | 优先级 |
|------|-----------|--------|
| Epic 1: 项目初始化与基础架构搭建 | 5 | P0 |
| Epic 2: 用户认证与成员管理 | 5 | P0 |
| Epic 3: 转正考核系统 | 5 | P1 |
| Epic 4: 内容发布与管理 | 4 | P0 |
| Epic 5: 内容审核机制 | 4 | P0 |
| Epic 6: 内容展示与发现 | 5 | P0 |
| Epic 7: 前端 UI 实现 | 5 | P0 |

**总计: 33 个 Stories**

### 实施建议

**Phase 1 (MVP) 实施顺序:**
1. Epic 1: 项目初始化（基础架构）
2. Epic 2: 用户认证（核心功能）
3. Epic 7: 前端 UI（基础界面）
4. Epic 4: 内容发布（核心功能）
5. Epic 5: 内容审核（核心功能）
6. Epic 6: 内容展示（核心功能）
7. Epic 3: 转正考核（增值功能）

---

**文档状态:** 完成
**最后更新:** 2026-04-03
**版本:** 1.0
