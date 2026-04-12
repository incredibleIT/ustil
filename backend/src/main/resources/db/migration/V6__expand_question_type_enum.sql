-- 扩展 questions 表的 question_type ENUM 类型
-- 添加 'true_false' 题型支持
ALTER TABLE questions 
MODIFY COLUMN question_type ENUM('single_choice', 'multiple_choice', 'true_false', 'short_answer') NOT NULL COMMENT '题型: single_choice-单选, multiple_choice-多选, true_false-判断, short_answer-简答';
