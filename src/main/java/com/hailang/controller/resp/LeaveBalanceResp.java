package com.hailang.controller.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "假期余额响应")
public class LeaveBalanceResp {
    @Schema(description = "业务uuid")
    private String uuid;

    @Schema(description = "年度")
    private Integer year;

    @Schema(description = "年假剩余小时数")
    private BigDecimal annualRemainingHours;

    @Schema(description = "调休假剩余小时数")
    private BigDecimal compRemainingHours;
}
