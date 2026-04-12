-- =============================================================================
-- 沈阳工业大学计算机程序设计社团官方网站 - 数据库初始化脚本
-- Flyway Migration: V1__init_schema.sql
-- Description: 创建所有核心表结构
-- MySQL Version: 8.0+ (使用 JSON 类型和 utf8mb4_unicode_ci 排序规则)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. users 表 - 用户表
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    student_id VARCHAR(20) NOT NULL COMMENT '学号',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '加密密码',
    role ENUM('ROLE_PROBATION', 'ROLE_MEMBER', 'ROLE_ADMIN') DEFAULT 'ROLE_PROBATION' COMMENT '角色: ROLE_PROBATION-见习成员, ROLE_MEMBER-正式成员, ROLE_ADMIN-管理员',
    avatar VARCHAR(255) COMMENT '头像URL',
    bio TEXT COMMENT '个人简介',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 唯一约束
    UNIQUE KEY uk_users_student_id (student_id),
    UNIQUE KEY uk_users_email (email),

    -- 索引
    INDEX idx_users_email (email),
    INDEX idx_users_status (status),
    INDEX idx_users_role (role),
    INDEX idx_users_deleted (deleted)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- -----------------------------------------------------------------------------
-- 2. content 表 - 内容表
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS content (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '内容ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    summary VARCHAR(500) COMMENT '摘要',
    content TEXT NOT NULL COMMENT '内容(Markdown格式)',
    type ENUM('news', 'blog') NOT NULL COMMENT '类型: news-资讯, blog-博客',
    status ENUM('draft', 'pending', 'published', 'rejected') DEFAULT 'draft' COMMENT '状态: draft-草稿, pending-审核中, published-已发布, rejected-已拒绝',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    cover_image VARCHAR(255) COMMENT '封面图URL',
    tags JSON COMMENT '标签数组',
    view_count INT DEFAULT 0 COMMENT '阅读数',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 外键约束
    CONSTRAINT fk_content_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,

    -- 索引
    INDEX idx_content_status_created (status, created_at),
    INDEX idx_content_author (author_id),
    INDEX idx_content_type (type),
    INDEX idx_content_created_at (created_at),
    INDEX idx_content_deleted (deleted)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容表';

-- -----------------------------------------------------------------------------
-- 3. review_votes 表 - 审核投票表
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS review_votes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '投票ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    user_id BIGINT NOT NULL COMMENT '投票用户ID',
    vote ENUM('approve', 'reject') NOT NULL COMMENT '投票: approve-通过, reject-拒绝',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    -- 外键约束
    CONSTRAINT fk_review_content FOREIGN KEY (content_id) REFERENCES content(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- 唯一约束: 每人每篇文章只能投一票 (同时作为索引使用)
    UNIQUE KEY uk_review_content_user (content_id, user_id),

    -- 索引
    INDEX idx_review_user (user_id),
    INDEX idx_review_created_at (created_at)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核投票表';

-- -----------------------------------------------------------------------------
-- 4. promotion_applications 表 - 转正申请表
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS promotion_applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    project_name VARCHAR(200) COMMENT '项目名称',
    project_description TEXT COMMENT '项目描述',
    project_url VARCHAR(500) COMMENT '项目链接',
    project_score INT COMMENT '项目评分(0-100)',
    exam_score INT COMMENT '考试分数(0-100)',
    total_score INT COMMENT '总分数(0-100)',
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '状态: pending-待审核, approved-已通过, rejected-已拒绝',
    reviewed_by BIGINT COMMENT '审核人ID',
    review_comment TEXT COMMENT '审核意见',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 外键约束
    CONSTRAINT fk_promotion_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_promotion_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,

    -- 索引
    INDEX idx_promotion_user (user_id),
    INDEX idx_promotion_status (status),
    INDEX idx_promotion_created_at (created_at),
    INDEX idx_promotion_total_score (total_score)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='转正申请表';

-- -----------------------------------------------------------------------------
-- 5. questions 表 - 题库表
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID',
    question_text TEXT NOT NULL COMMENT '题目内容',
    question_type ENUM('single_choice', 'multiple_choice', 'short_answer') NOT NULL COMMENT '题型: single_choice-单选, multiple_choice-多选, short_answer-简答',
    options JSON COMMENT '选项(JSON数组, 单选/多选时使用)',
    correct_answer VARCHAR(255) COMMENT '正确答案',
    score INT DEFAULT 10 COMMENT '分值',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 索引
    INDEX idx_question_type (question_type),
    INDEX idx_question_created_at (created_at)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题库表';

-- -----------------------------------------------------------------------------
-- 6. exam_records 表 - 考试记录表
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS exam_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    answers JSON COMMENT '答题记录(JSON格式: {questionId: answer})',
    score INT COMMENT '得分',
    started_at TIMESTAMP COMMENT '开始时间',
    completed_at TIMESTAMP COMMENT '完成时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    -- 外键约束
    CONSTRAINT fk_exam_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- 索引
    INDEX idx_exam_user (user_id),
    INDEX idx_exam_score (score),
    INDEX idx_exam_completed_at (completed_at),
    INDEX idx_exam_created_at (created_at)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试记录表';

-- =============================================================================
-- 初始化完成
-- =============================================================================
