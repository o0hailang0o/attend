package com.hailang.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "考勤申请请求")
public class ApplyReq {
    @Schema(description = "申请uuid（编辑时必填）")
    private String uuid;
    @Schema(description = "申请月份")
    private LocalDate month;
    @Schema(description = "申请类型")
    private Integer type;
    @Schema(description = "请假时间类型")
    private Integer lengthType;
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    @Schema(description = "请假事由")
    private String reason;

    @Schema(description = "申请人uuid")
    private String applyUserUuid;

    @Schema(description = "审批人uuid")
    private String leaderUuid;
}
