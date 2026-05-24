package com.hailang.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("leave_balance")
@Schema(description = "假期余额")
public class LeaveBalance {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "业务uuid")
    private String uuid;

    @Schema(description = "用户uuid")
    private String userUuid;

    @Schema(description = "年度")
    private Integer year;

    @Schema(description = "年假剩余小时数")
    private BigDecimal annualRemainingHours;

    @Schema(description = "调休假剩余小时数")
    private BigDecimal compRemainingHours;

    @Schema(description = "假删除 0删除 1保留")
    private Integer isDelete;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
