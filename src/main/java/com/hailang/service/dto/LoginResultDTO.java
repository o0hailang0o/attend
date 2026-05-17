package com.hailang.service.dto;

import lombok.Data;

@Data
public class LoginResultDTO {
    private String token;
    private Long userId;
    private String account;
    private String name;
    private String uuid;
}
