package com.hailang.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LeaveBalanceDTO {
    private String uuid;
    private String userUuid;
    private Integer year;
    private BigDecimal annualRemainingHours;
    private BigDecimal compRemainingHours;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
