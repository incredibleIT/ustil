-- 为转正申请表添加 reviewed_at 字段（评审完成时间）
ALTER TABLE promotion_applications 
ADD COLUMN reviewed_at TIMESTAMP NULL COMMENT '评审完成时间' 
AFTER review_comment;
