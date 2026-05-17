package com.hailang.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUserDTO {
    private String uuid;
    private String name;
    private String accout;
    private String nickName;
    private Integer gender;
    private String workNum;
    private String level;
    private String type;
    private String companyId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
