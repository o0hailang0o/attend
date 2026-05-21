package com.hailang.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ApplyDTO {
    private String uuid;
    private LocalDateTime month;
    private Integer type;
    private Integer lengthType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal length;
    private String applyUserUuid;
    private String leaderId;
    private String reject;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
