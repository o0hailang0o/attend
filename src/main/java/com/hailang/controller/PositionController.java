package com.hailang.controller;

import com.hailang.config.utils.BeanUtils;
import com.hailang.config.utils.Result;
import com.hailang.config.utils.ResultUtils;
import com.hailang.controller.req.PositionReq;
import com.hailang.controller.resp.PositionResp;
import com.hailang.service.PositionService;
import com.hailang.service.dto.PositionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "职位")
@RestController
@RequestMapping("/position")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @Operation(summary = "查询所有职位")
    @GetMapping
    public Result<List<PositionResp>> list() {
        List<PositionDTO> dtoList = positionService.list();
        List<PositionResp> respList = dtoList.stream()
                .map(dto -> BeanUtils.copy(dto, PositionResp.class))
                .collect(Collectors.toList());
        return ResultUtils.ok(respList);
    }

    @Operation(summary = "根据uuid查询职位")
    @GetMapping("/{uuid}")
    public Result<PositionResp> getByUuid(@PathVariable String uuid) {
        PositionDTO dto = positionService.getByUuid(uuid);
        return ResultUtils.ok(BeanUtils.copy(dto, PositionResp.class));
    }

    @Operation(summary = "新增职位")
    @PostMapping
    public Result<PositionResp> save(@RequestBody PositionReq req) {
        PositionDTO dto = BeanUtils.copy(req, PositionDTO.class);
        PositionDTO result = positionService.save(dto);
        return ResultUtils.ok(BeanUtils.copy(result, PositionResp.class));
    }

    @Operation(summary = "修改职位")
    @PutMapping
    public Result<PositionResp> update(@RequestBody PositionReq req) {
        PositionDTO dto = BeanUtils.copy(req, PositionDTO.class);
        PositionDTO result = positionService.update(dto);
        return ResultUtils.ok(BeanUtils.copy(result, PositionResp.class));
    }

    @Operation(summary = "根据uuid删除职位")
    @DeleteMapping("/{uuid}")
    public Result<Boolean> removeByUuid(@PathVariable String uuid) {
        return ResultUtils.ok(positionService.removeByUuid(uuid));
    }
}
