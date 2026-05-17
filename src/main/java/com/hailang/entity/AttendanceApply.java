package com.hailang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("attendance_apply")
@Schema(description = "考勤申请")
public class AttendanceApply {
    @TableId(type = IdType.AUTO)
    @Schema(description = "自增主键(内部)")
    private Long id;

    @Schema(description = "业务主键")
    private String uuid;

    @Schema(description = "申请人uuid")
    private String userUuid;

    @Schema(description = "申请类型 请假/加班/调休/外出")
    private String type;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "申请原因")
    private String reason;

    @Schema(description = "状态 0-待审批 1-已通过 2-已驳回")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
