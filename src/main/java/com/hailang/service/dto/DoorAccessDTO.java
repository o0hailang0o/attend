package com.hailang.service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DoorAccessDTO {
    private Long id;
    private String uuid;
    private String employeeUuid;
    private String employeeName;
    private String workNum;
    private String doorNo;
    private Integer direction;
    private LocalTime accessTime;
    private LocalDate accessDate;
}
