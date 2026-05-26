package com.hailang.controller;

import com.hailang.config.utils.BeanUtils;
import com.hailang.config.utils.Result;
import com.hailang.config.utils.ResultUtils;
import com.hailang.controller.resp.LeaveBalanceResp;
import com.hailang.service.LeaveBalanceService;
import com.hailang.service.dto.LeaveBalanceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "假期余额")
@RestController
@RequestMapping("/leaveBalance")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @Operation(summary = "查询当年假期余额（年假+调休假）")
    @GetMapping
    public Result<LeaveBalanceResp> get() {
        LeaveBalanceDTO dto = leaveBalanceService.getCurrent();
        if (dto == null) {
            return ResultUtils.ok(null);
        }
        return ResultUtils.ok(BeanUtils.copy(dto, LeaveBalanceResp.class));
    }

    @Operation(summary = "根据账号查询当年假期余额（内部接口）")
    @GetMapping("/byAccount")
    public Result<LeaveBalanceResp> getByAccount(@RequestParam String account) {
        LeaveBalanceDTO dto = leaveBalanceService.getByAccount(account);
        if (dto == null) {
            return ResultUtils.ok(null);
        }
        return ResultUtils.ok(BeanUtils.copy(dto, LeaveBalanceResp.class));
    }

    @Operation(summary = "根据用户uuid查询当年假期余额")
    @GetMapping("/byUser")
    public Result<LeaveBalanceResp> getByUserUuid(@RequestParam String userUuid) {
        LeaveBalanceDTO dto = leaveBalanceService.getByUserUuid(userUuid);
        if (dto == null) {
            return ResultUtils.ok(null);
        }
        return ResultUtils.ok(BeanUtils.copy(dto, LeaveBalanceResp.class));
    }
}
