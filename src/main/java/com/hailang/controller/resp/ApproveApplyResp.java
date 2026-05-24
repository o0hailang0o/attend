package com.hailang.controller.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "审批申请综合响应")
public class ApproveApplyResp {
    @Schema(description = "审批uuid")
    private String approveUuid;

    @Schema(description = "申请uuid")
    private String applyUuid;

    @Schema(description = "审批顺序")
    private Integer order;

    @Schema(description = "审批状态 0删除 3驳回 4待审批 5审批中 1通过 9审批通过")
    private Integer approveStatus;

    @Schema(description = "审批状态名称")
    private String approveStatusName;

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

    @Schema(description = "请假事由")
    private String reason;

    @Schema(description = "申请人uuid")
    private String applyUserUuid;

    @Schema(description = "申请人姓名")
    private String applyUserName;

    @Schema(description = "申请人工号")
    private String applyUserWorkNum;

    @Schema(description = "驳回原因")
    private String reject;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "审批流程")
    private List<WorkflowStepResp> workflow;
}
