package com.hailang.controller;

import com.hailang.config.utils.BeanUtils;
import com.hailang.config.utils.Result;
import com.hailang.config.utils.ResultUtils;
import com.hailang.controller.req.LeaveTypeReq;
import com.hailang.controller.resp.LeaveTypeResp;
import com.hailang.service.LeaveTypeService;
import com.hailang.service.dto.LeaveTypeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "假期类型")
@RestController
@RequestMapping("/leaveType")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    @Operation(summary = "查询所有假期类型")
    @GetMapping
    public Result<List<LeaveTypeResp>> list() {
        List<LeaveTypeDTO> dtoList = leaveTypeService.list();
        List<LeaveTypeResp> respList = dtoList.stream()
                .map(dto -> BeanUtils.copy(dto, LeaveTypeResp.class))
                .collect(Collectors.toList());
        return ResultUtils.ok(respList);
    }

    @Operation(summary = "根据uuid查询假期类型")
    @GetMapping("/{uuid}")
    public Result<LeaveTypeResp> getByUuid(@PathVariable String uuid) {
        LeaveTypeDTO dto = leaveTypeService.getByUuid(uuid);
        return ResultUtils.ok(BeanUtils.copy(dto, LeaveTypeResp.class));
    }

    @Operation(summary = "新增假期类型")
    @PostMapping
    public Result<LeaveTypeResp> save(@RequestBody LeaveTypeReq req) {
        LeaveTypeDTO dto = BeanUtils.copy(req, LeaveTypeDTO.class);
        LeaveTypeDTO result = leaveTypeService.save(dto);
        return ResultUtils.ok(BeanUtils.copy(result, LeaveTypeResp.class));
    }

    @Operation(summary = "修改假期类型")
    @PutMapping
    public Result<LeaveTypeResp> update(@RequestBody LeaveTypeReq req) {
        LeaveTypeDTO dto = BeanUtils.copy(req, LeaveTypeDTO.class);
        LeaveTypeDTO result = leaveTypeService.update(dto);
        return ResultUtils.ok(BeanUtils.copy(result, LeaveTypeResp.class));
    }

    @Operation(summary = "根据uuid删除假期类型")
    @DeleteMapping("/{uuid}")
    public Result<Boolean> removeByUuid(@PathVariable String uuid) {
        return ResultUtils.ok(leaveTypeService.removeByUuid(uuid));
    }
}
