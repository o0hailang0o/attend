package com.hailang.controller.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "门禁记录响应")
public class DoorAccessResp {
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

    @Schema(description = "通行日期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accessDatetime;
}
