package com.hailang.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hailang.config.utils.BeanUtils;
import com.hailang.config.utils.Result;
import com.hailang.config.utils.ResultUtils;
import com.hailang.controller.req.LoginReq;
import com.hailang.controller.req.SysUserBatchUpdateRuleReq;
import com.hailang.controller.req.SysUserQueryReq;
import com.hailang.controller.resp.LoginResp;
import com.hailang.controller.resp.SysUserResp;
import com.hailang.entity.SysUser;
import com.hailang.service.SysUserService;
import com.hailang.service.dto.LoginDTO;
import com.hailang.service.dto.LoginResultDTO;
import com.hailang.service.dto.SysUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "系统用户")
@RestController
@RequestMapping("/sysuser")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginResp> login(@RequestBody LoginReq req) {
        LoginDTO dto = BeanUtils.copy(req, LoginDTO.class);
        LoginResultDTO loginResultDTO = sysUserService.login(dto);
        return ResultUtils.ok(BeanUtils.copy(loginResultDTO, LoginResp.class));
    }

    @Operation(summary = "分页查询用户")
    @GetMapping
    public Result<IPage<SysUser>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            SysUserQueryReq req) {
        return ResultUtils.ok(sysUserService.list(page, size, req));
    }

    @Operation(summary = "根据业务主键查询用户详情")
    @GetMapping("/{uuid}")
    public Result<SysUserResp> getByUuid(@PathVariable String uuid) {
        SysUserDTO dto = sysUserService.getByUuid(uuid);
        return ResultUtils.ok(BeanUtils.copy(dto, SysUserResp.class));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Boolean> save(@RequestBody SysUser sysUser) {
        return ResultUtils.ok(sysUserService.save(sysUser));
    }

    @Operation(summary = "修改用户")
    @PutMapping
    public Result<Void> update(@RequestBody SysUser sysUser) {
        sysUserService.updateByUuid(sysUser);
        return ResultUtils.ok(null);
    }

    @Operation(summary = "根据业务主键删除用户")
    @DeleteMapping("/{uuid}")
    public Result<Boolean> removeByUuid(@PathVariable String uuid) {
        return ResultUtils.ok(sysUserService.removeByUuid(uuid));
    }

    @Operation(summary = "批量修改考勤规则")
    @PutMapping("/batch/rule")
    public Result<Void> batchUpdateRule(@RequestBody SysUserBatchUpdateRuleReq req) {
        sysUserService.batchUpdateRule(req.getUuids(), req.getRuleUuid(), req.getRuleName());
        return ResultUtils.ok(null);
    }
}
