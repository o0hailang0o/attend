package com.hailang.controller.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录响应")
public class LoginResp {
    @Schema(description = "令牌")
    private String token;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "账号")
    private String accout;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "业务主键")
    private String uuid;
}
