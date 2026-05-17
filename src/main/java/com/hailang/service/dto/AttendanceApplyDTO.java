package com.hailang.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceApplyDTO {
    private String uuid;
    private String userUuid;
    private String type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
