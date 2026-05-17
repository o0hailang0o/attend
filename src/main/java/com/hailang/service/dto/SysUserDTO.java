package com.hailang.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUserDTO {
    private String uuid;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
