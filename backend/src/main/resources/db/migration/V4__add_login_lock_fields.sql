-- 添加登录失败次数和锁定时间字段
ALTER TABLE users 
ADD COLUMN login_fail_count INT NOT NULL DEFAULT 0 COMMENT '登录失败次数',
ADD COLUMN lock_until DATETIME NULL COMMENT '锁定截止时间';

-- 添加索引优化查询
CREATE INDEX idx_users_lock_until ON users(lock_until);
