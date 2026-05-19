package com.hailang.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "职位请求")
public class PositionReq {
    @Schema(description = "uuid（修改时传）")
    private String uuid;

    @Schema(description = "职位名称")
    private String name;
}
