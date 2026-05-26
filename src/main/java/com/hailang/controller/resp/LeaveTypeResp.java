package com.hailang.controller.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "假期类型响应")
public class LeaveTypeResp {
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "uuid")
    private String uuid;

    @Schema(description = "假期类型名称")
    private String name;

    @Schema(description = "显示颜色")
    private String color;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否扣减余额")
    private Integer deductBalance;
}
