package com.hailang.controller.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "审批步骤")
public class WorkflowStepResp {
    @Schema(description = "申请uuid")
    private String applyUuid;

    @Schema(description = "审批uuid")
    private String approveUuid;

    @Schema(description = "审批人uuid")
    private String leaderUuid;

    @Schema(description = "审批人姓名")
    private String leaderName;

    @Schema(description = "审批顺序")
    private Integer order;

    @Schema(description = "审批状态 0删除 3驳回 4待审批 5审批中 1通过 9审批通过")
    private Integer status;

    @Schema(description = "审批状态名称")
    private String statusName;
}
