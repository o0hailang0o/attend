package com.hailang.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "部门请求")
public class DeptReq {
    @Schema(description = "uuid（修改时传）")
    private String uuid;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "上级部门uuid")
    private String parentUuid;
}
