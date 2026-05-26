package com.hailang.controller.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "每日考勤统计响应")
public class DailyAttendanceResp {
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "uuid")
    private String uuid;

    @Schema(description = "员工uuid")
    private String employeeUuid;

    @Schema(description = "员工姓名")
    private String employeeName;

    @Schema(description = "日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Schema(description = "上班时间")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime clockIn;

    @Schema(description = "下班时间")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime clockOut;

    @Schema(description = "实际工作小时")
    private BigDecimal actualWorkHours;

    @Schema(description = "认定工作小时")
    private BigDecimal recognizedHours;

    @Schema(description = "请假小时")
    private BigDecimal leaveHours;

    @Schema(description = "使用年假小时")
    private BigDecimal annualLeaveHours;

    @Schema(description = "使用调休假小时")
    private BigDecimal compLeaveHours;

    @Schema(description = "日类型 1工作日 2休息日 3假日")
    private Integer dayType;

    @Schema(description = "日类型名称")
    private String dayTypeName;

    @Schema(description = "考勤状态 1正常 2迟到 3早退 4缺勤 5补正")
    private Integer status;

    @Schema(description = "考勤状态名称")
    private String statusName;
}
