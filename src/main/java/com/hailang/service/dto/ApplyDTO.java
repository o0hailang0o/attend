package com.hailang.service.dto;

import com.hailang.controller.resp.WorkflowStepResp;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApplyDTO {
    private String uuid;
    private LocalDate month;
    private Integer type;
    private Integer lengthType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal length;
    private String applyUserUuid;
    private String leaderUuid;
    private String reject;
    private String reason;
    private Integer status;
    private String statusName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<WorkflowStepResp> workflow;
}
