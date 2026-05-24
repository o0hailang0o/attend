package com.hailang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@TableName("door_access")
@Schema(description = "门禁记录")
public class DoorAccess {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "uuid")
    private String uuid;

    @Schema(description = "员工uuid")
    private String employeeUuid;

    @Schema(description = "员工姓名")
    private String employeeName;

    @Schema(description = "员工工号")
    private String workNum;

    @Schema(description = "门号")
    private String doorNo;

    @Schema(description = "进出 0进 1出")
    private Integer direction;

    @Schema(description = "通行时间")
    private LocalTime accessTime;

    @Schema(description = "通行日期")
    private LocalDate accessDate;

    @Schema(description = "假删除 0删除 1保留")
    private Integer isDelete;
}
