package com.hailang.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private LocalDate month;

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

    @Schema(description = "申请人uuid")
    private String applyUserUuid;

    @Schema(description = "审批人uuid")
    private String leaderUuid;

    @Schema(description = "驳回原因")
    private String reject;

    @Schema(description = "请假事由")
    private String reason;

    @Schema(description = "状态 1提交 4审批中 2通过 3撤销 9未通过")
    private Integer status;

    @Schema(description = "假删除 0删除 1保留")
    private Integer isDelete;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
