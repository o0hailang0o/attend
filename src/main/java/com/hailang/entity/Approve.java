package com.hailang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("approve")
@Schema(description = "审批")
public class Approve {
    @TableId(type = IdType.AUTO)
    @Schema(description = "审批主键id")
    private Long id;

    @Schema(description = "审批uuid")
    private String uuid;

    @Schema(description = "申请uuid")
    private String applyUuid;

    @Schema(description = "审批顺序")
    private Integer order;

    @Schema(description = "下一审批人")
    private String leaderId;

    @Schema(description = "驳回原因")
    private String reject;

    @Schema(description = "状态 9通过 3驳回 2未通过 1通过 0删除")
    private Integer status;

    @Schema(description = "假删除 0删除 1保留")
    private Integer isDelete;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
