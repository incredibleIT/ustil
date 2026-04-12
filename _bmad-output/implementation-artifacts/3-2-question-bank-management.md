---
story_id: "3.2"
story_key: "3-2-question-bank-management"
epic: "转正考核系统"
status: review
created: "2026-04-07"
---

# Story 3.2: 题库管理与随机抽题

As a 负责人,
I want 维护转正考核题库,
So that 可以评估预备成员水平。

## Acceptance Criteria

### AC1: 题目 CRUD 操作
**Given** 负责人登录系统
**When** 访问题库管理页面 `/admin/questions`
**Then** 可以查看题目列表（分页，每页10条）
**And** 可以添加新题目（包括题干、题型、选项、答案、分值）
**And** 可以编辑现有题目
**And** 可以删除题目（软删除）

### AC2: 题目类型支持
**Given** 添加或编辑题目
**When** 选择题型
**Then** 支持以下题型：
  - `single_choice` - 单选题（4个选项，1个正确答案）
  - `multiple_choice` - 多选题（4-6个选项，2-4个正确答案）
  - `true_false` - 判断题（2个选项：对/错）
  - `short_answer` - 简答题（无选项，需人工评分）
**And** 不同题型显示不同的表单字段

### AC3: 题库查询与筛选
**Given** 负责人访问题库管理页面
**When** 使用筛选功能
**Then** 可以按题型筛选（单选/多选/判断/简答）
**And** 可以按关键词搜索题目内容
**And** 显示题目总数和各题型统计

### AC4: 随机抽题算法
**Given** 预备成员开始答题
**When** 调用抽题接口
**Then** 从题库中随机抽取指定数量题目：
  - 单选题：15题（每题4分，共60分）
  - 多选题：5题（每题4分，共20分）
  - 判断题：10题（每题2分，共20分）
**And** 每人每次抽取的题目不同（基于用户ID+时间戳种子）
**And** 不返回简答题和正确答案
**And** 记录抽取的题目ID列表到 exam_records 表

### AC5: 批量导入题目（可选）
**Given** 负责人有题目 Excel/CSV 文件
**When** 上传批量导入文件
**Then** 系统解析并验证题目格式
**And** 显示预览和错误提示
**And** 确认后批量插入题库

## Tasks / Subtasks

### Task 1: 后端 - 题库管理接口 (AC: 1, 2, 3)
- [x] 1.1 创建 QuestionService 接口和实现类
- [x] 1.2 实现题目分页查询（支持题型筛选和关键词搜索）
- [x] 1.3 实现题目创建接口（POST /api/admin/questions）
- [x] 1.4 实现题目更新接口（PUT /api/admin/questions/{id}）
- [x] 1.5 实现题目删除接口（DELETE /api/admin/questions/{id}）
- [x] 1.6 实现题目详情查询接口（GET /api/admin/questions/{id}）
- [x] 1.7 添加 @PreAuthorize("hasRole('ADMIN')") 权限控制

### Task 2: 后端 - 随机抽题接口 (AC: 4)
- [x] 2.1 实现随机抽题算法（基于 Collections.shuffle）
- [x] 2.2 创建 GET /api/exam/generate-paper 接口
- [x] 2.3 抽题时过滤简答题和正确答案
- [ ] 2.4 创建 ExamRecord 记录抽取的题目列表（Story 3-3 实现）
- [x] 2.5 添加 @PreAuthorize("hasRole('PROBATION')") 权限控制

### Task 3: 后端 - DTO 和验证
- [x] 3.1 创建 CreateQuestionRequest DTO（含 @Valid 验证）
- [x] 3.2 创建 UpdateQuestionRequest DTO
- [x] 3.3 创建 QuestionResponse DTO
- [x] 3.4 创建 PaperResponse DTO（试卷结构，不含答案）
- [x] 3.5 添加题型枚举校验（single_choice/multiple_choice/true_false/short_answer）
- [x] 3.6 添加答案格式校验（根据题型验证答案合法性）

### Task 4: 后端 - 错误码扩展
- [x] 4.1 在 ErrorCode.java 中添加题库相关错误码

