package com.hailang.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "批量修改考勤规则请求")
public class SysUserBatchUpdateRuleReq {
    @Schema(description = "用户uuid列表")
    private List<String> uuids;

    @Schema(description = "考勤规则uuid")
    private String ruleUuid;

    @Schema(description = "考勤规则名称")
    private String ruleName;
}
