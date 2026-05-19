package com.hailang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@TableName("rule")
@Schema(description = "规则")
public class Rule {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "uuid")
    private String uuid;

    @Schema(description = "规则名称")
    private String name;

    @Schema(description = "弹性几小时")
    private Integer flexibility;

    @Schema(description = "上班时间")
    private LocalTime startTime;

    @Schema(description = "下班时间")
    private LocalTime endTime;

    @Schema(description = "中午是否有午休")
    private Integer middleRest;

    @Schema(description = "午休开始时间")
    private LocalTime middleStart;

    @Schema(description = "午休结束时间")
    private LocalTime middleEnd;

    @Schema(description = "是否有年假")
    private Integer vacation;

    @Schema(description = "是否有调休假")
    private Integer comp;

    @Schema(description = "精确度0.5或1")
    private BigDecimal accuracy;

    @Schema(description = "假删除 0删除 1保留")
    private Integer isDelete;
}