### Task 5: 前端 - 题库管理页面 (AC: 1, 2, 3)
- [x] 5.1 创建 QuestionsView.vue 页面组件
- [x] 5.2 实现题目列表表格（分页、筛选）
- [x] 5.3 实现添加题目对话框（动态表单，根据题型显示不同字段）
- [x] 5.4 实现编辑题目对话框
- [x] 5.5 实现删除确认对话框
- [x] 5.6 添加题型筛选下拉框和关键词搜索框
- [x] 5.7 显示题目统计信息（总数、各题型数量）

### Task 6: 前端 - API 模块和类型定义
- [x] 6.1 创建 frontend/src/api/question.ts API 模块
- [x] 6.2 封装 getQuestions、createQuestion、updateQuestion、deleteQuestion API
- [x] 6.3 创建 frontend/src/api/exam.ts API 模块
- [x] 6.4 封装 generatePaper API
- [x] 6.5 创建 frontend/src/types/question.ts 类型定义
- [x] 6.6 定义 QuestionType、Question、Paper 等 TypeScript 类型

### Task 7: 前端 - 路由和权限
- [x] 7.1 在 router/index.ts 中添加 `/admin/questions` 路由
- [x] 7.2 添加 meta.requiresAuth = true 和 meta.requiresRole = 'ROLE_ADMIN'
- [x] 7.3 在首页导航添加"题库管理"入口（仅负责人可见）

### Review Follow-ups (AI)
- [x] [AI-Review][High] 1. 多选题答案排序一致性修复：在 createQuestion 和 updateQuestion 中对多选题答案进行排序后再存储
- [x] [AI-Review][High] 2. 前端统计查询优化：删除低效的 fetchQuestionStats()，改为从分页结果更新统计信息
- [x] [AI-Review][Med] 3. 添加题目重复检查：在 createQuestion 中检查相同题干，防止重复添加
- [x] [AI-Review][Med] 4. 前端题型切换逻辑优化：抽取 getInitialOptionsByType 函数提高可读性
- [x] [AI-Review][Med] 5. UpdateQuestionRequest DTO 添加安全注释
- [x] [AI-Review][Low] 6. ExamController 错误响应格式统一：改用 BusinessException 替代直接返回 ApiResponse.error

## Dev Notes

### 技术要点

**后端实现**:
- 使用 MyBatis-Plus 的 Page 对象进行分页查询
- 使用 LambdaQueryWrapper 构建动态查询条件
- 随机抽题算法示例：
  ```java
  // 基于用户ID和时间戳生成随机种子
  long seed = userId.hashCode() ^ System.currentTimeMillis();
  Random random = new Random(seed);
  
  // 从题库中随机抽取
  List<Question> questions = questionMapper.selectList(wrapper);
  Collections.shuffle(questions, random);
  return questions.subList(0, Math.min(count, questions.size()));
  ```
- 题型验证逻辑：
  - 单选题：options.size() == 4, correctAnswer 为单个字母（A/B/C/D）
  - 多选题：options.size() >= 4, correctAnswer 为多个字母（如"ABC"）
  - 判断题：options = ["对", "错"], correctAnswer 为 "对" 或 "错"
  - 简答题：options 为 null, correctAnswer 为 null（人工评分）
- 参考已有代码：Story 2.5 的 UserServiceImpl（分页查询模式）

**前端实现**:
- 使用 Element Plus Table + Pagination 组件
- 使用 Element Plus Dialog + Form 组件
- 动态表单：根据 questionType 显示/隐藏选项字段
- 参考已有代码：Story 2.5 的 MembersView.vue（列表+筛选模式）

### 题目表单设计

**单选题/多选题表单**:
- 题目内容（TextArea）
- 题型选择（Radio: single_choice / multiple_choice）
- 选项列表（4个输入框，动态增删）
- 正确答案（多选时使用 CheckboxGroup）
- 分值（NumberInput，默认10）

**判断题表单**:
- 题目内容（TextArea）
- 题型选择（Radio: true_false，自动选中）
- 正确答案（Radio: 对 / 错）
- 分值（NumberInput，默认10）

