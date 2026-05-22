package com.hailang.controller;

import com.hailang.config.AuthContext;
import com.hailang.config.utils.BeanUtils;
import com.hailang.config.utils.Result;
import com.hailang.config.utils.ResultUtils;
import com.hailang.controller.req.LeaderReq;
import com.hailang.controller.resp.LeaderResp;
import com.hailang.entity.SysUser;
import com.hailang.service.LeaderService;
import com.hailang.service.dto.LeaderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "领导")
@RestController
@RequestMapping("/leader")
@RequiredArgsConstructor
public class LeaderController {

    private final LeaderService leaderService;

    @Operation(summary = "查询所有领导（排除自己）")
    @GetMapping
    public Result<List<LeaderResp>> list() {
        SysUser currentUser = AuthContext.getCurrentUser();
        String excludeUuid = currentUser != null ? currentUser.getUuid() : null;
        List<LeaderDTO> dtoList = leaderService.list(excludeUuid);
        List<LeaderResp> respList = dtoList.stream()
                .map(dto -> BeanUtils.copy(dto, LeaderResp.class))
                .collect(Collectors.toList());
        return ResultUtils.ok(respList);
    }

    @Operation(summary = "根据leaderUuid查询领导")
    @GetMapping("/{leaderUuid}")
    public Result<LeaderResp> getByLeaderUuid(@PathVariable String leaderUuid) {
        LeaderDTO dto = leaderService.getByLeaderUuid(leaderUuid);
        return ResultUtils.ok(BeanUtils.copy(dto, LeaderResp.class));
    }

    @Operation(summary = "新增领导")
    @PostMapping
    public Result<LeaderResp> save(@RequestBody LeaderReq req) {
        LeaderDTO dto = BeanUtils.copy(req, LeaderDTO.class);
        LeaderDTO result = leaderService.save(dto);
        return ResultUtils.ok(BeanUtils.copy(result, LeaderResp.class));
    }

    @Operation(summary = "修改领导")
    @PutMapping
    public Result<LeaderResp> update(@RequestBody LeaderReq req) {
        LeaderDTO dto = BeanUtils.copy(req, LeaderDTO.class);
        LeaderDTO result = leaderService.update(dto);
        return ResultUtils.ok(BeanUtils.copy(result, LeaderResp.class));
    }

}
