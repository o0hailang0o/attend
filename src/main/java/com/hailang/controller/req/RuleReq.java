package com.hailang.controller.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@Schema(description = "规则请求")
public class RuleReq {
    @Schema(description = "uuid（修改时传）")
    private String uuid;

    @Schema(description = "规则名称")
    private String name;

    @Schema(description = "弹性几小时")
    private Integer flexibility;

    @Schema(description = "上班时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Schema(description = "下班时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "中午是否有午休")
    private Integer middleRest;

    @Schema(description = "午休开始时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime middleStart;

    @Schema(description = "午休结束时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime middleEnd;

    @Schema(description = "是否有年假")
    private Integer vacation;

    @Schema(description = "是否有调休假")
    private Integer comp;

    @Schema(description = "精确度0.5或1")
    private BigDecimal accuracy;
}
