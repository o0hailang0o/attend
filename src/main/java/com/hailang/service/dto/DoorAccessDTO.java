package com.hailang.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DoorAccessDTO {
    private Long id;
    private String uuid;
    private String employeeUuid;
    private String employeeName;
    private String workNum;
    private String doorNo;
    private Integer direction;
    private LocalDateTime accessDatetime;
}
