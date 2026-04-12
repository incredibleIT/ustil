-- =============================================================================
-- Flyway Migration: V2__add_deleted_column.sql
-- Description: 为缺少逻辑删除字段的表添加 deleted 列
-- =============================================================================

-- review_votes 表添加逻辑删除字段
ALTER TABLE review_votes
    ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除' AFTER created_at,
    ADD INDEX idx_review_deleted (deleted);

-- promotion_applications 表添加逻辑删除字段
ALTER TABLE promotion_applications
    ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除' AFTER updated_at,
    ADD INDEX idx_promotion_deleted (deleted);

-- questions 表添加逻辑删除字段
ALTER TABLE questions
    ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除' AFTER updated_at,
    ADD INDEX idx_question_deleted (deleted);

-- exam_records 表添加逻辑删除字段
ALTER TABLE exam_records
    ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除' AFTER created_at,
    ADD INDEX idx_exam_deleted (deleted);

