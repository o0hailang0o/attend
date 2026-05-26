package com.hailang.service.dto;

import lombok.Data;

@Data
public class LeaveTypeDTO {
    private Long id;
    private String uuid;
    private String name;
    private String color;
    private Integer sortOrder;
    private Integer deductBalance;
}
