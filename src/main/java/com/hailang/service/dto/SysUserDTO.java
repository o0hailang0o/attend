package com.hailang.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUserDTO {
    private String uuid;
    private String name;
    private String account;
    private String nickName;
    private Integer gender;
    private String workNum;
    private String level;
    private String position;
    private String positionUuid;
    private String ruleUuid;
    private String ruleName;
    private String companyId;
    private String deptUuid;
    private String deptName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
