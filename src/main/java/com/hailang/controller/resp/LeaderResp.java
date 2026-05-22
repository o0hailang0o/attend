package com.hailang.controller.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "领导响应")
public class LeaderResp {
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "领导用户uuid")
    private String leaderUuid;

    @Schema(description = "领导姓名")
    private String leaderName;

    @Schema(description = "上级领导id")
    private Long parentId;

    @Schema(description = "级别")
    private Integer level;

    @Schema(description = "审批链")
    private String tree;
}
