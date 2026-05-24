CREATE TABLE sys_user (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
    uuid        VARCHAR(64)  NOT NULL UNIQUE              COMMENT 'uuid',
    name        VARCHAR(32)  NOT NULL                     COMMENT '姓名',
    account      VARCHAR(32)  NOT NULL                     COMMENT '账号',
    password    VARCHAR(1000) NOT NULL                    COMMENT '密码',
    nick_name   VARCHAR(32)                                COMMENT '昵称',
    gender      INT(10)      NOT NULL DEFAULT 1           COMMENT '性别',
    work_num    VARCHAR(32)                                COMMENT '工号',
    level       VARCHAR(16)                                COMMENT '级别',
    position    VARCHAR(32)                                COMMENT '职称',
    position_uuid VARCHAR(64)                              COMMENT '职位uuid',
    rule_uuid   VARCHAR(64)                                COMMENT '考勤规则uuid',
    rule_name   VARCHAR(64)                                COMMENT '考勤规则名称',
    company_id  VARCHAR(64)                                COMMENT '公司id',
    dept_uuid   VARCHAR(64)                                COMMENT '部门uuid',
    dept_name   VARCHAR(64)                                COMMENT '部门名称',
    is_delete   INT(10)      NOT NULL DEFAULT 1            COMMENT '假删除 0删除 1保留',
    create_time TIMESTAMP                                  COMMENT '创建时间',
    update_time TIMESTAMP                                  COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

CREATE TABLE rule (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
    uuid          VARCHAR(64)  NOT NULL UNIQUE             COMMENT 'uuid',
    name          VARCHAR(64)                              COMMENT '规则名称',
    flexibility   INT(10)      NOT NULL DEFAULT 0          COMMENT '弹性几小时',
    start_time    TIME         NOT NULL                    COMMENT '上班时间',
    end_time      TIME         NOT NULL                    COMMENT '下班时间',
    middle_rest   INT(10)                                  COMMENT '中午是否有午休',
    middle_start  TIME                                     COMMENT '午休开始时间',
    middle_end    TIME                                     COMMENT '午休结束时间',
    vacation      INT(10)                                  COMMENT '是否有年假',
    comp          INT(10)                                  COMMENT '是否有调休假',
    accuracy      DECIMAL(9,1)                             COMMENT '精确度0.5或1',
    is_delete     INT(10)      NOT NULL DEFAULT 0           COMMENT '假删除 0删除 1保留'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规则';

CREATE TABLE apply (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '申请单主键id',
    uuid        VARCHAR(64)  NOT NULL UNIQUE              COMMENT '申请uuid',
    month       TIMESTAMP    NOT NULL                     COMMENT '申请月份',
    type        INT(10)      NOT NULL                     COMMENT '申请类型',
    length_type INT(10)      NOT NULL                     COMMENT '请假时间类型',
    start_time  TIMESTAMP    NOT NULL                     COMMENT '开始时间',
    end_time    TIMESTAMP    NOT NULL                     COMMENT '结束时间',
    length      DECIMAL(9)   NOT NULL                     COMMENT '时长',
    apply_user_uuid VARCHAR(64)                            COMMENT '申请人uuid',
    leader_uuid VARCHAR(64)  NOT NULL                     COMMENT '审批人uuid',
    reject      VARCHAR(64)  NOT NULL                     COMMENT '驳回原因',
    reason      VARCHAR(500)                               COMMENT '请假事由',
    status      INT(10)                                   COMMENT '状态 1提交 4审批中 2通过 3撤销 9未通过',
    is_delete   INT(10)      NOT NULL DEFAULT 1            COMMENT '假删除 0删除 1保留',
    create_time TIMESTAMP    NOT NULL                     COMMENT '创建时间',
    update_time TIMESTAMP                                  COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤申请';

CREATE TABLE approve (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '审批主键id',
    uuid        VARCHAR(64)  NOT NULL UNIQUE              COMMENT '审批uuid',
    apply_uuid  VARCHAR(64)  NOT NULL                     COMMENT '申请uuid',
    `order`     INT(10)                                   COMMENT '审批顺序',
    leader_uuid VARCHAR(64)                               COMMENT '下一审批人',
    reject      VARCHAR(500) NOT NULL DEFAULT ''           COMMENT '驳回原因',
    status      INT(10)      NOT NULL DEFAULT 0           COMMENT '状态 0删除 3驳回 4待审批 5审批中 9审批完成',
    is_delete   INT(10)      NOT NULL DEFAULT 1            COMMENT '假删除 0删除 1保留',
    create_time TIMESTAMP    NOT NULL                     COMMENT '创建时间',
    update_time TIMESTAMP                                  COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批';

CREATE TABLE dept (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
    uuid        VARCHAR(64)  NOT NULL UNIQUE              COMMENT 'uuid',
    name        VARCHAR(64)  NOT NULL                     COMMENT '部门名称',
    parent_uuid VARCHAR(64)                               COMMENT '上级部门uuid',
    is_delete   INT(10)      NOT NULL DEFAULT 1            COMMENT '假删除 0删除 1保留',
    create_time TIMESTAMP                                  COMMENT '创建时间',
    update_time TIMESTAMP                                  COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门';

CREATE TABLE `position` (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
    uuid        VARCHAR(64)  NOT NULL UNIQUE              COMMENT 'uuid',
    name        VARCHAR(64)  NOT NULL                     COMMENT '职位名称',
    is_delete   INT(10)      NOT NULL DEFAULT 1            COMMENT '假删除 0删除 1保留',
    create_time TIMESTAMP                                  COMMENT '创建时间',
    update_time TIMESTAMP                                  COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位';

CREATE TABLE leader (
    id           BIGINT       PRIMARY KEY COMMENT '主键id',
    leader_uuid  VARCHAR(64)                              COMMENT '领导用户uuid',
    leader_name  VARCHAR(32)                              COMMENT '领导姓名',
    parent_id    BIGINT                                   COMMENT '上级领导id',
    level        INT(10)                                  COMMENT '级别',
    tree         VARCHAR(2000)                            COMMENT '审批链'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公司领导审批链';

CREATE TABLE leave_balance (
    id                     BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
    uuid                   VARCHAR(64)  NOT NULL UNIQUE              COMMENT '业务uuid',
    user_uuid              VARCHAR(64)  NOT NULL                     COMMENT '用户uuid',
    year                   INT(10)      NOT NULL                     COMMENT '年度',
    annual_remaining_hours DECIMAL(5,1) NOT NULL DEFAULT 0           COMMENT '年假剩余小时数',
    comp_remaining_hours   DECIMAL(5,1) NOT NULL DEFAULT 0           COMMENT '调休假剩余小时数',
    is_delete              INT(10)      NOT NULL DEFAULT 1            COMMENT '假删除 0删除 1保留',
    create_time            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time            TIMESTAMP    NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_user_year (user_uuid, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='假期余额';
