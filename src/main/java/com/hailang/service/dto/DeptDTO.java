package com.hailang.service.dto;

import lombok.Data;

@Data
public class DeptDTO {
    private Long id;
    private String uuid;
    private String name;
    private String parentUuid;
}
