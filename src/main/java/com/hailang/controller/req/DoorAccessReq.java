package com.hailang.controller.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "门禁记录请求")
public class DoorAccessReq {
    @Schema(description = "uuid（修改时传）")
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
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime accessTime;

    @Schema(description = "通行日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate accessDate;
}
