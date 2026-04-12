-- =============================================================================
-- 沈阳工业大学计算机程序设计社团官方网站 - 种子数据脚本
-- Flyway Migration: V2__seed_data.sql
-- Description: 插入初始测试数据
-- 注意: 本脚本密码哈希为占位符，首次使用前需要通过应用重新设置密码
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. 插入测试用户数据
-- 密码说明: 以下密码哈希为占位格式，实际密码为 '123456'
-- 首次登录前请通过应用的密码重置功能或注册流程生成有效密码
-- -----------------------------------------------------------------------------
INSERT INTO users (student_id, username, email, password, role, avatar, bio, status, deleted) VALUES
('2024001', 'admin', 'admin@syit.edu.cn', '$2a$10$7JB8bM.jR7M6P0X8qRzQO.5UQ5q5Q5q5Q5q5Q5q5Q5q5Q5q5Q5q5Q5O', 'ROLE_ADMIN', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', '社团管理员，负责社团日常管理和技术支持。', 1, 0),
('2024002', 'zhangsan', 'zhangsan@syit.edu.cn', '$2a$10$7JB8bM.jR7M6P0X8qRzQO.5UQ5q5Q5q5Q5q5Q5q5Q5q5Q5q5Q5q5Q5O', 'ROLE_MEMBER', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhangsan', '正式成员，热爱编程，擅长Java后端开发。', 1, 0),
('2024003', 'lisi', 'lisi@syit.edu.cn', '$2a$10$7JB8bM.jR7M6P0X8qRzQO.5UQ5q5Q5q5Q5q5Q5q5Q5q5Q5q5Q5q5Q5O', 'ROLE_MEMBER', 'https://api.dicebear.com/7.x/avataaars/svg?seed=lisi', '正式成员，前端开发爱好者，喜欢Vue和React。', 1, 0),
('2024004', 'wangwu', 'wangwu@syit.edu.cn', '$2a$10$7JB8bM.jR7M6P0X8qRzQO.5UQ5q5Q5q5Q5q5Q5q5Q5q5Q5q5Q5q5Q5O', 'ROLE_PROBATION', 'https://api.dicebear.com/7.x/avataaars/svg?seed=wangwu', '见习成员，正在努力学习编程，希望早日转正。', 1, 0),
('2024005', 'zhaoliu', 'zhaoliu@syit.edu.cn', '$2a$10$7JB8bM.jR7M6P0X8qRzQO.5UQ5q5Q5q5Q5q5Q5q5Q5q5Q5q5Q5q5Q5O', 'ROLE_PROBATION', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhaoliu', '见习成员，对算法竞赛很感兴趣。', 1, 0);

-- -----------------------------------------------------------------------------
-- 2. 插入测试内容数据 (资讯和博客)
-- -----------------------------------------------------------------------------
INSERT INTO content (title, summary, content, type, status, author_id, cover_image, tags, view_count, deleted) VALUES
-- 已发布的资讯
('欢迎加入计算机程序设计社团', '社团简介及新成员欢迎词', '# 欢迎来到计算机程序设计社团！

我们是一个热爱编程、追求技术的学生社团。在这里，你可以：

- 学习各种编程语言和技术
- 参加算法竞赛和项目开发
- 结识志同道合的朋友
- 获得实习和就业推荐

## 社团活动

1. **每周技术分享会** - 成员轮流分享技术心得
2. **算法训练** - 定期组织算法刷题和竞赛
3. **项目实战** - 参与真实项目开发
4. **企业参访** - 参观知名科技公司

期待你的加入！', 'news', 'published', 1, 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800', '["社团", "欢迎", "介绍"]', 128, 0),

('2024年社团招新开始啦', '新学年社团招新活动正式启动', '# 2024年社团招新

## 招新对象

- 全校本科生和研究生
- 对编程有兴趣的同学
- 想要提升技术能力的同学

## 报名方式

1. 在线填写报名表
2. 参加笔试和面试
3. 等待录取通知

## 招新时间

- 报名截止：9月30日
- 笔试时间：10月5日
- 面试时间：10月10-12日

快来加入我们吧！', 'news', 'published', 1, 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=800', '["招新", "2024", "活动"]', 256, 0),

-- 审核中的内容
('我的编程学习之路', '分享我从零开始学习编程的经历', '# 我的编程学习之路

## 初识编程

大一开始接触编程，从C语言入门...

## 学习心得

1. 坚持练习
2. 多做项目
3. 参与开源

## 未来规划

希望能成为一名优秀的后端工程师。', 'blog', 'pending', 2, NULL, '["学习", "心得", "成长"]', 0, 0),

-- 草稿状态的内容
('Spring Boot 入门教程', '从零开始学习Spring Boot框架', '# Spring Boot 入门教程

正在编写中...', 'blog', 'draft', 2, NULL, '["Spring Boot", "Java", "教程"]', 0, 0);

-- -----------------------------------------------------------------------------
-- 3. 插入测试审核投票数据
-- 使用子查询确保外键关联正确
-- -----------------------------------------------------------------------------
INSERT INTO review_votes (content_id, user_id, vote) VALUES
((SELECT id FROM content WHERE title = '我的编程学习之路'), (SELECT id FROM users WHERE username = 'zhangsan'), 'approve'),
((SELECT id FROM content WHERE title = '我的编程学习之路'), (SELECT id FROM users WHERE username = 'lisi'), 'approve');

-- -----------------------------------------------------------------------------
-- 4. 插入测试题库数据
-- -----------------------------------------------------------------------------
INSERT INTO questions (question_text, question_type, options, correct_answer, score) VALUES
-- 单选题
('Java中，以下哪个关键字用于定义类？', 'single_choice', '["A. class", "B. struct", "C. interface", "D. enum"]', 'A', 10),
('在Python中，以下哪个函数用于获取列表的长度？', 'single_choice', '["A. size()", "B. length()", "C. len()", "D. count()"]', 'C', 10),
('HTML中，哪个标签用于定义最大的标题？', 'single_choice', '["A. <h6>", "B. <head>", "C. <h1>", "D. <header>"]', 'C', 10),
('CSS中，以下哪个属性用于设置背景颜色？', 'single_choice', '["A. color", "B. bgcolor", "C. background-color", "D. background-image"]', 'C', 10),
('JavaScript中，以下哪个方法用于向数组末尾添加元素？', 'single_choice', '["A. append()", "B. push()", "C. add()", "D. insert()"]', 'B', 10),

-- 多选题
('以下哪些是Java的基本数据类型？', 'multiple_choice', '["A. int", "B. String", "C. boolean", "D. double", "E. ArrayList"]', 'A,C,D', 15),
('以下哪些是HTTP请求方法？', 'multiple_choice', '["A. GET", "B. POST", "C. DELETE", "D. UPDATE", "E. PUT"]', 'A,B,C,E', 15),

-- 简答题
('简述什么是数据库索引，以及它的优缺点。', 'short_answer', NULL, '数据库索引是一种数据结构，用于快速查询数据库表中的数据。优点：加快数据检索速度，提高查询效率。缺点：占用额外存储空间，降低数据插入、更新、删除的速度。', 20),
('什么是RESTful API？请简述其主要特点。', 'short_answer', NULL, 'RESTful API是一种基于REST架构风格的网络应用程序接口。主要特点：使用HTTP方法(GET/POST/PUT/DELETE)操作资源，无状态通信，统一的接口，资源通过URI标识。', 20);

-- -----------------------------------------------------------------------------
-- 5. 插入测试转正申请数据
-- -----------------------------------------------------------------------------
INSERT INTO promotion_applications (user_id, project_name, project_description, project_url, project_score, exam_score, total_score, status, reviewed_by, review_comment) VALUES
((SELECT id FROM users WHERE username = 'wangwu'), '个人博客系统', '使用Spring Boot + Vue开发的个人博客系统，实现了文章发布、评论、标签分类等功能。', 'https://github.com/wangwu/blog-system', 85, 78, 82, 'pending', NULL, NULL),
((SELECT id FROM users WHERE username = 'zhaoliu'), '图书管理系统', '基于Java Swing的图书管理系统，实现了图书借阅、归还、查询等功能。', 'https://github.com/zhaoliu/library-system', 75, 82, 78, 'pending', NULL, NULL);

-- -----------------------------------------------------------------------------
-- 6. 插入测试考试记录数据
-- 使用固定时间戳确保数据一致性
-- -----------------------------------------------------------------------------
INSERT INTO exam_records (user_id, answers, score, started_at, completed_at) VALUES
((SELECT id FROM users WHERE username = 'wangwu'), '{"1": "A", "2": "C", "3": "C", "4": "C", "5": "B", "6": "A,C,D", "7": "A,B,C,E", "8": "数据库索引是一种数据结构...", "9": "RESTful API是一种基于REST架构..."}', 78, '2026-04-03 08:00:00', '2026-04-03 09:00:00'),
((SELECT id FROM users WHERE username = 'zhaoliu'), '{"1": "A", "2": "C", "3": "B", "4": "C", "5": "B", "6": "A,C", "7": "A,B,E", "8": "索引可以提高查询速度", "9": "RESTful是一种API设计风格"}', 82, '2026-04-03 07:00:00', '2026-04-03 08:00:00');

-- =============================================================================
-- 种子数据插入完成
-- =============================================================================
