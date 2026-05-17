CREATE TABLE sys_user (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    uuid        VARCHAR(32)  NOT NULL UNIQUE              COMMENT '业务主键',
    username    VARCHAR(50)  NOT NULL                     COMMENT '用户名',
    password    VARCHAR(64)  NOT NULL                     COMMENT '密码(MD5)',
    real_name   VARCHAR(50)                                COMMENT '真实姓名',
    email       VARCHAR(100)                               COMMENT '邮箱',
    phone       VARCHAR(20)                                COMMENT '手机号',
    status      TINYINT      DEFAULT 1                    COMMENT '状态 0-禁用 1-启用',
    token       VARCHAR(64)                                COMMENT '登录令牌',
    create_time DATETIME                                   COMMENT '创建时间',
    update_time DATETIME                                   COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

CREATE TABLE rule (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    name        VARCHAR(50)                               COMMENT '规则名称',
    type        VARCHAR(50)                               COMMENT '规则类型',
    value       VARCHAR(255)                              COMMENT '规则值',
    `desc`      VARCHAR(255)                              COMMENT '规则描述',
    sort        INT                                       COMMENT '排序',
    status      TINYINT      DEFAULT 1                    COMMENT '状态 0-禁用 1-启用',
    create_time DATETIME                                  COMMENT '创建时间',
    update_time DATETIME                                  COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规则';

CREATE TABLE attendance_apply (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    uuid        VARCHAR(32)  NOT NULL UNIQUE              COMMENT '业务主键',
    user_uuid   VARCHAR(32)  NOT NULL                     COMMENT '申请人uuid',
    type        VARCHAR(20)  NOT NULL                     COMMENT '申请类型 请假/加班/调休/外出',
    start_time  DATETIME     NOT NULL                     COMMENT '开始时间',
    end_time    DATETIME     NOT NULL                     COMMENT '结束时间',
    reason      VARCHAR(500)                               COMMENT '申请原因',
    status      TINYINT      DEFAULT 0                    COMMENT '状态 0-待审批 1-已通过 2-已驳回',
    create_time DATETIME                                   COMMENT '创建时间',
    update_time DATETIME                                   COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤申请';
