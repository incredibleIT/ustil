---
story_id: "1.4"
story_key: "1-4-entity-mapper-creation"
epic: "项目初始化与基础架构搭建"
status: done
created: "2026-04-03"
---

# Story 1.4: 基础实体与 Mapper 创建

## Story

As a 开发团队,
I want 创建 MyBatis-Plus 实体类和 Mapper 接口,
So that 可以进行数据访问。

## Acceptance Criteria

### AC1: Entity 实体类创建
**Given** 数据库表已设计
**When** 创建 Entity 类
**Then** 包含 User, Content, ReviewVote, PromotionApplication, Question, ExamRecord 实体
**And** 使用 Lombok 简化代码
**And** 正确映射数据库字段和表名

### AC2: Mapper 接口创建
**Given** 实体类创建完成
**When** 创建 Mapper 接口
**Then** 继承 BaseMapper 获得基础 CRUD 能力
**And** 使用 @Mapper 注解
**And** 创建对应的 XML 映射文件目录结构

### AC3: 逻辑删除支持
**Given** 实体类包含 deleted 字段
**When** 配置 MyBatis-Plus
**Then** 启用逻辑删除功能
**And** 实体类添加 @TableLogic 注解

## Tasks / Subtasks

- [x] Task 1: 创建 User 实体类 (AC: #1)
  - [x] 创建 User.java 实体类
  - [x] 添加 Lombok @Data 注解
  - [x] 添加 MyBatis-Plus @TableName("users") 注解
  - [x] 映射所有字段（id, student_id, username, email, password, role, avatar, bio, status, deleted, created_at, updated_at）
  - [x] 添加 @TableLogic 注解到 deleted 字段
  - [x] 添加 @TableField 注解处理下划线转驼峰

- [x] Task 2: 创建 Content 实体类 (AC: #1)
  - [x] 创建 Content.java 实体类
  - [x] 添加 Lombok @Data 注解
  - [x] 添加 @TableName("content") 注解
  - [x] 映射所有字段（id, title, summary, content, type, status, author_id, cover_image, tags, view_count, deleted, created_at, updated_at）
  - [x] 添加 @TableLogic 注解到 deleted 字段
  - [x] tags 字段使用 Jackson 处理 JSON

- [x] Task 3: 创建 ReviewVote 实体类 (AC: #1)
  - [x] 创建 ReviewVote.java 实体类
  - [x] 添加 Lombok @Data 注解
  - [x] 添加 @TableName("review_votes") 注解
  - [x] 映射所有字段（id, content_id, user_id, vote, created_at, deleted）
  - [x] 添加 @TableLogic 注解到 deleted 字段

- [x] Task 4: 创建 PromotionApplication 实体类 (AC: #1)
  - [x] 创建 PromotionApplication.java 实体类
  - [x] 添加 Lombok @Data 注解
  - [x] 添加 @TableName("promotion_applications") 注解
  - [x] 映射所有字段（id, user_id, project_name, project_description, project_url, project_score, exam_score, total_score, status, reviewed_by, review_comment, created_at, updated_at, deleted）
  - [x] 添加 @TableLogic 注解到 deleted 字段

- [x] Task 5: 创建 Question 实体类 (AC: #1)
  - [x] 创建 Question.java 实体类
  - [x] 添加 Lombok @Data 注解
  - [x] 添加 @TableName("questions") 注解
  - [x] 映射所有字段（id, question_text, question_type, options, correct_answer, score, created_at, updated_at, deleted）
  - [x] options 字段使用 Jackson 处理 JSON
  - [x] 添加 @TableLogic 注解到 deleted 字段

- [x] Task 6: 创建 ExamRecord 实体类 (AC: #1)
  - [x] 创建 ExamRecord.java 实体类
  - [x] 添加 Lombok @Data 注解
  - [x] 添加 @TableName("exam_records") 注解
  - [x] 映射所有字段（id, user_id, answers, score, started_at, completed_at, created_at, deleted）
  - [x] answers 字段使用 Jackson 处理 JSON
  - [x] 添加 @TableLogic 注解到 deleted 字段

- [x] Task 7: 创建 Mapper 接口 (AC: #2)
  - [x] 创建 UserMapper.java 继承 BaseMapper<User>
  - [x] 创建 ContentMapper.java 继承 BaseMapper<Content>
  - [x] 创建 ReviewVoteMapper.java 继承 BaseMapper<ReviewVote>
  - [x] 创建 PromotionApplicationMapper.java 继承 BaseMapper<PromotionApplication>
  - [x] 创建 QuestionMapper.java 继承 BaseMapper<Question>
  - [x] 创建 ExamRecordMapper.java 继承 BaseMapper<ExamRecord>
  - [x] 所有 Mapper 添加 @Mapper 注解

- [x] Task 8: 创建 XML 映射文件目录 (AC: #2)
  - [x] 创建 src/main/resources/mapper/ 目录
  - [x] 创建 UserMapper.xml 基础结构
  - [x] 创建 ContentMapper.xml 基础结构
  - [x] 创建 ReviewVoteMapper.xml 基础结构
  - [x] 创建 PromotionApplicationMapper.xml 基础结构
  - [x] 创建 QuestionMapper.xml 基础结构
  - [x] 创建 ExamRecordMapper.xml 基础结构

- [x] Task 9: 验证实体和 Mapper (AC: #3)
  - [x] 启动应用验证无报错
  - [x] 验证 MyBatis-Plus 自动扫描 Mapper
  - [x] 验证逻辑删除配置生效

## Dev Notes

### 必须遵守的架构规范 [Source: docs/architecture.md]

**实体类规范**:
- 使用 Lombok @Data 简化 getter/setter
- 使用 MyBatis-Plus @TableName 指定表名
- 使用 @TableLogic 标记逻辑删除字段
- 使用 @TableField 处理特殊字段映射
- 包路径: `com.syit.cpc.entity`

**Mapper 规范**:
- 继承 BaseMapper<T> 获得基础 CRUD
- 使用 @Mapper 注解（或 @MapperScan 统一配置）
- XML 文件路径: `src/main/resources/mapper/`
- 包路径: `com.syit.cpc.mapper`

### 数据库表结构参考

**users 表**:
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(20) NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ROLE_PROBATION', 'ROLE_MEMBER', 'ROLE_ADMIN'),
    avatar VARCHAR(255),
    bio TEXT,
    status TINYINT DEFAULT 1,
    deleted TINYINT DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

**content 表**:
```sql
CREATE TABLE content (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500),
    content TEXT NOT NULL,
    type ENUM('news', 'blog'),
    status ENUM('draft', 'pending', 'published', 'rejected'),
    author_id BIGINT NOT NULL,
    cover_image VARCHAR(255),
    tags JSON,
    view_count INT DEFAULT 0,
    deleted TINYINT DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### MyBatis-Plus 配置

**application.yml 已配置**:
```yaml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  mapper-locations: classpath:/mapper/**/*.xml
```

### 实体类示例

```java
@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String studentId;
    private String username;
    private String email;
    private String password;
    private String role;
    private String avatar;
    private String bio;
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

### JSON 字段处理

对于 tags, options, answers 等 JSON 字段，使用 Jackson 处理：
```java
@TableField(typeHandler = JacksonTypeHandler.class)
private List<String> tags;
```

### 依赖检查

确保 pom.xml 包含：
```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.5</version>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

## References

- [Source: docs/architecture.md] - MyBatis-Plus 配置、实体类规范
- [Source: _bmad-output/planning-artifacts/epics.md] - Story 1.4 原始需求
- [Source: backend/src/main/resources/db/migration/V1__init_schema.sql] - 数据库表结构
- MyBatis-Plus 文档: https://baomidou.com/

## Dev Agent Record

### Agent Model Used

Lingma

### Debug Log References

### Completion Notes List

- 2026-04-03: 完成所有实体类创建（User, Content, ReviewVote, PromotionApplication, Question, ExamRecord）
- 2026-04-03: 完成所有 Mapper 接口创建（继承 BaseMapper，添加 @Mapper 注解）
- 2026-04-03: 完成 XML 映射文件目录和基础结构创建
- 2026-04-03: 项目编译验证通过，无报错
- 2026-04-03: 代码审查修复 - 为所有实体类统一添加逻辑删除支持（@TableLogic）
- 2026-04-03: 创建 Flyway 迁移脚本 V2__add_deleted_column.sql

### File List

- backend/src/main/java/com/syit/cpc/entity/User.java
- backend/src/main/java/com/syit/cpc/entity/Content.java
- backend/src/main/java/com/syit/cpc/entity/ReviewVote.java
- backend/src/main/java/com/syit/cpc/entity/PromotionApplication.java
- backend/src/main/java/com/syit/cpc/entity/Question.java
- backend/src/main/java/com/syit/cpc/entity/ExamRecord.java
- backend/src/main/java/com/syit/cpc/mapper/UserMapper.java
- backend/src/main/java/com/syit/cpc/mapper/ContentMapper.java
- backend/src/main/java/com/syit/cpc/mapper/ReviewVoteMapper.java
- backend/src/main/java/com/syit/cpc/mapper/PromotionApplicationMapper.java
- backend/src/main/java/com/syit/cpc/mapper/QuestionMapper.java
- backend/src/main/java/com/syit/cpc/mapper/ExamRecordMapper.java
- backend/src/main/resources/mapper/UserMapper.xml
- backend/src/main/resources/mapper/ContentMapper.xml
- backend/src/main/resources/mapper/ReviewVoteMapper.xml
- backend/src/main/resources/mapper/PromotionApplicationMapper.xml
- backend/src/main/resources/mapper/QuestionMapper.xml
- backend/src/main/resources/mapper/ExamRecordMapper.xml
- backend/src/main/resources/db/migration/V2__add_deleted_column.sql
