package com.hailang.controller;

import com.hailang.config.utils.BeanUtils;
import com.hailang.config.utils.Result;
import com.hailang.config.utils.ResultUtils;
import com.hailang.controller.req.DeptReq;
import com.hailang.controller.resp.DeptResp;
import com.hailang.service.DeptService;
import com.hailang.service.dto.DeptDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "部门")
@RestController
@RequestMapping("/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @Operation(summary = "查询所有部门")
    @GetMapping
    public Result<List<DeptResp>> list() {
        List<DeptDTO> dtoList = deptService.list();
        List<DeptResp> respList = dtoList.stream()
                .map(dto -> BeanUtils.copy(dto, DeptResp.class))
                .collect(Collectors.toList());
        return ResultUtils.ok(respList);
    }

    @Operation(summary = "根据uuid查询部门")
    @GetMapping("/{uuid}")
    public Result<DeptResp> getByUuid(@PathVariable String uuid) {
        DeptDTO dto = deptService.getByUuid(uuid);
        return ResultUtils.ok(BeanUtils.copy(dto, DeptResp.class));
    }

    @Operation(summary = "新增部门")
    @PostMapping
    public Result<DeptResp> save(@RequestBody DeptReq req) {
        DeptDTO dto = BeanUtils.copy(req, DeptDTO.class);
        DeptDTO result = deptService.save(dto);
        return ResultUtils.ok(BeanUtils.copy(result, DeptResp.class));
    }

    @Operation(summary = "修改部门")
    @PutMapping
    public Result<DeptResp> update(@RequestBody DeptReq req) {
        DeptDTO dto = BeanUtils.copy(req, DeptDTO.class);
        DeptDTO result = deptService.update(dto);
        return ResultUtils.ok(BeanUtils.copy(result, DeptResp.class));
    }

    @Operation(summary = "根据uuid删除部门")
    @DeleteMapping("/{uuid}")
    public Result<Boolean> removeByUuid(@PathVariable String uuid) {
        return ResultUtils.ok(deptService.removeByUuid(uuid));
    }
}
