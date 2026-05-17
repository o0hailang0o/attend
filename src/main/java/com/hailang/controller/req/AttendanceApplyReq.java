package com.hailang.controller.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "考勤申请请求")
public class AttendanceApplyReq {
    @Schema(description = "申请人uuid")
    private String userUuid;

    @Schema(description = "申请人uuid")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate month;

    @Schema(description = "申请类型 请假/加班/调休/外出")
    private String type;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "申请原因")
    private String reason;
}
