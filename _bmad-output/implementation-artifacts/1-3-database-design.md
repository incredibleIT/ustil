---
story_id: "1.3"
story_key: "1-3-database-design"
epic: "项目初始化与基础架构搭建"
status: done
created: "2026-04-03"
---

# Story 1.3: 数据库设计与迁移脚本

## Story

As a 开发团队,
I want 设计数据库 Schema 并创建 Flyway 迁移脚本,
So that 数据库结构版本化管理。

## Acceptance Criteria

### AC1: 数据库 Schema 设计
**Given** 根据 PRD 设计数据库
**When** 创建 V1__init_schema.sql
**Then** 包含 users, content, review_votes, promotion_applications, questions, exam_records 表
**And** 设置主键、外键、索引和约束

### AC2: 种子数据
**Given** 数据库表创建完成
**When** 创建 V2__seed_data.sql
**Then** 插入初始角色数据和测试数据
**And** 配置 Flyway 在应用启动时自动执行

## Tasks / Subtasks

- [x] Task 1: 设计 users 表 (AC: #1)
  - [x] 创建用户表结构：id, student_id, username, email, password, role, avatar, bio, status
  - [x] 添加主键约束 (id)
  - [x] 添加唯一约束 (student_id, email)
  - [x] 添加索引 (email, status)
  - [x] 添加 created_at, updated_at 时间戳

- [x] Task 2: 设计 content 表 (AC: #1)
  - [x] 创建内容表结构：id, title, summary, content, type, status, author_id, cover_image, tags, view_count
  - [x] 添加主键约束 (id)
  - [x] 添加外键约束 (author_id -> users.id)
  - [x] 添加索引 (status, created_at, author_id)
  - [x] 添加 created_at, updated_at 时间戳

- [x] Task 3: 设计 review_votes 表 (AC: #1)
  - [x] 创建审核投票表结构：id, content_id, user_id, vote, created_at
  - [x] 添加主键约束 (id)
  - [x] 添加外键约束 (content_id -> content.id, user_id -> users.id)
  - [x] 添加唯一约束 (content_id, user_id) - 每人每篇文章只能投一票
  - [x] 添加索引 (content_id, user_id, content_id, status)

- [x] Task 4: 设计 promotion_applications 表 (AC: #1)
  - [x] 创建转正申请表结构：id, user_id, project_name, project_description, project_url, project_score, exam_score, total_score, status, reviewed_by, review_comment
  - [x] 添加主键约束 (id)
  - [x] 添加外键约束 (user_id -> users.id, reviewed_by -> users.id)
  - [x] 添加索引 (user_id, status)
  - [x] 添加 created_at, updated_at 时间戳

- [x] Task 5: 设计 questions 表 (AC: #1)
  - [x] 创建题库表结构：id, question_text, question_type, options, correct_answer, score
  - [x] 添加主键约束 (id)
  - [x] 添加索引 (question_type)
  - [x] 添加 created_at, updated_at 时间戳

- [x] Task 6: 设计 exam_records 表 (AC: #1)
  - [x] 创建考试记录表结构：id, user_id, answers, score, started_at, completed_at
  - [x] 添加主键约束 (id)
  - [x] 添加外键约束 (user_id -> users.id)
  - [x] 添加索引 (user_id)
  - [x] 添加 created_at 时间戳

- [x] Task 7: 创建 V1__init_schema.sql 迁移脚本 (AC: #1)
  - [x] 按依赖顺序创建所有表
  - [x] 添加所有约束和索引
  - [x] 验证 SQL 语法正确

- [x] Task 8: 创建 V2__seed_data.sql 种子数据脚本 (AC: #2)
  - [x] 插入测试用户数据（包含三种角色）
  - [x] 插入测试内容数据
  - [x] 插入测试题库数据
  - [x] 验证数据插入正确

- [x] Task 9: 验证 Flyway 配置 (AC: #2)
  - [x] 确认 application.yml 中 Flyway 配置正确
  - [x] 启动应用验证迁移脚本自动执行
  - [x] 检查数据库表是否正确创建

## Dev Notes

### 数据库设计规范

**必须遵守的命名规范** [Source: docs/architecture.md]:
- 表名: 小写蛇形命名，复数形式 (如 `users`, `content_posts`, `review_votes`)
- 列名: 小写蛇形命名
- 主键: `id`
- 外键: `{table}_id` (如 `user_id`, `content_id`)
- 时间戳: `created_at`, `updated_at`
- 布尔值: `is_{形容词}` (如 `is_active`)
- 索引名: `idx_{table}_{column}`

### 表结构设计

**users 表**:
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '加密密码',
    role ENUM('ROLE_PROBATION', 'ROLE_MEMBER', 'ROLE_ADMIN') DEFAULT 'ROLE_PROBATION' COMMENT '角色',
    avatar VARCHAR(255) COMMENT '头像URL',
    bio TEXT COMMENT '个人简介',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

**content 表**:
```sql
CREATE TABLE content (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    summary VARCHAR(500) COMMENT '摘要',
    content TEXT NOT NULL COMMENT '内容',
    type ENUM('news', 'blog') NOT NULL COMMENT '类型: news-资讯, blog-博客',
    status ENUM('draft', 'pending', 'published', 'rejected') DEFAULT 'draft' COMMENT '状态',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    cover_image VARCHAR(255) COMMENT '封面图URL',
    tags JSON COMMENT '标签数组',
    view_count INT DEFAULT 0 COMMENT '阅读数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_content_status_created (status, created_at),
    INDEX idx_content_author (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容表';
```

**review_votes 表**:
```sql
CREATE TABLE review_votes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_id BIGINT NOT NULL COMMENT '内容ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    vote ENUM('approve', 'reject') NOT NULL COMMENT '投票: approve-通过, reject-拒绝',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (content_id) REFERENCES content(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_review_content_user (content_id, user_id),
    INDEX idx_review_content_status (content_id, vote)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核投票表';
```

**promotion_applications 表**:
```sql
CREATE TABLE promotion_applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    project_name VARCHAR(200) COMMENT '项目名称',
    project_description TEXT COMMENT '项目描述',
    project_url VARCHAR(500) COMMENT '项目链接',
    project_score INT COMMENT '项目评分',
    exam_score INT COMMENT '考试分数',
    total_score INT COMMENT '总分数',
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '状态',
    reviewed_by BIGINT COMMENT '审核人ID',
    review_comment TEXT COMMENT '审核意见',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_promotion_user (user_id),
    INDEX idx_promotion_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='转正申请表';
```

**questions 表**:
```sql
CREATE TABLE questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_text TEXT NOT NULL COMMENT '题目内容',
    question_type ENUM('single_choice', 'multiple_choice', 'short_answer') NOT NULL COMMENT '题型',
    options JSON COMMENT '选项(JSON数组)',
    correct_answer VARCHAR(255) COMMENT '正确答案',
    score INT DEFAULT 10 COMMENT '分值',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_question_type (question_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题库表';
```

**exam_records 表**:
```sql
CREATE TABLE exam_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    answers JSON COMMENT '答题记录',
    score INT COMMENT '得分',
    started_at TIMESTAMP COMMENT '开始时间',
    completed_at TIMESTAMP COMMENT '完成时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_exam_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试记录表';
```

### Flyway 迁移脚本位置

**必须遵循的路径** [Source: docs/architecture.md]:
```
backend/src/main/resources/db/migration/
├── V1__init_schema.sql
└── V2__seed_data.sql
```

### 种子数据示例

**V2__seed_data.sql**:
```sql
-- 插入测试用户
INSERT INTO users (student_id, username, email, password, role, status) VALUES
('2024001', 'admin', 'admin@syit.edu.cn', '$2a$10$...', 'ROLE_ADMIN', 1),
('2024002', 'member', 'member@syit.edu.cn', '$2a$10$...', 'ROLE_MEMBER', 1),
('2024003', 'probation', 'probation@syit.edu.cn', '$2a$10$...', 'ROLE_PROBATION', 1);

-- 插入测试内容
INSERT INTO content (title, summary, content, type, status, author_id) VALUES
('欢迎加入社团', '社团介绍', '欢迎内容...', 'news', 'published', 1);

-- 插入测试题目
INSERT INTO questions (question_text, question_type, options, correct_answer, score) VALUES
('社团的宗旨是什么？', 'single_choice', '["A. 学习", "B. 交流", "C. 竞赛", "D. 以上都是"]', 'D', 10);
```

### 技术栈要求

**必须使用的技术** [Source: docs/architecture.md]:
- MySQL 8.0
- Flyway 数据库迁移
- InnoDB 存储引擎
- UTF8MB4 字符集

### 测试要求

- 迁移脚本必须能在全新数据库上成功执行
- 种子数据插入后，应用能正常启动
- 所有外键约束能正确工作
- 索引能正确创建

## References

- [Source: docs/architecture.md] - 数据库命名规范、索引策略
- [Source: _bmad-output/planning-artifacts/epics.md] - Story 1.3 原始需求
- Flyway 文档: https://documentation.red-gate.com/flyway
- MySQL 8.0 文档: https://dev.mysql.com/doc/

## Dev Agent Record

### Agent Model Used

Lingma

### Debug Log References

### Completion Notes List

- ✅ 完成所有6张表的Schema设计 (users, content, review_votes, promotion_applications, questions, exam_records)
- ✅ 创建 V1__init_schema.sql 迁移脚本，包含完整的表结构、主键、外键、索引和约束
- ✅ 创建 V2__seed_data.sql 种子数据脚本，包含5个测试用户、4条内容、8道题目、2条转正申请和考试记录
- ✅ 所有表使用 InnoDB 引擎和 UTF8MB4 字符集
- ✅ 遵循数据库命名规范：小写蛇形命名、复数表名、idx_{table}_{column} 索引名
- ✅ Flyway 配置已验证正确 (application.yml 中 enabled=true, locations=classpath:db/migration)
- ✅ **代码审查修复完成**:
  - H1: 添加密码哈希说明注释
  - H2/H3: users 和 content 表添加 deleted 逻辑删除字段
  - M1: 移除 review_votes 冗余索引
  - M3: 种子数据使用子查询确保外键正确
  - L1: 添加 MySQL 版本要求注释
  - L2: 使用固定时间戳替代 NOW()

### File List

- backend/src/main/resources/db/migration/V1__init_schema.sql (新创建 - 数据库Schema初始化脚本)
- backend/src/main/resources/db/migration/V2__seed_data.sql (新创建 - 种子数据脚本)
