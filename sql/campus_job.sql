-- ============================================================
-- 校园兼职网站 - 数据库初始化脚本
-- ============================================================

DROP DATABASE IF EXISTS campus_job;

CREATE DATABASE IF NOT EXISTS campus_job CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE campus_job;

-- ------------------------------------------------------------
-- 1. 系统用户表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    username        VARCHAR(50)     NOT NULL                 COMMENT '用户名',
    password_hash   VARCHAR(100)    NOT NULL                 COMMENT '密码哈希（BCrypt）',
    phone           VARCHAR(20)     DEFAULT NULL             COMMENT '手机号',
    email           VARCHAR(100)    DEFAULT NULL             COMMENT '邮箱',
    role_type       VARCHAR(20)     NOT NULL DEFAULT 'STUDENT' COMMENT '角色类型: STUDENT/EMPLOYER/ADMIN',
    status          INT             NOT NULL DEFAULT 1       COMMENT '状态: 0-禁用, 1-启用',
    deleted         INT             NOT NULL DEFAULT 0       COMMENT '逻辑删除: 0-未删除, 1-已删除',
    create_time     DATETIME        DEFAULT NULL             COMMENT '创建时间',
    update_time     DATETIME        DEFAULT NULL             COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_phone (phone),
    KEY idx_role_type (role_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ------------------------------------------------------------
-- 2. 用户详情表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS user_profile;
CREATE TABLE user_profile (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    user_id         BIGINT          NOT NULL                 COMMENT '用户ID',
    real_name       VARCHAR(50)     DEFAULT NULL             COMMENT '真实姓名',
    id_card_hash    VARCHAR(128)    DEFAULT NULL             COMMENT '身份证哈希',
    id_card_image   VARCHAR(500)    DEFAULT NULL             COMMENT '身份证图片URL',
    gender          INT             DEFAULT 0                COMMENT '性别: 0-未知, 1-男, 2-女',
    university      VARCHAR(100)    DEFAULT NULL             COMMENT '学校',
    major           VARCHAR(100)    DEFAULT NULL             COMMENT '专业',
    grade           VARCHAR(50)     DEFAULT NULL             COMMENT '年级',
    balance         DECIMAL(10,2)   DEFAULT 0.00             COMMENT '余额',
    credit_score    INT             DEFAULT 100              COMMENT '信用分',
    avatar_url      VARCHAR(500)    DEFAULT NULL             COMMENT '头像URL',
    verified_status INT             DEFAULT 0                COMMENT '实名认证状态: 0-未认证, 1-审核中, 2-已认证, 3-认证失败',
    create_time     DATETIME        DEFAULT NULL             COMMENT '创建时间',
    update_time     DATETIME        DEFAULT NULL             COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id),
    KEY idx_university (university),
    KEY idx_verified_status (verified_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户详情表';

-- ------------------------------------------------------------
-- 3. 岗位分类表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS job_category;
CREATE TABLE job_category (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    name            VARCHAR(50)     NOT NULL                 COMMENT '分类名称',
    parent_id       BIGINT          DEFAULT 0                COMMENT '父分类ID',
    sort_order      INT             DEFAULT 0                COMMENT '排序号',
    status          INT             NOT NULL DEFAULT 1       COMMENT '状态: 0-禁用, 1-启用',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位分类表';

-- ------------------------------------------------------------
-- 4. 岗位发布表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS job_post;
CREATE TABLE job_post (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '岗位ID',
    publisher_id    BIGINT          NOT NULL                 COMMENT '发布者ID',
    category_id     BIGINT          NOT NULL                 COMMENT '分类ID',
    title           VARCHAR(100)    NOT NULL                 COMMENT '岗位标题',
    description     TEXT            DEFAULT NULL             COMMENT '岗位描述',
    location        VARCHAR(200)    DEFAULT NULL             COMMENT '工作地点',
    salary_type     VARCHAR(20)     DEFAULT NULL             COMMENT '薪资类型',
    salary_amount   DECIMAL(10,2)   DEFAULT NULL             COMMENT '薪资金额',
    start_time      DATETIME        DEFAULT NULL             COMMENT '开始时间',
    end_time        DATETIME        DEFAULT NULL             COMMENT '结束时间',
    recruit_num     INT             DEFAULT 1                COMMENT '招聘人数',
    hired_num       INT             DEFAULT 0                COMMENT '已录用人数',
    status          INT             DEFAULT 0                COMMENT '状态: 0-草稿, 1-待审核, 2-已发布, 3-已拒绝, 4-已下架',
    audit_status    INT             DEFAULT 0                COMMENT '审核状态: 0-待审核, 1-已通过, 2-已拒绝',
    audit_remark    VARCHAR(500)    DEFAULT NULL             COMMENT '审核备注',
    version         INT             NOT NULL DEFAULT 0       COMMENT '乐观锁版本号',
    deleted         INT             NOT NULL DEFAULT 0       COMMENT '逻辑删除: 0-未删除, 1-已删除',
    create_time     DATETIME        DEFAULT NULL             COMMENT '创建时间',
    update_time     DATETIME        DEFAULT NULL             COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_publisher_id (publisher_id),
    KEY idx_category_id (category_id),
    KEY idx_status (status),
    KEY idx_audit_status (audit_status),
    KEY idx_salary_type (salary_type),
    KEY idx_location (location),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位发布表';

-- ------------------------------------------------------------
-- 5. 报名申请表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS application;
CREATE TABLE application (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '申请ID',
    job_id          BIGINT          NOT NULL                 COMMENT '岗位ID',
    applicant_id    BIGINT          NOT NULL                 COMMENT '申请人ID',
    resume_url      VARCHAR(500)    DEFAULT NULL             COMMENT '简历URL',
    status          INT             DEFAULT 0                COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝, 3-已入职, 4-已取消',
    apply_time      DATETIME        DEFAULT NULL             COMMENT '申请时间',
    review_time     DATETIME        DEFAULT NULL             COMMENT '审核时间',
    reject_reason   VARCHAR(500)    DEFAULT NULL             COMMENT '拒绝原因',
    PRIMARY KEY (id),
    KEY idx_job_id (job_id),
    KEY idx_applicant_id (applicant_id),
    KEY idx_status (status),
    UNIQUE KEY uk_job_applicant (job_id, applicant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报名申请表';

-- ------------------------------------------------------------
-- 6. 订单记录表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS order_record;
CREATE TABLE order_record (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '订单ID',
    application_id      BIGINT          NOT NULL                 COMMENT '申请ID',
    student_id          BIGINT          NOT NULL                 COMMENT '学生ID',
    employer_id         BIGINT          NOT NULL                 COMMENT '雇主ID',
    job_id              BIGINT          NOT NULL                 COMMENT '岗位ID',
    amount              DECIMAL(10,2)   NOT NULL                 COMMENT '订单金额',
    pay_status          INT             DEFAULT 0                COMMENT '支付状态: 0-待支付, 1-已支付, 2-已退款',
    settlement_status   INT             DEFAULT 0                COMMENT '结算状态: 0-待结算, 1-已结算',
    start_date          DATETIME        DEFAULT NULL             COMMENT '工作开始日期',
    end_date            DATETIME        DEFAULT NULL             COMMENT '工作结束日期',
    create_time         DATETIME        DEFAULT NULL             COMMENT '创建时间',
    update_time         DATETIME        DEFAULT NULL             COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_application_id (application_id),
    KEY idx_student_id (student_id),
    KEY idx_employer_id (employer_id),
    KEY idx_job_id (job_id),
    KEY idx_pay_status (pay_status),
    KEY idx_settlement_status (settlement_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单记录表';

-- ------------------------------------------------------------
-- 7. 评价表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS review;
CREATE TABLE review (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '评价ID',
    order_id        BIGINT          NOT NULL                 COMMENT '订单ID',
    reviewer_id     BIGINT          NOT NULL                 COMMENT '评价人ID',
    target_id       BIGINT          NOT NULL                 COMMENT '被评价人ID',
    rating          INT             NOT NULL                 COMMENT '评分: 1-5',
    comment         TEXT            DEFAULT NULL             COMMENT '评价内容',
    type            VARCHAR(20)     DEFAULT NULL             COMMENT '评价类型',
    create_time     DATETIME        DEFAULT NULL             COMMENT '创建时间',
    update_time     DATETIME        DEFAULT NULL             COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_reviewer_id (reviewer_id),
    KEY idx_target_id (target_id),
    KEY idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- ------------------------------------------------------------
-- 8. 系统字典表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    dict_type       VARCHAR(50)     NOT NULL                 COMMENT '字典类型',
    dict_code       VARCHAR(50)     NOT NULL                 COMMENT '字典编码',
    dict_label      VARCHAR(100)    DEFAULT NULL             COMMENT '字典标签',
    sort_order      INT             DEFAULT 0                COMMENT '排序号',
    status          INT             DEFAULT 1                COMMENT '状态: 0-禁用, 1-启用',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_type_code (dict_type, dict_code),
    KEY idx_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典表';

-- ============================================================
-- 初始样例数据
-- ============================================================

-- ------------------------------------------------------------
-- 1. 系统用户 (共15人: 1管理员 + 2管理员 + 5学生 + 7雇主)
--    所有密码均为 123456
-- ------------------------------------------------------------

-- 管理员
INSERT INTO sys_user (username, password_hash, phone, email, role_type, status, deleted, create_time, update_time) VALUES
('admin',      '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13700000001', 'admin@campus.com',     'ADMIN',    1, 0, '2025-09-01 10:00:00', '2025-09-01 10:00:00'),
('manager_wu', '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13700000002', 'wuguanli@campus.com', 'ADMIN',    1, 0, '2025-09-01 10:00:00', '2025-09-01 10:00:00');

-- 学生
INSERT INTO sys_user (username, password_hash, phone, email, role_type, status, deleted, create_time, update_time) VALUES
('zhangsan',   '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13800001001', 'zhangsan@stu.edu.cn',  'STUDENT',  1, 0, '2025-09-05 08:30:00', '2025-09-05 08:30:00'),
('lisi',       '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13800001002', 'lisi@stu.edu.cn',      'STUDENT',  1, 0, '2025-09-05 09:00:00', '2025-09-05 09:00:00'),
('wangwu',     '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13800001003', 'wangwu@stu.edu.cn',    'STUDENT',  1, 0, '2025-09-05 09:15:00', '2025-09-05 09:15:00'),
('zhaoliu',    '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13800001004', 'zhaoliu@stu.edu.cn',   'STUDENT',  1, 0, '2025-09-06 14:00:00', '2025-09-06 14:00:00'),
('sunqi',      '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13800001005', 'sunqi@stu.edu.cn',     'STUDENT',  1, 0, '2025-09-10 11:00:00', '2025-09-10 11:00:00'),
('zhouba',     '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13800001006', 'zhouba@stu.edu.cn',    'STUDENT',  1, 0, '2025-09-12 16:00:00', '2025-09-12 16:00:00');

-- 雇主
INSERT INTO sys_user (username, password_hash, phone, email, role_type, status, deleted, create_time, update_time) VALUES
('edu_star',       '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13900002001', 'contact@edustar.com',       'EMPLOYER', 1, 0, '2025-08-20 09:00:00', '2025-08-20 09:00:00'),
('milk_tea_house', '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13900002002', 'hr@milktea.com',            'EMPLOYER', 1, 0, '2025-08-22 10:00:00', '2025-08-22 10:00:00'),
('tech_future',    '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13900002003', 'hr@techfuture.cn',          'EMPLOYER', 1, 0, '2025-08-25 14:00:00', '2025-08-25 14:00:00'),
('event_king',     '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13900002004', 'job@eventking.cn',          'EMPLOYER', 1, 0, '2025-09-01 08:30:00', '2025-09-01 08:30:00'),
('design_lab',     '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13900002005', 'hr@designlab.art',          'EMPLOYER', 1, 0, '2025-09-03 11:00:00', '2025-09-03 11:00:00'),
('super_mall',     '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13900002006', 'parttime@supermall.com',   'EMPLOYER', 1, 0, '2025-09-05 15:00:00', '2025-09-05 15:00:00'),
('write_craft',    '$2a$10$IN6uwNqmPIa.EUFrSnvoI.v/QJeZhXQfrTswcId01u7LVjGzWes4O', '13900002007', 'cooperate@writecraft.cn',  'EMPLOYER', 1, 0, '2025-09-08 09:00:00', '2025-09-08 09:00:00');

-- ------------------------------------------------------------
-- 2. 用户详情 (与上面的15个用户一一对应)
-- ------------------------------------------------------------

-- 管理员详情
INSERT INTO user_profile (user_id, real_name, gender, university, major, grade, balance, credit_score, verified_status, create_time, update_time) VALUES
(1, '系统管理员', 1, '某某大学', '计算机科学', '已毕业', 0.00, 100, 2, '2025-09-01 10:00:00', '2025-09-01 10:00:00'),
(2, '吴管理',   1, '某某大学', '工商管理', '已毕业', 0.00, 100, 2, '2025-09-01 10:00:00', '2025-09-01 10:00:00');

-- 学生详情
INSERT INTO user_profile (user_id, real_name, gender, university, major, grade, balance, credit_score, verified_status, create_time, update_time) VALUES
(3, '张三',   1, '某某大学', '数学与应用数学',   '大三', 1580.00, 98, 2, '2025-09-05 08:30:00', '2026-03-20 10:00:00'),
(4, '李四',   2, '某某大学', '英语',           '大二', 620.00,  95, 2, '2025-09-05 09:00:00', '2026-03-15 14:30:00'),
(5, '王五',   1, '某某理工大学', '计算机科学与技术', '大四', 2350.00, 100, 2, '2025-09-05 09:15:00', '2026-03-25 09:00:00'),
(6, '赵六',   1, '某某师范大学', '物理学',         '大三', 480.00,  92, 2, '2025-09-06 14:00:00', '2026-03-10 11:00:00'),
(7, '孙七',   2, '某某艺术学院', '视觉传达设计',   '大二', 1200.00, 88, 1, '2025-09-10 11:00:00', '2026-03-18 16:00:00'),
(8, '周八',   2, '某某财经大学', '会计学',         '大一', 200.00,  85, 0, '2025-09-12 16:00:00', '2025-09-12 16:00:00');

-- 雇主详情
INSERT INTO user_profile (user_id, real_name, gender, university, major, grade, balance, credit_score, verified_status, create_time, update_time) VALUES
(9,  '启明星教育',   1, '启明星教育科技有限公司', '教育培训', '', 50000.00, 100, 2, '2025-08-20 09:00:00', '2026-03-01 10:00:00'),
(10, '茶颜悦色',    1, '茶颜悦色餐饮管理有限公司', '餐饮连锁', '', 30000.00, 100, 2, '2025-08-22 10:00:00', '2026-03-05 09:00:00'),
(11, '未来科技',    1, '未来科技有限公司',       '软件开发', '', 80000.00, 100, 2, '2025-08-25 14:00:00', '2026-02-28 15:00:00'),
(12, '盛大活动',    1, '盛大活动策划有限公司',   '活动策划', '', 20000.00, 100, 2, '2025-09-01 08:30:00', '2026-03-10 10:00:00'),
(13, '设计实验室',  2, '设计实验室创意工作室',   '品牌设计', '', 15000.00, 100, 2, '2025-09-03 11:00:00', '2026-03-12 11:00:00'),
(14, '超级商城',    1, '超级商贸有限公司',       '零售百货', '', 60000.00, 100, 2, '2025-09-05 15:00:00', '2026-03-15 14:00:00'),
(15, '妙笔生花',    2, '妙笔生花文化传媒有限公司', '内容创作', '', 10000.00, 100, 2, '2025-09-08 09:00:00', '2026-03-08 16:00:00');

-- ------------------------------------------------------------
-- 3. 岗位分类 (9个一级分类 + 若干二级分类)
-- ------------------------------------------------------------

-- 一级分类 (parent_id = 0)
INSERT INTO job_category (name, parent_id, sort_order, status) VALUES
('家教',     0, 1, 1),
('餐饮服务', 0, 2, 1),
('零售促销', 0, 3, 1),
('活动策划', 0, 4, 1),
('IT技术',   0, 5, 1),
('设计创意', 0, 6, 1),
('文案写作', 0, 7, 1),
('实习兼职', 0, 8, 1),
('其他',     0, 9, 1);

-- 二级分类 (parent_id = 一级分类ID)
INSERT INTO job_category (name, parent_id, sort_order, status) VALUES
-- 家教子分类
('中小学辅导', 1, 1, 1),
('艺体培训',   1, 2, 1),
('语言培训',   1, 3, 1),
-- 餐饮服务子分类
('服务员',   2, 1, 1),
('咖啡师',   2, 2, 1),
('外卖配送', 2, 3, 1),
-- 零售促销子分类
('店员导购',     3, 1, 1),
('促销推广',     3, 2, 1),
('收银员',       3, 3, 1),
-- 活动策划子分类
('礼仪模特',     4, 1, 1),
('展会协助',     4, 2, 1),
('现场执行',     4, 3, 1),
-- IT技术子分类
('前端开发', 5, 1, 1),
('后端开发', 5, 2, 1),
('测试运维', 5, 3, 1),
-- 设计创意子分类
('平面设计',     6, 1, 1),
('UI/UX设计',    6, 2, 1),
('视频剪辑',     6, 3, 1),
-- 文案写作子分类
('新媒体运营',   7, 1, 1),
('内容撰稿',     7, 2, 1),
('翻译校对',     7, 3, 1),
-- 实习兼职子分类
('行政助理',   8, 1, 1),
('数据录入',   8, 2, 1),
('客服兼职',   8, 3, 1);

-- ------------------------------------------------------------
-- 4. 岗位发布 (20个岗位，覆盖多个雇主和分类)
--    状态: 0-草稿, 1-待审核, 2-已发布, 3-已拒绝, 4-已下架
--    审核状态: 0-待审核, 1-已通过, 2-已拒绝
--    薪资类型: 0-时薪, 1-日薪, 2-周薪, 3-月薪, 4-次薪
-- ------------------------------------------------------------

INSERT INTO job_post (publisher_id, category_id, title, description, location, salary_type, salary_amount, start_time, end_time, recruit_num, hired_num, status, audit_status, audit_remark, version, deleted, create_time, update_time) VALUES
-- 已发布的岗位 (status=2, audit_status=1)
(9,  1,  '高中数学辅导老师',
 '负责高中数学一对一辅导，要求数学功底扎实，有耐心，善于沟通。每周至少辅导3次，每次2小时。可在线上或线下进行教学。',
 '某某大学南门启明星教育中心', '1', 200.00, '2026-03-01 00:00:00', '2026-06-30 23:59:59', 5, 2, 2, 1, NULL, 1, 0, '2026-01-10 09:00:00', '2026-01-12 10:00:00'),

(9,  3,  '少儿英语口语陪练',
 '为小学生提供英语口语陪练服务，要求英语发音标准，性格活泼，喜欢和小朋友相处。需要准备简单的口语练习材料。',
 '某某市朝阳区少儿活动中心',   '1', 150.00, '2026-03-01 00:00:00', '2026-07-31 23:59:59', 8, 3, 2, 1, NULL, 2, 0, '2026-01-15 10:30:00', '2026-01-16 14:00:00'),

(10, 5,  '奶茶店兼职店员',
 '负责奶茶店的日常服务工作，包括点单、制作饮品、保持店内清洁等。工作时间灵活，可排班。有奶茶店经验优先。',
 '某某大学北门商业街茶颜悦色旗舰店', '0', 22.00, '2026-03-01 00:00:00', '2026-08-31 23:59:59', 10, 4, 2, 1, NULL, 3, 0, '2026-01-20 08:00:00', '2026-01-21 09:00:00'),

(10, 8,  '咖啡师学徒',
 '学习咖啡制作技巧，协助吧台日常工作。对咖啡文化有兴趣的同学优先，无需经验，我们会提供系统培训。',
 '某某大学南门茶颜悦色臻选店', '0', 20.00, '2026-04-01 00:00:00', '2026-09-30 23:59:59', 3, 0, 2, 1, NULL, 0, 0, '2026-02-01 11:00:00', '2026-02-02 16:00:00'),

(11, 15, 'Web前端开发实习生',
 '参与公司官网及内部管理系统前端开发，使用Vue.js框架。要求熟悉HTML/CSS/JavaScript，有Vue项目经验者优先。每周至少工作3天。',
 '某某市高新区未来科技大厦12F', '2', 800.00, '2026-03-15 00:00:00', '2026-06-15 23:59:59', 3, 1, 2, 1, NULL, 1, 0, '2026-02-10 09:00:00', '2026-02-12 10:00:00'),

(11, 16, 'Java后端开发实习生',
 '参与公司后端微服务开发，使用Spring Boot + MyBatis-Plus。要求熟悉Java基础，了解MySQL和Redis。有Spring Boot项目经验者优先。',
 '某某市高新区未来科技大厦12F', '2', 1000.00, '2026-03-15 00:00:00', '2026-06-15 23:59:59', 2, 1, 2, 1, NULL, 2, 0, '2026-02-10 09:30:00', '2026-02-12 10:30:00'),

(12, 10, '展会礼仪引导员',
 '负责大型展会现场的礼仪接待和引导工作，要求形象气质佳，身高165cm以上，有礼仪经验优先。展会为期3天，包工作餐。',
 '某某国际会展中心', '1', 250.00, '2026-04-10 08:00:00', '2026-04-12 18:00:00', 20, 15, 2, 1, NULL, 5, 0, '2026-02-20 10:00:00', '2026-02-22 11:00:00'),

(12, 11, '活动现场执行助理',
 '协助活动现场布置、签到、物资管理等执行工作。要求动手能力强，有团队协作精神。需提前一天到场彩排。',
 '某某国际会展中心', '1', 180.00, '2026-04-10 08:00:00', '2026-04-12 18:00:00', 10, 6, 2, 1, NULL, 3, 0, '2026-02-20 10:30:00', '2026-02-22 11:30:00'),

(13, 19, '平面设计兼职设计师',
 '负责公司客户的宣传海报、名片、宣传册等平面设计工作。要求熟练使用Photoshop、Illustrator，有作品集可展示。',
 '某某市创意产业园B栋设计实验室', '4', 500.00, '2026-03-01 00:00:00', '2026-12-31 23:59:59', 5, 2, 2, 1, NULL, 4, 0, '2026-02-05 14:00:00', '2026-02-07 09:00:00'),

(13, 20, 'UI界面设计实习生',
 '参与公司移动端App和Web平台的UI设计，使用Figma完成交互稿和高保真设计。要求有UI设计基础，了解设计规范。',
 '某某市创意产业园B栋设计实验室', '2', 600.00, '2026-04-01 00:00:00', '2026-07-31 23:59:59', 2, 0, 2, 1, NULL, 0, 0, '2026-02-25 09:00:00', '2026-02-26 15:00:00'),

(14, 10, '超市周末促销员',
 '负责超市内品牌产品的促销推广工作，主动向顾客介绍产品特点并引导购买。要求性格开朗，善于沟通。周末两天全天。',
 '某某市多家连锁超市（就近分配）', '1', 160.00, '2026-03-01 00:00:00', '2026-06-30 23:59:59', 8, 3, 2, 1, NULL, 2, 0, '2026-02-15 08:00:00', '2026-02-16 10:00:00'),

(14, 12, '便利店收银员',
 '负责便利店的日常收银和商品整理工作，需熟悉基本收银操作，工作态度认真负责。提供夜班和白班两种选择。',
 '某某大学周边超级便利连锁店', '0', 21.00, '2026-03-01 00:00:00', '2026-08-31 23:59:59', 6, 2, 2, 1, NULL, 1, 0, '2026-02-18 09:00:00', '2026-02-19 11:00:00'),

(15, 22, '微信公众号运营实习生',
 '负责公司微信公众号的日常内容编辑、排版和发布，追踪阅读数据并优化内容策略。要求文笔好，有新媒体运营经验优先。',
 '远程办公（每周需到公司一次开会）', '2', 500.00, '2026-03-10 00:00:00', '2026-06-10 23:59:59', 2, 1, 2, 1, NULL, 1, 0, '2026-02-28 09:00:00', '2026-03-01 10:00:00'),

(15, 23, '翻译兼职（英语→中文）',
 '负责公司英文文档、产品说明书的翻译工作，要求英语专业或通过CET-6，翻译准确流畅。按篇计费，稿件量充足。',
 '远程办公', '4', 300.00, '2026-03-01 00:00:00', '2026-12-31 23:59:59', 5, 1, 2, 1, NULL, 2, 0, '2026-02-10 11:00:00', '2026-02-12 14:00:00'),

-- 已下架的岗位 (status=4, audit_status=1)
(9,  1,  '暑期小学全科辅导老师',
 '暑期期间为小学生提供全科辅导，包括语文、数学、英语。要求有辅导经验，责任心强。',
 '某某大学南门启明星教育中心', '3', 4500.00, '2025-07-01 00:00:00', '2025-08-31 23:59:59', 6, 6, 4, 1, '活动已结束', 3, 0, '2025-06-01 09:00:00', '2025-09-01 10:00:00'),

(10, 4,  '暑期奶茶店暑期工',
 '暑期奶茶店全职兼职均可，工作内容包括制茶、点单、清洁等，提供完整培训。',
 '某某大学北门商业街茶颜悦色旗舰店', '3', 4000.00, '2025-07-01 00:00:00', '2025-08-31 23:59:59', 8, 8, 4, 1, '暑期结束', 4, 0, '2025-06-10 08:00:00', '2025-09-01 09:00:00'),

-- 待审核的岗位 (status=1, audit_status=0)
(12, 4,  '校园音乐节志愿者',
 '协助校园音乐节的现场布置、秩序维护和物资分发。音乐节为期两天，提供工作餐和纪念T恤。',
 '某某大学体育馆', '1', 120.00, '2026-05-01 08:00:00', '2026-05-02 22:00:00', 30, 0, 1, 0, NULL, 0, 0, '2026-03-25 16:00:00', '2026-03-25 16:00:00'),

(11, 17, '软件测试实习生',
 '参与公司产品的功能测试和自动化测试工作，编写测试用例并提交缺陷报告。要求细心严谨，了解软件测试基本概念。',
 '某某市高新区未来科技大厦12F', '2', 700.00, '2026-04-01 00:00:00', '2026-06-30 23:59:59', 2, 0, 1, 0, NULL, 0, 0, '2026-03-20 09:00:00', '2026-03-20 09:00:00'),

-- 被拒绝的岗位 (status=3, audit_status=2)
(14, 9, '快递分拣员',
 '负责快递仓库的包裹分拣和扫码入库工作，夜班为主，工作强度较大。',
 '某某市物流园区', '0', 18.00, '2026-03-01 00:00:00', '2026-12-31 23:59:59', 10, 0, 3, 2, '薪资低于最低标准，请调整', 2, 0, '2026-02-20 10:00:00', '2026-02-22 09:00:00'),

-- 草稿岗位 (status=0, audit_status=0)
(15, 21, '短视频脚本编剧',
 '负责公司抖音、B站账号的短视频脚本创作，要求有创意脑洞，了解短视频平台调性和流行趋势。有爆款作品经验者优先。',
 '远程办公', '4', 800.00, '2026-04-01 00:00:00', '2026-12-31 23:59:59', 3, 0, 0, 0, NULL, 0, 0, '2026-03-28 14:00:00', '2026-03-28 14:00:00');

-- ------------------------------------------------------------
-- 5. 报名申请 (30条，各种状态)
--    状态: 0-待审核, 1-已通过, 2-已拒绝, 3-已入职, 4-已取消
-- ------------------------------------------------------------

INSERT INTO application (job_id, applicant_id, resume_url, status, apply_time, review_time, reject_reason) VALUES
-- 岗位1: 高中数学辅导老师 (已发布, 招5录2)
(1, 3, '/resumes/zhangsan_math.pdf',        3, '2026-01-15 10:00:00', '2026-01-16 09:00:00', NULL),
(1, 4, '/resumes/lisi_english.pdf',          2, '2026-01-15 14:00:00', '2026-01-17 10:00:00', '数学专业背景不足'),
(1, 5, '/resumes/wangwu_cs.pdf',             3, '2026-01-16 08:30:00', '2026-01-17 11:00:00', NULL),
(1, 6, '/resumes/zhaoliu_physics.pdf',       0, '2026-03-20 09:00:00', NULL, NULL),

-- 岗位2: 少儿英语口语陪练 (已发布, 招8录3)
(2, 4, '/resumes/lisi_english.pdf',          3, '2026-01-20 09:00:00', '2026-01-21 10:00:00', NULL),
(2, 3, '/resumes/zhangsan_math.pdf',         2, '2026-01-20 11:00:00', '2026-01-22 09:00:00', '英语口语能力待提升'),
(2, 7, '/resumes/sunqi_design.pdf',          3, '2026-01-22 15:00:00', '2026-01-23 14:00:00', NULL),
(2, 8, '/resumes/zhouba_accounting.pdf',     1, '2026-03-10 10:00:00', '2026-03-12 16:00:00', NULL),
(2, 6, '/resumes/zhaoliu_physics.pdf',       0, '2026-03-18 14:30:00', NULL, NULL),

-- 岗位3: 奶茶店兼职店员 (已发布, 招10录4)
(3, 3, '/resumes/zhangsan_math.pdf',         3, '2026-01-25 08:00:00', '2026-01-26 09:00:00', NULL),
(3, 4, '/resumes/lisi_english.pdf',          3, '2026-01-25 09:30:00', '2026-01-26 10:00:00', NULL),
(3, 5, '/resumes/wangwu_cs.pdf',             3, '2026-01-25 10:00:00', '2026-01-26 11:00:00', NULL),
(3, 6, '/resumes/zhaoliu_physics.pdf',       3, '2026-01-26 14:00:00', '2026-01-27 09:00:00', NULL),
(3, 7, '/resumes/sunqi_design.pdf',          2, '2026-02-01 09:00:00', '2026-02-02 16:00:00', '排班时间冲突'),
(3, 8, '/resumes/zhouba_accounting.pdf',     0, '2026-03-05 16:00:00', NULL, NULL),

-- 岗位5: Web前端开发实习生 (已发布, 招3录1)
(5, 5, '/resumes/wangwu_cs.pdf',             3, '2026-02-15 10:00:00', '2026-02-17 09:00:00', NULL),
(5, 3, '/resumes/zhangsan_math.pdf',         2, '2026-02-16 11:00:00', '2026-02-18 10:00:00', '前端技术栈掌握不足'),

-- 岗位6: Java后端开发实习生 (已发布, 招2录1)
(6, 5, '/resumes/wangwu_cs.pdf',             3, '2026-02-15 10:30:00', '2026-02-17 09:30:00', NULL),
(6, 3, '/resumes/zhangsan_math.pdf',         2, '2026-02-16 14:00:00', '2026-02-18 11:00:00', 'Java项目经验欠缺'),

-- 岗位7: 展会礼仪引导员 (已发布, 招20录15)
(7, 4, '/resumes/lisi_english.pdf',          3, '2026-03-01 09:00:00', '2026-03-02 10:00:00', NULL),
(7, 7, '/resumes/sunqi_design.pdf',          3, '2026-03-01 10:00:00', '2026-03-02 11:00:00', NULL),
(7, 3, '/resumes/zhangsan_math.pdf',         4, '2026-03-05 14:00:00', NULL, NULL),

-- 岗位9: 平面设计兼职设计师 (已发布, 招5录2)
(9, 7, '/resumes/sunqi_design.pdf',          3, '2026-02-10 09:00:00', '2026-02-11 10:00:00', NULL),
(9, 4, '/resumes/lisi_english.pdf',          3, '2026-02-10 14:00:00', '2026-02-12 09:00:00', NULL),
(9, 5, '/resumes/wangwu_cs.pdf',             2, '2026-02-15 16:00:00', '2026-02-16 14:00:00', '设计作品风格不匹配'),

-- 岗位13: 微信公众号运营实习生 (已发布, 招2录1)
(13, 4, '/resumes/lisi_english.pdf',         3, '2026-03-05 08:00:00', '2026-03-06 09:00:00', NULL),

-- 岗位14: 翻译兼职 (已发布, 招5录1)
(14, 4, '/resumes/lisi_english.pdf',         3, '2026-02-20 10:00:00', '2026-02-22 14:00:00', NULL),
(14, 6, '/resumes/zhaoliu_physics.pdf',      0, '2026-03-15 09:00:00', NULL, NULL),

-- 岗位11: 超市周末促销员 (已发布, 招8录3)
(11, 3, '/resumes/zhangsan_math.pdf',        3, '2026-02-25 09:00:00', '2026-02-26 10:00:00', NULL),
(11, 6, '/resumes/zhaoliu_physics.pdf',      3, '2026-02-25 14:00:00', '2026-02-26 15:00:00', NULL),
(11, 8, '/resumes/zhouba_accounting.pdf',    3, '2026-02-28 10:00:00', '2026-03-01 09:00:00', NULL),

-- 岗位12: 便利店收银员 (已发布, 招6录2)
(12, 3, '/resumes/zhangsan_math.pdf',        3, '2026-03-01 08:00:00', '2026-03-02 09:00:00', NULL),
(12, 8, '/resumes/zhouba_accounting.pdf',    3, '2026-03-05 09:00:00', '2026-03-06 10:00:00', NULL),

-- 岗位20: 校园音乐节志愿者 (待审核, status=0)
(19, 5, '/resumes/wangwu_cs.pdf',            0, '2026-03-26 09:00:00', NULL, NULL),
(19, 7, '/resumes/sunqi_design.pdf',         0, '2026-03-26 10:00:00', NULL, NULL);

-- ------------------------------------------------------------
-- 6. 订单记录 (15条，基于已通过的申请生成)
--    支付状态: 0-待支付, 1-已支付, 2-已退款
--    结算状态: 0-待结算, 1-已结算
-- ------------------------------------------------------------

INSERT INTO order_record (application_id, student_id, employer_id, job_id, amount, pay_status, settlement_status, start_date, end_date, create_time, update_time) VALUES
-- 基于申请1: 张三→高中数学辅导 (已结算)
(1,  3,  9,  1,  3600.00, 1, 1, '2026-02-01 09:00:00', '2026-03-01 18:00:00', '2026-02-01 09:00:00', '2026-03-05 10:00:00'),

-- 基于申请3: 王五→高中数学辅导 (已结算)
(3,  5,  9,  1,  2800.00, 1, 1, '2026-02-15 14:00:00', '2026-03-10 18:00:00', '2026-02-15 14:00:00', '2026-03-12 09:00:00'),

-- 基于申请4: 李四→少儿英语陪练 (已结算)
(4,  4,  9,  2,  2700.00, 1, 1, '2026-02-01 09:00:00', '2026-03-01 18:00:00', '2026-02-01 09:00:00', '2026-03-08 14:00:00'),

-- 基于申请6: 孙七→少儿英语陪练 (已结算)
(6,  7,  9,  2,  1800.00, 1, 1, '2026-02-10 09:00:00', '2026-03-01 18:00:00', '2026-02-10 09:00:00', '2026-03-06 10:00:00'),

-- 基于申请8: 李四→奶茶店店员 (已结算)
(8,  3,  10, 3,  1320.00, 1, 1, '2026-02-01 09:00:00', '2026-02-28 21:00:00', '2026-02-01 09:00:00', '2026-03-05 09:00:00'),

-- 基于申请9: 李四→奶茶店店员 (已结算)
(9,  4,  10, 3,  1100.00, 1, 1, '2026-02-05 09:00:00', '2026-02-28 21:00:00', '2026-02-05 09:00:00', '2026-03-05 10:00:00'),

-- 基于申请10: 王五→奶茶店店员 (已结算)
(10, 5,  10, 3,  880.00, 1, 1, '2026-02-10 09:00:00', '2026-02-28 21:00:00', '2026-02-10 09:00:00', '2026-03-05 11:00:00'),

-- 基于申请11: 赵六→奶茶店店员 (已支付待结算)
(11, 6,  10, 3,  660.00, 1, 0, '2026-03-01 09:00:00', '2026-03-31 21:00:00', '2026-03-01 09:00:00', '2026-03-01 09:00:00'),

-- 基于申请13: 王五→Web前端实习 (已支付待结算)
(13, 5,  11, 5,  4800.00, 1, 0, '2026-03-15 09:00:00', '2026-06-15 18:00:00', '2026-03-15 09:00:00', '2026-03-15 09:00:00'),

-- 基于申请15: 王五→Java后端实习 (已支付待结算)
(15, 5,  11, 6,  6000.00, 1, 0, '2026-03-15 09:00:00', '2026-06-15 18:00:00', '2026-03-15 09:00:00', '2026-03-15 09:00:00'),

-- 基于申请16: 李四→展会礼仪 (已结算)
(16, 4,  12, 7,  750.00, 1, 1, '2026-04-10 08:00:00', '2026-04-12 18:00:00', '2026-04-10 08:00:00', '2026-04-15 10:00:00'),

-- 基于申请17: 孙七→展会礼仪 (已结算)
(17, 7,  12, 7,  750.00, 1, 1, '2026-04-10 08:00:00', '2026-04-12 18:00:00', '2026-04-10 08:00:00', '2026-04-15 11:00:00'),

-- 基于申请19: 孙七→平面设计 (已结算)
(19, 7,  13, 9,  1500.00, 1, 1, '2026-03-01 09:00:00', '2026-03-31 18:00:00', '2026-03-01 09:00:00', '2026-04-05 10:00:00'),

-- 基于申请24: 李四→公众号运营 (已支付待结算)
(24, 4,  15, 13, 3000.00, 1, 0, '2026-03-10 09:00:00', '2026-06-10 18:00:00', '2026-03-10 09:00:00', '2026-03-10 09:00:00'),

-- 基于申请25: 李四→翻译兼职 (已结算)
(25, 4,  15, 14, 900.00, 1, 1, '2026-03-01 09:00:00', '2026-03-31 18:00:00', '2026-03-01 09:00:00', '2026-04-02 14:00:00'),

-- 基于申请27: 张三→超市促销员 (已结算)
(27, 3,  14, 11, 2880.00, 1, 1, '2026-03-01 09:00:00', '2026-03-31 18:00:00', '2026-03-01 09:00:00', '2026-04-05 09:00:00'),

-- 基于申请30: 张三→便利店收银 (已支付待结算)
(30, 3,  14, 12, 504.00, 1, 0, '2026-03-01 09:00:00', '2026-03-31 21:00:00', '2026-03-01 09:00:00', '2026-03-01 09:00:00');

-- ------------------------------------------------------------
-- 7. 评价 (16条，基于已结算的订单)
--    评分: 1-5, 评价类型: STUDENT_TO_EMPLOYER / EMPLOYER_TO_STUDENT
-- ------------------------------------------------------------

INSERT INTO review (order_id, reviewer_id, target_id, rating, comment, type, create_time, update_time) VALUES
-- 订单1: 张三(3)→启明星(9) 互评
(1,  3,  9,  5, '启明星教育的团队非常专业，工作安排合理，工资结算及时，沟通顺畅。非常推荐！', 'STUDENT_TO_EMPLOYER',    '2026-03-06 09:00:00', '2026-03-06 09:00:00'),
(1,  9,  3,  5, '张三同学数学功底扎实，教学认真负责，学生反馈非常好，准时上课不缺席。', 'EMPLOYER_TO_STUDENT',    '2026-03-07 10:00:00', '2026-03-07 10:00:00'),

-- 订单2: 王五(5)→启明星(9) 互评
(2,  5,  9,  4, '整体体验不错，工作时间灵活，但偶尔排班会临时调整，希望提前通知。',       'STUDENT_TO_EMPLOYER',    '2026-03-13 10:00:00', '2026-03-13 10:00:00'),
(2,  9,  5,  5, '王五同学表现优秀，讲解清晰耐心，与学生互动很好，期待下次继续合作。',     'EMPLOYER_TO_STUDENT',    '2026-03-13 14:00:00', '2026-03-13 14:00:00'),

-- 订单3: 李四(4)→启明星(9) 互评
(3,  4,  9,  5, '工作氛围很好，同事之间互帮互助，薪资准时发放，是一份很好的兼职经历。',   'STUDENT_TO_EMPLOYER',    '2026-03-09 11:00:00', '2026-03-09 11:00:00'),
(3,  9,  4,  4, '李四同学英语能力强，和小朋友相处融洽，偶尔上课前准备可以更充分一些。',   'EMPLOYER_TO_STUDENT',    '2026-03-09 15:00:00', '2026-03-09 15:00:00'),

-- 订单5: 张三(3)→茶颜悦色(10) 互评
(5,  3,  10, 4, '奶茶店工作节奏快但学到很多，团队氛围好，就是高峰期比较忙，需要快速适应。', 'STUDENT_TO_EMPLOYER',   '2026-03-06 08:00:00', '2026-03-06 08:00:00'),
(5,  10, 3, 5, '张三同学手脚麻利，学习能力强，很快就独立上手了，高峰期表现非常稳定。',    'EMPLOYER_TO_STUDENT',   '2026-03-06 09:00:00', '2026-03-06 09:00:00'),

-- 订单16: 李四(4)→盛大活动(12) 互评
(16, 4,  12, 4, '展会工作强度较大但很有趣，认识了很多人，包餐的饭菜质量不错。',          'STUDENT_TO_EMPLOYER',    '2026-04-16 09:00:00', '2026-04-16 09:00:00'),
(16, 12, 4,  5, '李四同学形象气质好，接待热情周到，现场应变能力强，是优秀的礼仪人员。',  'EMPLOYER_TO_STUDENT',    '2026-04-16 11:00:00', '2026-04-16 11:00:00'),

-- 订单17: 孙七(7)→盛大活动(12) 互评
(17, 7,  12, 5, '非常棒的展会体验！组织有序，工作人员友好，收获满满，下次还愿意参加。',  'STUDENT_TO_EMPLOYER',    '2026-04-16 10:00:00', '2026-04-16 10:00:00'),
(17, 12, 7, 4, '孙七同学表现积极，主动承担额外任务，就是偶尔手机看太多需要提醒。',      'EMPLOYER_TO_STUDENT',    '2026-04-16 14:00:00', '2026-04-16 14:00:00'),

-- 订单19: 孙七(7)→设计实验室(13) 互评
(19, 7,  13, 5, '设计实验室的氛围特别好，创意空间自由度高，负责人给的反馈很有建设性！',  'STUDENT_TO_EMPLOYER',    '2026-04-06 09:00:00', '2026-04-06 09:00:00'),
(19, 13, 7, 5, '孙七同学设计感很强，作品质量超出预期，沟通效率高，按时交付不拖延。',    'EMPLOYER_TO_STUDENT',    '2026-04-06 11:00:00', '2026-04-06 11:00:00'),

-- 订单28: 张三(3)→超级商城(14) 互评
(28, 3,  14, 3, '促销工作比较辛苦，站一天腿很酸，但薪资结算准时，适合锻炼沟通能力。',    'STUDENT_TO_EMPLOYER',    '2026-04-06 10:00:00', '2026-04-06 10:00:00'),
(28, 14, 3, 4, '张三同学性格开朗善于沟通，促销业绩不错，就是周六偶尔迟到几分钟。',      'EMPLOYER_TO_STUDENT',    '2026-04-06 14:00:00', '2026-04-06 14:00:00');

-- ------------------------------------------------------------
-- 8. 系统字典 (扩展更多字典类型)
-- ------------------------------------------------------------

-- 性别字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort_order, status) VALUES
('gender', '0', '未知', 1, 1),
('gender', '1', '男',   2, 1),
('gender', '2', '女',   3, 1);

-- 薪资类型字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort_order, status) VALUES
('salary_type', '0', '时薪', 1, 1),
('salary_type', '1', '日薪', 2, 1),
('salary_type', '2', '周薪', 3, 1),
('salary_type', '3', '月薪', 4, 1),
('salary_type', '4', '次薪', 5, 1);

-- 岗位状态字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort_order, status) VALUES
('job_status', '0', '草稿',   1, 1),
('job_status', '1', '待审核', 2, 1),
('job_status', '2', '已发布', 3, 1),
('job_status', '3', '已拒绝', 4, 1),
('job_status', '4', '已下架', 5, 1);

-- 审核状态字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort_order, status) VALUES
('audit_status', '0', '待审核', 1, 1),
('audit_status', '1', '已通过', 2, 1),
('audit_status', '2', '已拒绝', 3, 1);

-- 申请状态字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort_order, status) VALUES
('apply_status', '0', '待审核', 1, 1),
('apply_status', '1', '已通过', 2, 1),
('apply_status', '2', '已拒绝', 3, 1),
('apply_status', '3', '已录用', 4, 1),
('apply_status', '4', '已取消', 5, 1);

-- 支付状态字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort_order, status) VALUES
('pay_status', '0', '待支付', 1, 1),
('pay_status', '1', '已支付', 2, 1),
('pay_status', '2', '已退款', 3, 1);

-- 结算状态字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort_order, status) VALUES
('settlement_status', '0', '待结算', 1, 1),
('settlement_status', '1', '已结算', 2, 1);

-- 实名认证状态字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort_order, status) VALUES
('verified_status', '0', '未认证', 1, 1),
('verified_status', '1', '审核中', 2, 1),
('verified_status', '2', '已认证', 3, 1),
('verified_status', '3', '认证失败', 4, 1);

-- 评价类型字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort_order, status) VALUES
('review_type', 'STUDENT_TO_EMPLOYER', '学生对雇主评价', 1, 1),
('review_type', 'EMPLOYER_TO_STUDENT', '雇主对学生评价', 2, 1);

-- 学历字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort_order, status) VALUES
('education', '1', '大专',   1, 1),
('education', '2', '本科',   2, 1),
('education', '3', '硕士',   3, 1),
('education', '4', '博士',   4, 1);

-- 年级字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort_order, status) VALUES
('grade', '1', '大一',   1, 1),
('grade', '2', '大二',   2, 1),
('grade', '3', '大三',   3, 1),
('grade', '4', '大四',   4, 1),
('grade', '5', '研一',   5, 1),
('grade', '6', '研二',   6, 1),
('grade', '7', '研三',   7, 1),
('grade', '8', '已毕业', 8, 1);
