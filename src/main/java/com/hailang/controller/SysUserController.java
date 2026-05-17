package com.hailang.controller;

import com.hailang.controller.req.LoginReq;
import com.hailang.controller.resp.LoginResp;
import com.hailang.entity.SysUser;
import com.hailang.service.SysUserService;
import com.hailang.service.dto.LoginDTO;
import com.hailang.service.dto.LoginResultDTO;
import com.hailang.config.utils.BeanUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "系统用户")
@RestController
@RequestMapping("/sysUser")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public LoginResp login(@RequestBody LoginReq req) {
        LoginDTO dto = BeanUtils.copy(req, LoginDTO.class);
        LoginResultDTO loginResultDTO = sysUserService.login(dto);
        return BeanUtils.copy(loginResultDTO, LoginResp.class);
    }

    @Operation(summary = "查询所有用户")
    @GetMapping
    public List<SysUser> list() {
        return sysUserService.list();
    }

    @Operation(summary = "根据业务主键查询用户")
    @GetMapping("/{uuid}")
    public SysUser getByUuid(@PathVariable String uuid) {
        return sysUserService.getByUuid(uuid);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public boolean save(@RequestBody SysUser sysUser) {
        return sysUserService.save(sysUser);
    }

    @Operation(summary = "修改用户")
    @PutMapping
    public boolean update(@RequestBody SysUser sysUser) {
        return sysUserService.updateById(sysUser);
    }

    @Operation(summary = "根据业务主键删除用户")
    @DeleteMapping("/{uuid}")
    public boolean removeByUuid(@PathVariable String uuid) {
        return sysUserService.removeByUuid(uuid);
    }
}
