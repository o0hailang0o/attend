package com.hailang.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DailyAttendanceDTO {
    private Long id;
    private String uuid;
    private String employeeUuid;
    private String employeeName;
    private LocalDate date;
    private LocalTime clockIn;
    private LocalTime clockOut;
    private BigDecimal actualWorkHours;
    private BigDecimal recognizedHours;
    private BigDecimal leaveHours;
    private BigDecimal annualLeaveHours;
    private BigDecimal compLeaveHours;
    private Integer dayType;
    private Integer status;
    private String dayTypeName;
    private String statusName;
}
