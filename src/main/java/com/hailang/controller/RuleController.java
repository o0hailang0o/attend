package com.hailang.controller;

import com.hailang.entity.Rule;
import com.hailang.service.RuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "规则")
@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    @Operation(summary = "查询所有规则")
    @GetMapping
    public List<Rule> list() {
        return ruleService.list();
    }

    @Operation(summary = "根据ID查询规则")
    @GetMapping("/{id}")
    public Rule getById(@PathVariable Long id) {
        return ruleService.getById(id);
    }

    @Operation(summary = "新增规则")
    @PostMapping
    public boolean save(@RequestBody Rule rule) {
        return ruleService.save(rule);
    }

    @Operation(summary = "修改规则")
    @PutMapping
    public boolean update(@RequestBody Rule rule) {
        return ruleService.updateById(rule);
    }

    @Operation(summary = "删除规则")
    @DeleteMapping("/{id}")
    public boolean remove(@PathVariable Long id) {
        return ruleService.removeById(id);
    }
}
