package com.hailang.controller.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "考勤申请响应")
public class ApplyResp {
    @Schema(description = "申请uuid")
    private String uuid;

    @Schema(description = "申请月份")
    private LocalDateTime month;

    @Schema(description = "申请类型")
    private Integer type;

    @Schema(description = "请假时间类型")
    private Integer lengthType;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "时长")
    private BigDecimal length;

    @Schema(description = "状态 1提交 2驳回 3撤销 9未通过")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
