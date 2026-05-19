package com.hailang.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户列表查询请求")
public class SysUserQueryReq {
    @Schema(description = "姓名")
    private String name;

    @Schema(description = "级别")
    private String level;

    @Schema(description = "职称")
    private String position;

    @Schema(description = "公司id")
    private String companyId;

    @Schema(description = "部门uuid")
    private String deptUuid;

    @Schema(description = "职位uuid")
    private String positionUuid;

    @Schema(description = "考勤规则uuid")
    private String ruleUuid;
}
