-- 转正申请表 status 字段类型修改
-- 将旧的 ENUM 类型改为 VARCHAR(20) 以支持更多状态
ALTER TABLE promotion_applications 
MODIFY COLUMN `status` VARCHAR(20) DEFAULT 'pending_exam' COMMENT '状态: pending_exam-待答题, exam_in_progress-答题中, pending_project-待提交项目, project_reviewing-评审中, approved-已通过, rejected-已拒绝';
