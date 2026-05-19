package com.hailang.controller.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "部门响应")
public class DeptResp {
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "uuid")
    private String uuid;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "上级部门uuid")
    private String parentUuid;
}
