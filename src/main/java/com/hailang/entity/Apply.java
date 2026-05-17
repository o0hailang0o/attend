package com.hailang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("apply")
@Schema(description = "考勤申请")
public class Apply {
    @TableId(type = IdType.AUTO)
    @Schema(description = "申请单主键id")
    private Long id;

    @Schema(description = "申请uuid")
    private String uuid;

    @Schema(description = "申请月份")
    private LocalDateTime month;

    @Schema(description = "申请类型")
    private Integer type;

    @Schema(description = "请假时间类型")
    private Integer lengthType;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "时长")
    private BigDecimal length;

    @Schema(description = "审批人uuid")
    private String leaderId;

    @Schema(description = "驳回原因")
    private String reject;

    @Schema(description = "状态 1提交 2驳回 3撤销 9未通过")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
