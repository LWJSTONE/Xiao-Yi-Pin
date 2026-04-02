-- ============================================================
-- 校园兼职网站 - 数据库初始化脚本
-- ============================================================

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
    status          INT             DEFAULT 1                COMMENT '状态',
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
    status          INT             DEFAULT 0                COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝, 3-已入职, 4-已退出',
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
    dict_type       VARCHAR(50)     NOT NULL                 COMMENT '字典类型（主键）',
    dict_code       VARCHAR(50)     NOT NULL                 COMMENT '字典编码（主键）',
    dict_label      VARCHAR(100)    DEFAULT NULL             COMMENT '字典标签',
    sort            INT             DEFAULT 0                COMMENT '排序号',
    status          INT             DEFAULT 1                COMMENT '状态: 0-禁用, 1-启用',
    PRIMARY KEY (dict_type, dict_code),
    KEY idx_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 管理员用户（密码: 123456）
INSERT INTO sys_user (username, password_hash, phone, email, role_type, status, deleted, create_time, update_time)
VALUES ('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '13700137000', 'admin@campus.com', 'ADMIN', 1, 0, NOW(), NOW());

-- 测试学生用户（密码: 123456）
INSERT INTO sys_user (username, password_hash, phone, email, role_type, status, deleted, create_time, update_time)
VALUES ('student', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '13800138001', 'student@campus.com', 'STUDENT', 1, 0, NOW(), NOW());

-- 测试雇主用户（密码: 123456）
INSERT INTO sys_user (username, password_hash, phone, email, role_type, status, deleted, create_time, update_time)
VALUES ('employer', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '13900139001', 'employer@campus.com', 'EMPLOYER', 1, 0, NOW(), NOW());

-- 默认岗位分类
INSERT INTO job_category (name, parent_id, sort_order, status) VALUES
('家教', 0, 1, 1),
('餐饮服务', 0, 2, 1),
('零售促销', 0, 3, 1),
('活动策划', 0, 4, 1),
('IT技术', 0, 5, 1),
('设计创意', 0, 6, 1),
('文案写作', 0, 7, 1),
('实习兼职', 0, 8, 1),
('其他', 0, 9, 1);

-- 薪资类型字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort, status) VALUES
('salary_type', 'hourly', '时薪', 1, 1),
('salary_type', 'daily', '日薪', 2, 1),
('salary_type', 'monthly', '月薪', 3, 1),
('salary_type', 'piece', '计件', 4, 1);

-- 岗位状态字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort, status) VALUES
('job_status', 'open', '招聘中', 1, 1),
('job_status', 'closed', '已关闭', 2, 1),
('job_status', 'expired', '已过期', 3, 1);

-- 审核状态字典
INSERT INTO sys_dict (dict_type, dict_code, dict_label, sort, status) VALUES
('audit_status', 'pending', '待审核', 1, 1),
('audit_status', 'approved', '已通过', 2, 1),
('audit_status', 'rejected', '已拒绝', 3, 1);
