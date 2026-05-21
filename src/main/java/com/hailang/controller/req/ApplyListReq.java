package com.hailang.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Schema(description = "个人考勤申请列表查询请求")
public class ApplyListReq {
    @Schema(description = "申请人uuid")
    private String userUuid;

    @Schema(description = "申请月份")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate month;
}
