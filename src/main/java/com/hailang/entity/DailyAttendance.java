package com.hailang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@TableName("daily_attendance")
@Schema(description = "每日考勤统计")
public class DailyAttendance {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "uuid")
    private String uuid;

    @Schema(description = "员工uuid")
    private String employeeUuid;

    @Schema(description = "员工姓名")
    private String employeeName;

    @Schema(description = "日期")
    private LocalDate date;

    @Schema(description = "上班时间")
    private LocalTime clockIn;

    @Schema(description = "下班时间")
    private LocalTime clockOut;

    @Schema(description = "实际工作小时")
    private java.math.BigDecimal actualWorkHours;

    @Schema(description = "认定工作小时")
    private java.math.BigDecimal recognizedHours;

    @Schema(description = "请假小时")
    private java.math.BigDecimal leaveHours;

    @Schema(description = "使用年假小时")
    private java.math.BigDecimal annualLeaveHours;

    @Schema(description = "使用调休假小时")
    private java.math.BigDecimal compLeaveHours;

    @Schema(description = "日类型 1工作日 2休息日 3假日")
    private Integer dayType;

    @Schema(description = "假删除 0删除 1保留")
    private Integer isDelete;
}
