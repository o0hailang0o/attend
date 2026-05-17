package com.hailang.controller.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户详情")
public class SysUserResp {
    @Schema(description = "业务主键")
    private String uuid;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "账号")
    private String account;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "性别 1男 0女")
    private Integer gender;

    @Schema(description = "工号")
    private String workNum;

    @Schema(description = "级别")
    private String level;

    @Schema(description = "考勤类型")
    private String type;

    @Schema(description = "公司id")
    private String companyId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