**简答题表单**:
- 题目内容（TextArea）
- 题型选择（Radio: short_answer，自动选中）
- 评分标准（TextArea，可选）
- 分值（NumberInput，默认10）

### API 接口设计

**1. 分页查询题目列表**
```
GET /api/admin/questions?page=1&size=10&type=single_choice&keyword=关键词
```

Response:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "pages": 5,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "questionText": "Java 中 String 是基本数据类型吗？",
        "questionType": "true_false",
        "options": ["对", "错"],
        "correctAnswer": "错",
        "score": 10,
        "createdAt": "2026-04-01T10:00:00"
      }
    ]
  }
}
```

**2. 创建题目**
```
POST /api/admin/questions
{
  "questionText": "以下哪些是 Java 的集合类？",
  "questionType": "multiple_choice",
  "options": ["ArrayList", "HashMap", "String", "HashSet"],
  "correctAnswer": "ABD",
  "score": 10
}
```

**3. 更新题目**
```
PUT /api/admin/questions/{id}
{
  "questionText": "...",
  "questionType": "...",
  "options": [...],
  "correctAnswer": "...",
  "score": 10
}
```

**4. 删除题目**
```
DELETE /api/admin/questions/{id}
```

**5. 随机生成试卷**
```
GET /api/exam/generate-paper
```

Response:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "paperId": 1,
    "questions": [
      {
        "id": 1,
        "questionText": "Java 中 String 是基本数据类型吗？",
        "questionType": "true_false",
        "options": ["对", "错"],
        "score": 10
        // 注意：不包含 correctAnswer
      }
    ],
    "totalScore": 100,
    "duration": 60
  }
}
```

### 数据库查询示例

**分页查询题目（带筛选）**:
```java
Page<Question> page = new Page<>(current, size);
LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
if (StringUtils.isNotBlank(type)) {
    wrapper.eq(Question::getQuestionType, type);
}
if (StringUtils.isNotBlank(keyword)) {
    wrapper.like(Question::getQuestionText, keyword);
}
wrapper.orderByDesc(Question::getCreatedAt);
Page<Question> result = questionMapper.selectPage(page, wrapper);
```

**随机抽取题目**:
```java
// 抽取单选题
LambdaQueryWrapper<Question> singleWrapper = new LambdaQueryWrapper<>();
singleWrapper.eq(Question::getQuestionType, "single_choice");
List<Question> singleQuestions = questionMapper.selectList(singleWrapper);
Collections.shuffle(singleQuestions);
List<Question> selected = singleQuestions.subList(0, Math.min(15, singleQuestions.size()));
```

### 权限控制策略

**后端**:
```java
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin/questions")
public class QuestionAdminController {
    // 题库管理接口，仅负责人可访问
}

@PreAuthorize("hasRole('PROBATION')")
@RestController
@RequestMapping("/api/exam")
public class ExamController {
    // 考试相关接口，仅预备成员可访问
}
```

**前端**:
```typescript
// 路由配置
{
  path: '/admin/questions',
  name: 'questions',
  component: () => import('../views/QuestionsView.vue'),
  meta: { 
    requiresAuth: true,
    requiresRole: 'ROLE_ADMIN'
  }
}

// 导航栏角色判断
const authStore = useAuthStore()
const isAdmin = computed(() => authStore.user?.role === 'ROLE_ADMIN')
```

## Project Structure Notes

**后端新增文件**:
- `backend/src/main/java/com/syit/cpc/controller/QuestionAdminController.java` - 题库管理控制器
- `backend/src/main/java/com/syit/cpc/controller/ExamController.java` - 考试控制器
- `backend/src/main/java/com/syit/cpc/service/QuestionService.java` - 题库服务接口
- `backend/src/main/java/com/syit/cpc/service/impl/QuestionServiceImpl.java` - 题库服务实现
- `backend/src/main/java/com/syit/cpc/dto/request/CreateQuestionRequest.java` - 创建题目请求
- `backend/src/main/java/com/syit/cpc/dto/request/UpdateQuestionRequest.java` - 更新题目请求
- `backend/src/main/java/com/syit/cpc/dto/response/QuestionResponse.java` - 题目响应
- `backend/src/main/java/com/syit/cpc/dto/response/PaperResponse.java` - 试卷响应

