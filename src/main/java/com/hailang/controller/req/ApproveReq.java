package com.hailang.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "审批请求")
public class ApproveReq {
    @Schema(description = "审批uuid")
    private String uuid;

    @Schema(description = "申请uuid")
    private String applyUuid;

    @Schema(description = "驳回原因")
    private String reject;
}
