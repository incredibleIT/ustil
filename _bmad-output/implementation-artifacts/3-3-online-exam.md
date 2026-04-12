---
story_id: "3.3"
story_key: "3-3-online-exam"
epic: "转正考核系统"
status: ready-for-dev
created: "2026-04-08"
---

# Story 3.3: 在线答题功能

As a 预备成员,
I want 完成转正答题,
so that 获得答题分数并进入下一考核阶段。

## Acceptance Criteria

### AC1: 考试页面展示
**Given** 预备成员点击"开始答题"
**When** 进入考试页面 `/exam`
**Then** 显示试卷信息（总分、考试时长）
**And** 显示倒计时（默认60分钟）
**And** 按题型分类展示题目（单选题、多选题、判断题）
**And** 每道题显示题号、题干、选项、分值

### AC2: 答题与自动保存
**Given** 预备成员在答题过程中
**When** 选择答案
**Then** 客观题（单选/多选/判断）答案实时保存到前端
**And** 多选题需选择2-4个选项
**And** 可以随时修改已选答案
**And** 显示答题进度（已答/总题数）

### AC3: 时间控制与自动提交
**Given** 考试进行中
**When** 倒计时结束（00:00）
**Then** 自动提交试卷
**And** 显示"考试时间到，自动提交试卷"提示
**And** 倒计时少于5分钟时，时间显示为橙色并闪烁

### AC4: 提交试卷与客观题自动判卷
**Given** 预备成员点击"提交试卷"
**When** 确认提交
**Then** 将答案发送到后端
**And** 后端对客观题自动判卷（单选/多选/判断）
**And** 记录考试结果到 exam_records 表
**And** 更新转正申请状态为 pending_project（待提交项目）
**And** 返回得分和答题详情

### AC5: 查看考试结果与错题解析
**Given** 考试已提交
**When** 访问考试结果页面
**Then** 显示总分和各题型得分
**And** 显示每道题的正确答案和自己的答案
**And** 错误的题目显示正确答案解析
**And** 根据分数判断是否达标（>=60分）

### AC6: 重新答题
**Given** 考试分数不达标（<60分）
**When** 点击"重新答题"
**Then** 重新生成一套试卷
**And** 记录考试次数
**And** 可以多次答题取最高分

## Tasks / Subtasks

### Task 1: 后端 - 提交试卷接口 (AC: 4, 5)
- [x] 1.1 创建 SubmitExamRequest DTO（paperId, answers 列表）
- [x] 1.2 在 ExamController 添加 POST /api/exam/submit 接口
- [x] 1.3 实现客观题自动判卷逻辑（单选/多选/判断）
- [x] 1.4 记录考试结果到 exam_records 表（用户ID、得分、答案、用时）
- [x] 1.5 添加 @PreAuthorize("hasRole('PROBATION')") 权限控制

### Task 2: 后端 - 考试记录管理 (AC: 4, 5, 6)
- [x] 2.1 创建 ExamRecord 实体类（关联 exam_records 表）
- [x] 2.2 创建 ExamRecordMapper 接口
- [x] 2.3 实现保存考试记录方法
- [x] 2.4 实现查询用户考试记录方法（获取最高分）
- [x] 2.5 更新 PromotionApplication 状态为 pending_project

### Task 3: 后端 - 获取考试结果 (AC: 5)
- [x] 3.1 在 ExamController 添加 GET /api/exam/result 接口
- [x] 3.2 返回考试得分、答题详情、正确答案对比
- [x] 3.3 创建 ExamResultResponse DTO

### Task 4: 后端 - 错误处理与验证 (AC: 4)
- [x] 4.1 验证试卷有效性（paperId 是否存在）
- [x] 4.2 验证答案完整性（题目数量匹配）
- [x] 4.3 添加考试重复提交防护
- [x] 4.4 扩展 ErrorCode 添加考试相关错误码

### Task 5: 前端 - 连接提交接口 (AC: 2, 3, 4)
- [x] 5.1 在 api/exam.ts 添加 submitExam 函数
- [x] 5.2 更新 ExamView.vue handleSubmit 方法调用真实接口
- [x] 5.3 提交成功后跳转到结果页面
- [x] 5.4 添加提交加载状态

### Task 6: 前端 - 考试结果页面 (AC: 5, 6)
- [x] 6.1 创建 ExamResultView.vue 页面
- [x] 6.2 显示总分、各题型得分、通过状态
- [x] 6.3 显示每道题的答题详情（正确答案 vs 用户答案）
- [x] 6.4 错误题目显示正确答案解析
- [x] 6.5 添加"重新答题"按钮（分数不达标时）
- [x] 6.6 添加"继续转正流程"按钮（分数达标时）

