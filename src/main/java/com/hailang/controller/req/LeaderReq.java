package com.hailang.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "领导请求")
public class LeaderReq {
    @Schema(description = "主键id（新增时无需传）")
    private Long id;

    @Schema(description = "领导用户uuid（修改时传）")
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
