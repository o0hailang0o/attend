package com.hailang.controller.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "审批响应")
public class ApproveResp {
    @Schema(description = "审批uuid")
    private String uuid;

    @Schema(description = "申请uuid")
    private String applyUuid;

    @Schema(description = "审批顺序")
    private Integer order;

    @Schema(description = "下一审批人")
    private String leaderId;

    @Schema(description = "驳回原因")
    private String reject;

    @Schema(description = "状态 9通过 3驳回 2未通过 1通过 0删除")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