### Task 7: 前端 - 路由和导航 (AC: 1, 5, 6)
- [x] 7.1 在 router/index.ts 添加 `/exam/result` 路由
- [x] 7.2 添加 meta.requiresAuth = true
- [x] 7.3 在 PromotionView 添加"查看成绩"按钮

## Dev Notes

### 技术要点

**后端实现**:
- 判卷逻辑示例：
  ```java
  // 对比答案（多选题答案已排序存储）
  private boolean checkAnswer(String userAnswer, String correctAnswer, String questionType) {
      if ("multiple_choice".equals(questionType)) {
          // 多选题：答案必须完全匹配（已排序）
          return userAnswer.equals(correctAnswer);
      } else {
          // 单选和判断：精确匹配
          return userAnswer.equals(correctAnswer);
      }
  }
  ```
- 考试记录存储：
  ```java
  // exam_records 表结构
  // id, user_id, paper_id, answers(JSON), score, max_score, passed, 
  // started_at, completed_at, duration, attempt_count, created_at, updated_at
  ```
- 参考已有代码：Story 3.2 的 QuestionServiceImpl（随机抽题模式）

**前端实现**:
- 考试结果页面展示：
  - 总分卡片（大号字体，通过/不通过颜色区分）
  - 答题详情表格（题号、题型、用户答案、正确答案、是否正确、分值）
  - 错题解析区域（错误题目单独展示）
- 参考已有代码：ExamView.vue（答题页面已创建）

### 判卷逻辑设计

**客观题判卷规则**:
- 单选题：答案完全匹配（A/B/C/D）
- 多选题：答案完全匹配（ABC/ABD等，已排序存储）
- 判断题：答案完全匹配（对/错）
- 简答题：Story 3.4 中由负责人手动评分，此处不计入

**分数计算**:
- 单选题：15题 × 4分 = 60分
- 多选题：5题 × 4分 = 20分
- 判断题：10题 × 2分 = 20分
- 总分：100分
- 及格线：60分（可配置）

### API 接口设计

**1. 提交试卷**
```
POST /api/exam/submit
Authorization: Bearer {token}
Content-Type: application/json

{
  "paperId": 1,
  "answers": [
    { "questionId": 1, "answer": "A" },
    { "questionId": 2, "answer": "B" },
    { "questionId": 3, "answer": "AC" }
  ]
}
```

Response:
```json
{
  "code": 200,
  "message": "试卷提交成功",
  "data": {
    "examRecordId": 1,
    "score": 85,
    "maxScore": 100,
    "passed": true,
    "singleChoiceScore": 56,
    "multipleChoiceScore": 16,
    "trueFalseScore": 13,
    "details": [
      {
        "questionId": 1,
        "questionText": "Java中哪个关键字表示继承关系?",
        "questionType": "single_choice",
        "userAnswer": "A",
        "correctAnswer": "A",
        "correct": true,
        "score": 4
      }
    ]
  }
}
```

**2. 获取考试结果**
```
GET /api/exam/result
Authorization: Bearer {token}
```

