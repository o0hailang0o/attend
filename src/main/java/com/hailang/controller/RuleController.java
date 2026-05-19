package com.hailang.controller;

import com.hailang.config.utils.BeanUtils;
import com.hailang.config.utils.Result;
import com.hailang.config.utils.ResultUtils;
import com.hailang.controller.req.RuleReq;
import com.hailang.controller.resp.RuleResp;
import com.hailang.service.RuleService;
import com.hailang.service.dto.RuleDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "规则")
@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    @Operation(summary = "查询所有规则")
    @GetMapping
    public Result<List<RuleResp>> list() {
        List<RuleDTO> dtoList = ruleService.list();
        List<RuleResp> respList = dtoList.stream()
                .map(dto -> BeanUtils.copy(dto, RuleResp.class))
                .collect(Collectors.toList());
        return ResultUtils.ok(respList);
    }

    @Operation(summary = "根据uuid查询规则")
    @GetMapping("/{uuid}")
    public Result<RuleResp> getByUuid(@PathVariable String uuid) {
        RuleDTO dto = ruleService.getByUuid(uuid);
        return ResultUtils.ok(BeanUtils.copy(dto, RuleResp.class));
    }

    @Operation(summary = "新增规则")
    @PostMapping
    public Result<RuleResp> save(@RequestBody RuleReq req) {
        RuleDTO dto = BeanUtils.copy(req, RuleDTO.class);
        RuleDTO result = ruleService.save(dto);
        return ResultUtils.ok(BeanUtils.copy(result, RuleResp.class));
    }

    @Operation(summary = "修改规则")
    @PutMapping
    public Result<RuleResp> update(@RequestBody RuleReq req) {
        RuleDTO dto = BeanUtils.copy(req, RuleDTO.class);
        RuleDTO result = ruleService.update(dto);
        return ResultUtils.ok(BeanUtils.copy(result, RuleResp.class));
    }

    @Operation(summary = "根据uuid删除规则")
    @DeleteMapping("/{uuid}")
    public Result<Boolean> removeByUuid(@PathVariable String uuid) {
        return ResultUtils.ok(ruleService.removeByUuid(uuid));
    }
}
