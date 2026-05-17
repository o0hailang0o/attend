package com.hailang.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录请求")
public class LoginReq {
    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;
}