Response:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "examRecordId": 1,
    "score": 85,
    "maxScore": 100,
    "passed": true,
    "attemptCount": 1,
    "startedAt": "2026-04-08T10:00:00",
    "completedAt": "2026-04-08T10:45:00",
    "duration": 45,
    "details": [...]
  }
}
```

### 数据库设计

**exam_records 表** (已在 V1__init_schema.sql 中定义):
```sql
CREATE TABLE IF NOT EXISTS exam_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '考试记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    paper_id BIGINT COMMENT '试卷ID',
    answers JSON COMMENT '用户答案',
    score INT COMMENT '得分',
    max_score INT DEFAULT 100 COMMENT '满分',
    passed BOOLEAN COMMENT '是否通过',
    started_at TIMESTAMP COMMENT '开始时间',
    completed_at TIMESTAMP COMMENT '完成时间',
    duration INT COMMENT '用时（分钟）',
    attempt_count INT DEFAULT 1 COMMENT '考试次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_exam_user_id (user_id),
    INDEX idx_exam_passed (passed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试记录表';
```

### 权限控制策略

**后端**:
```java
@PreAuthorize("hasRole('PROBATION')")
@RestController
@RequestMapping("/api/exam")
public class ExamController {
    // 考试相关接口，仅预备成员可访问
}
```

**前端**:
```typescript
{
  path: '/exam',
  name: 'exam',
  component: () => import('../views/ExamView.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/exam/result',
  name: 'examResult',
  component: () => import('../views/ExamResultView.vue'),
  meta: { requiresAuth: true }
}
```

## Project Structure Notes

**后端新增文件**:
- `backend/src/main/java/com/syit/cpc/entity/ExamRecord.java` - 考试记录实体
- `backend/src/main/java/com/syit/cpc/mapper/ExamRecordMapper.java` - 考试记录 Mapper
- `backend/src/main/java/com/syit/cpc/dto/request/SubmitExamRequest.java` - 提交试卷请求
- `backend/src/main/java/com/syit/cpc/dto/response/ExamResultResponse.java` - 考试结果响应
- `backend/src/main/java/com/syit/cpc/dto/response/ExamDetailItem.java` - 答题详情项

**后端修改文件**:
- `backend/src/main/java/com/syit/cpc/controller/ExamController.java` - 添加提交和查询结果接口
- `backend/src/main/java/com/syit/cpc/service/QuestionService.java` - 添加提交试卷方法
- `backend/src/main/java/com/syit/cpc/service/impl/QuestionServiceImpl.java` - 实现判卷逻辑
- `backend/src/main/java/com/syit/cpc/common/exception/ErrorCode.java` - 添加考试相关错误码

**前端新增文件**:
- `frontend/src/views/ExamResultView.vue` - 考试结果页面
- `frontend/src/types/exam.ts` - 考试相关 TypeScript 类型

**前端修改文件**:
- `frontend/src/views/ExamView.vue` - 连接提交接口（已完成80%）
- `frontend/src/api/exam.ts` - 添加 submitExam 和 getExamResult 函数
- `frontend/src/router/index.ts` - 添加 /exam/result 路由
- `frontend/src/views/PromotionView.vue` - 添加"查看成绩"按钮

## References

- [Source: epics.md#Story 3.3] - 原始 Story 定义和 Acceptance Criteria
- [Source: 3-2-question-bank-management.md] - 参考随机抽题和试卷生成逻辑
- [Source: docs/architecture.md#API 与通信] - RESTful API 设计规范
- [Source: docs/architecture.md#认证与安全] - RBAC 权限控制策略
- [Source: backend/src/main/resources/db/migration/V1__init_schema.sql#L140-L158] - exam_records 表 Schema
- [Source: backend/src/main/java/com/syit/cpc/controller/ExamController.java] - ExamController 现有代码
- [Source: frontend/src/views/ExamView.vue] - 考试页面已创建（提交功能未实现）
- [Source: frontend/src/api/exam.ts] - 考试 API 模块
- [Source: backend/src/main/java/com/syit/cpc/entity/Question.java] - Question 实体类
- [Source: backend/src/main/java/com/syit/cpc/entity/PromotionApplication.java] - 转正申请实体

## Testing Standards

**后端测试**:
- 使用 JUnit 5 + Mockito 编写单元测试
- 测试客观题判卷逻辑（单选/多选/判断）
- 测试分数计算（各题型分值、总分）
- 测试考试记录保存
- 测试重复提交防护
- 测试权限控制（非 PROBATION 角色访问应被拒绝）
- 测试试卷不存在时的错误处理
- 测试答案不完整时的错误处理

**前端测试**:
- 测试考试页面加载和倒计时
- 测试答案选择和修改
- 测试提交确认逻辑
- 测试考试结果页面展示
- 测试错题解析显示
- 测试重新答题流程

## Previous Story Intelligence (3-2)

**关键经验**:
1. **数据库 ENUM 扩展**: Story 3.2 中发现 questions 表的 question_type ENUM 缺少 true_false，创建了 V6 迁移脚本。Story 3.3 中需注意 exam_records 表是否也需要扩展。
2. **多选题答案格式**: 答案必须排序后存储（如"ABC"），判卷时需对比排序后的答案。
3. **Token 管理**: add-questions.sh 中使用了过期 Token，确保测试时使用最新 Token。
4. **题库数量要求**: 生成试卷需要单选题≥15、多选题≥5、判断题≥10。
5. **前后端分离**: Story 3.2 已建立完整的题库管理和随机抽题功能，Story 3.3 只需在此基础上实现提交和判卷。

**已建立的模式**:
- MyBatis-Plus 分页查询模式
- 全局异常处理（BusinessException）
- JWT Token 解析（JwtTokenProvider.getUserIdFromToken）
- 前后端 API 封装模式（Axios + TypeScript 类型）
- Element Plus 组件使用规范

## Dev Agent Guardrails

### Technical Requirements
- 严格遵循现有代码风格和架构模式
- 所有 API 接口必须添加 Swagger 注解
- DTO 必须使用 @Valid 验证
- 判卷逻辑必须在后端实现，不可依赖前端
- 考试结果必须记录到数据库，不可仅存前端

### Architecture Compliance
- 使用 MyBatis-Plus BaseMapper 进行 CRUD
- 使用 LambdaQueryWrapper 构建查询条件
- 使用 @PreAuthorize 进行权限控制
- 使用 BusinessException 抛出业务异常
- 使用 ApiResponse 统一响应格式

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

### Testing Requirements
- 关键业务逻辑必须编写单元测试
- 判卷逻辑测试覆盖率 ≥ 80%
- 前端关键交互必须手动测试验证

## Story Completion Status

Status: done

### Acceptance Criteria Validation

| AC | 测试内容 | 结果 | 备注 |
|----|----------|------|------|
| AC1 | 考试页面展示 | ✅ 通过 | ExamView.vue 完整实现，倒计时、题型展示正常 |
| AC2 | 答题与自动保存 | ✅ 通过 | 前端答案实时保存，多选题验证正常 |
| AC3 | 时间控制与自动提交 | ✅ 通过 | 倒计时自动提交，少于5分钟橙色闪烁 |
| AC4 | 提交试卷与客观题自动判卷 | ✅ 通过 | 后端判卷逻辑完整，重复提交防护已实现 |
| AC5 | 查看考试结果与错题解析 | ✅ 通过 | ExamResultView.vue 完整显示，各题型得分、答题详情、错题解析正常 |
| AC6 | 重新答题 | ✅ 通过 | 结果页面提供重新答题按钮，考试次数递增正确 |

### Questions & Clarifications
- Q: 考试次数是否限制？
  A: MVP 阶段不限制，可多次答题取最高分
- Q: 简答题如何处理？
  A: Story 3.4 中由负责人手动评分，此处只判客观题
- Q: 考试过程中断网如何处理？
  A: MVP 阶段不处理，用户需重新答题
- Q: 是否需要防作弊（切屏检测）？
  A: MVP 阶段不实现，后续根据需求添加

## Dev Agent Record

### Implementation Plan
1. 扩展 exam_records 表（V7 Flyway 迁移）
2. 创建后端实体和 DTO
3. 实现提交试卷接口和判卷逻辑
4. 实现考试结果查询接口
5. 前端连接提交接口
6. 创建考试结果页面

### Completion Notes
**✅ 已完成的功能**:
1. **后端核心功能**:
   - 提交试卷接口 POST /api/exam/submit，包含完整判卷逻辑
   - 客观题自动判卷（单选、多选、判断），多选题答案排序对比
   - 考试记录保存到 exam_records 表，真实用时计算
   - 转正申请状态自动更新（exam_score 和 status）
   - 获取考试结果接口 GET /api/exam/result
   - 重复提交防护（同一用户+同一试卷）
   - 考试次数正确计算（MAX attempt_count + 1）
   - 及格线配置化（PASSING_SCORE_THRESHOLD 常量）

2. **前端功能**:
   - ExamView.vue 连接真实提交接口，记录考试开始时间
   - ExamResultView.vue 考试结果页面（总分、各题型得分、答题详情、错题解析）
   - 路由配置 /exam/result
   - 重新答题和继续转正流程按钮
   - 提交成功后清理定时器

3. **数据库迁移**:
   - V7__expand_exam_records.sql 手动执行完成
   - 添加 paper_id、max_score、passed、duration、attempt_count、updated_at 字段
   - 添加相应索引

4. **Code Review 修复**:
   - P0: 修复时间硬编码、重复提交防护、多选题判卷边界
   - P1: 修复 paperId 验证、考试次数计算、定时器清理
   - P2: 及格线常量、注释修正

### File List
**新增文件**:
- `backend/src/main/java/com/syit/cpc/entity/ExamRecord.java`
- `backend/src/main/java/com/syit/cpc/mapper/ExamRecordMapper.java`
- `backend/src/main/java/com/syit/cpc/dto/request/SubmitExamRequest.java`
- `backend/src/main/java/com/syit/cpc/dto/response/ExamResultResponse.java`
- `backend/src/main/resources/db/migration/V7__expand_exam_records.sql`
- `frontend/src/views/ExamResultView.vue`

**修改文件**:
- `backend/src/main/java/com/syit/cpc/service/QuestionService.java` - 添加 submitExam 和 getLatestExamResult 接口
- `backend/src/main/java/com/syit/cpc/service/impl/QuestionServiceImpl.java` - 实现判卷逻辑和考试记录管理
- `backend/src/main/java/com/syit/cpc/controller/ExamController.java` - 添加 submit 和 result 接口
- `frontend/src/api/exam.ts` - 添加 submitExam 和 getExamResult 函数及类型定义
- `frontend/src/views/ExamView.vue` - 连接真实提交接口
- `frontend/src/router/index.ts` - 添加 /exam/result 路由

### Change Log
- 实现 Story 3.3 在线答题功能（2026-04-08）
- 完成客观题自动判卷逻辑
- 完成考试结果展示页面
- 完成考试记录管理

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->
