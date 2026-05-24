package com.hailang.controller;

import com.hailang.config.utils.BeanUtils;
import com.hailang.config.utils.Result;
import com.hailang.config.utils.ResultUtils;
import com.hailang.controller.req.DoorAccessReq;
import com.hailang.controller.resp.DoorAccessResp;
import com.hailang.service.DoorAccessService;
import com.hailang.service.dto.DoorAccessDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "门禁记录")
@RestController
@RequestMapping("/doorAccess")
@RequiredArgsConstructor
public class DoorAccessController {

    private final DoorAccessService doorAccessService;

    @Operation(summary = "根据日期和员工uuid查询门禁记录")
    @GetMapping
    public Result<List<DoorAccessResp>> query(
            @RequestParam(required = false) String employeeUuid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<DoorAccessDTO> dtoList = doorAccessService.queryByDateAndEmployee(employeeUuid, date);
        List<DoorAccessResp> respList = dtoList.stream()
                .map(dto -> BeanUtils.copy(dto, DoorAccessResp.class))
                .collect(Collectors.toList());
        return ResultUtils.ok(respList);
    }

    @Operation(summary = "根据uuid查询门禁记录")
    @GetMapping("/{uuid}")
    public Result<DoorAccessResp> getByUuid(@PathVariable String uuid) {
        DoorAccessDTO dto = doorAccessService.getByUuid(uuid);
        return ResultUtils.ok(BeanUtils.copy(dto, DoorAccessResp.class));
    }

    @Operation(summary = "新增门禁记录")
    @PostMapping
    public Result<DoorAccessResp> save(@RequestBody DoorAccessReq req) {
        DoorAccessDTO dto = BeanUtils.copy(req, DoorAccessDTO.class);
        DoorAccessDTO result = doorAccessService.save(dto);
        return ResultUtils.ok(BeanUtils.copy(result, DoorAccessResp.class));
    }

    @Operation(summary = "修改门禁记录")
    @PutMapping
    public Result<DoorAccessResp> update(@RequestBody DoorAccessReq req) {
        DoorAccessDTO dto = BeanUtils.copy(req, DoorAccessDTO.class);
        DoorAccessDTO result = doorAccessService.update(dto);
        return ResultUtils.ok(BeanUtils.copy(result, DoorAccessResp.class));
    }

    @Operation(summary = "根据uuid删除门禁记录")
    @DeleteMapping("/{uuid}")
    public Result<Boolean> removeByUuid(@PathVariable String uuid) {
        return ResultUtils.ok(doorAccessService.removeByUuid(uuid));
    }
}