**后端修改文件**:
- `backend/src/main/java/com/syit/cpc/common/exception/ErrorCode.java` - 添加题库相关错误码
- `backend/src/main/java/com/syit/cpc/config/SecurityConfig.java` - 添加 /api/admin/questions/** 和 /api/exam/** 路径配置

**前端新增文件**:
- `frontend/src/views/QuestionsView.vue` - 题库管理页面
- `frontend/src/api/question.ts` - 题库 API 模块
- `frontend/src/api/exam.ts` - 考试 API 模块
- `frontend/src/types/question.ts` - 题库 TypeScript 类型定义

**前端修改文件**:
- `frontend/src/router/index.ts` - 添加 /admin/questions 路由
- 导航栏组件 - 添加负责人的"题库管理"入口

## References

- [Source: epics.md#Story 3.2] - 原始 Story 定义和 Acceptance Criteria
- [Source: docs/architecture.md#项目结构与边界] - 完整项目目录结构
- [Source: docs/architecture.md#认证与安全] - RBAC 权限控制策略
- [Source: docs/architecture.md#API 与通信] - RESTful API 设计规范
- [Source: backend/src/main/java/com/syit/cpc/entity/Question.java] - Question 实体类定义
- [Source: backend/src/main/java/com/syit/cpc/mapper/QuestionMapper.java] - Question Mapper
- [Source: backend/src/main/resources/db/migration/V1__init_schema.sql#L120-L137] - questions 表 Schema
- [Source: 3-1-promotion-application-entry.md] - 参考转正申请流程和权限控制模式
- [Source: 2-5-member-list-management.md] - 参考分页查询、筛选、权限控制模式

## Testing Standards

**后端测试**:
- 使用 JUnit 5 + Mockito 编写单元测试
- 测试题目 CRUD 操作
- 测试题型验证逻辑（单选/多选/判断/简答）
- 测试答案格式校验
- 测试随机抽题算法（题目不重复、数量正确、不含答案）
- 测试权限控制（非 ADMIN 角色访问题库管理应被拒绝）
- 测试题库不足时生成试卷的错误处理

**前端测试**:
- 测试题目列表加载和分页
- 测试题型筛选和关键词搜索
- 测试添加题目表单验证
- 测试编辑题目功能
- 测试删除确认逻辑
- 测试角色自适应（负责人 vs 其他角色）

## Dev Agent Record

### Agent Choice Validation

#### Chosen Stack
- **Backend**: Java 17, Spring Boot 3.5.0, MyBatis-Plus 3.5.9, MySQL 8.0
- **Frontend**: Vue.js 3.5.22, TypeScript, Vite 7.1.5, Element Plus, Axios
- **State Management**: Pinia 3.0.3
- **Routing**: Vue Router 4.6.3
- **Validation**: Bean Validation (Hibernate Validator)

#### Choice Validation
- 分离 QuestionAdminController 和 ExamController：遵循单一职责原则，管理和考试逻辑分离
- 复用 Question 实体和 QuestionMapper：Story 1.4 已创建，直接扩展
- 使用动态表单：根据题型显示不同字段，提升用户体验
- 随机抽题在后端实现：保证公平性和安全性，前端无法预测题目

### Change Log
| Change | Description | Reason | Impact |
|--------|-------------|--------|--------|
| 新增 | ErrorCode.java | 添加题库相关错误码（3001-3005） | 扩展错误处理 |
| 新增 | CreateQuestionRequest.java | 创建题目请求 DTO | 支持题目创建 |
| 新增 | UpdateQuestionRequest.java | 更新题目请求 DTO | 支持题目更新 |
| 新增 | QuestionResponse.java | 题目响应 DTO | 题目数据返回 |
| 新增 | QuestionListResponse.java | 分页列表响应 DTO | 支持分页查询 |
| 新增 | PaperResponse.java | 试卷响应 DTO | 不含正确答案 |
| 新增 | QuestionService.java | 题库服务接口 | 定义题库操作 |
| 新增 | QuestionServiceImpl.java | 题库服务实现 | CRUD + 随机抽题 |
| 新增 | QuestionAdminController.java | 题库管理控制器 | ADMIN 权限 |
| 新增 | ExamController.java | 考试控制器 | PROBATION 权限 |
| 新增 | QuestionsView.vue | 题库管理页面 | 前端主界面 |
| 新增 | question.ts (api) | 题库 API 模块 | 前端接口封装 |
| 新增 | exam.ts (api) | 考试 API 模块 | 抽题接口封装 |
| 新增 | question.ts (types) | TypeScript 类型定义 | 类型安全 |
| 修改 | router/index.ts | 添加 /admin/questions 路由 | 支持题库页面访问 |
| 修改 | HomeView.vue | 添加题库管理入口 | 导航优化 |

## Senior Developer Review (AI)

### Review Summary
**Review Date**: 2026-04-08  
**Reviewer**: AI Code Reviewer  
**Outcome**: Changes Requested  
**Total Action Items**: 6 (2 High, 3 Medium, 1 Low)  
**Status**: ✅ All items resolved

### Action Items
| # | Severity | Description | Status | Related AC/File |
|---|----------|-------------|--------|----------------|
| 1 | High | 多选题答案排序不一致：前端排序提交但后端未排序存储，编辑时显示混乱 | ✅ Resolved | AC2/QuestionServiceImpl.java |
| 2 | High | 前端统计查询效率低且不准确：size=1000 全量查询，题库超1000题统计不准 | ✅ Resolved | AC3/QuestionsView.vue |
| 3 | Med | 缺少题目重复检查：可创建相同题干的题目 | ✅ Resolved | AC1/QuestionServiceImpl.java |
| 4 | Med | 前端题型切换逻辑可读性差：硬编码 if-else 分支 | ✅ Resolved | AC2/QuestionsView.vue |
| 5 | Med | UpdateQuestionRequest 缺少安全注释说明 | ✅ Resolved | AC1/UpdateQuestionRequest.java |
| 6 | Low | ExamController 错误响应格式不一致：直接返回 vs 全局异常处理 | ✅ Resolved | AC4/ExamController.java |

### Additional Details

#### 题库数据初始化
建议创建 Flyway 迁移脚本 V6__seed_questions.sql，预置 50-100 道测试题目，包含：
- 30 道单选题（Java 基础、面向对象、集合框架）
- 10 道多选题（并发编程、设计模式）
- 10 道判断题（语法、概念）
- 10 道简答题（编程题、系统设计）

#### 性能考虑
- 题库数量 < 1000：直接查询 + 内存 shuffle
- 题库数量 > 1000：使用 MySQL ORDER BY RAND()  LIMIT n
- 当前阶段预计题库 < 500，使用内存 shuffle 即可

## Story Completion Status

Status: review

### Acceptance Criteria Validation

| AC | 测试内容 | 结果 | 备注 |
|----|----------|------|------|
| AC1 | 题目 CRUD 操作 | ⏸️ 待测试 | 需启动前后端验证 |
| AC2 | 题目类型支持 | ⏸️ 待测试 | 需启动前后端验证 |
| AC3 | 题库查询与筛选 | ⏸️ 待测试 | 需启动前后端验证 |
| AC4 | 随机抽题算法 | ⏸️ 待测试 | 需预备成员账号验证 |
| AC5 | 批量导入题目 | ⏸️ 可选 | MVP 可跳过 |

### Questions & Clarifications
- Q: 判断题的 correctAnswer 存储格式？
  A: 存储为 "对" 或 "错"（与 options 保持一致）
- Q: 多选题的 correctAnswer 格式？
  A: 存储为 "ABC"（字母拼接，便于比较）
- Q: 简答题如何评分？
  A: Story 3.4 中由负责人手动评分，此处只需支持题目创建

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->
