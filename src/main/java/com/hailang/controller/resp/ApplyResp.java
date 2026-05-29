package com.hailang.controller.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "考勤申请响应")
public class ApplyResp {
    @Schema(description = "申请uuid")
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

    @Schema(description = "时长")
    private BigDecimal length;

    @Schema(description = "申请人uuid")
    private String applyUserUuid;

    @Schema(description = "审批人uuid")
    private String leaderUuid;

    @Schema(description = "驳回原因")
    private String reject;

    @Schema(description = "请假事由")
    private String reason;

    @Schema(description = "状态 1提交 2保存 3驳回 4待审批 5审批中 9审批通过")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "审批流程")
    private List<WorkflowStepResp> workflow;
}
