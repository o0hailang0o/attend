package com.hailang.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "假期类型请求")
public class LeaveTypeReq {
    @Schema(description = "uuid（修改时传）")
    private String uuid;

    @Schema(description = "假期类型名称")
    private String name;

    @Schema(description = "显示颜色")
    private String color;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否扣减余额 0不扣 1扣减")
    private Integer deductBalance;
}
