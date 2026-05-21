package com.hailang.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hailang.config.utils.BeanUtils;
import com.hailang.config.utils.Result;
import com.hailang.config.utils.ResultUtils;
import com.hailang.controller.req.ApplyCalcReq;
import com.hailang.controller.req.ApplyListReq;
import com.hailang.controller.req.ApplyReq;
import com.hailang.controller.resp.ApplyResp;
import com.hailang.service.ApplyService;
import com.hailang.service.dto.ApplyDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Tag(name = "考勤申请")
@RestController
@RequestMapping("/apply")
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyService applyService;

    @Operation(summary = "提交考勤申请")
    @PostMapping
    public Result<Void> submit(@RequestBody ApplyReq req) {
        ApplyDTO dto = BeanUtils.copy(req, ApplyDTO.class);
        dto.setLeaderId(req.getUserUuid());
        applyService.submit(dto);
        return ResultUtils.ok(null);
    }

    @Operation(summary = "查询申请详情")
    @GetMapping("/{uuid}")
    public Result<ApplyResp> getByUuid(@PathVariable String uuid) {
        ApplyDTO dto = applyService.getByUuid(uuid);
        return ResultUtils.ok(BeanUtils.copy(dto, ApplyResp.class));
    }

    @Operation(summary = "分页查询个人考勤申请列表")
    @GetMapping
    public Result<IPage<ApplyResp>> list(ApplyListReq req,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        IPage<ApplyDTO> pageResult = applyService.listByUser(req.getUserUuid(),
                req.getMonth() != null ? req.getMonth().atStartOfDay() : null, page, size);
        IPage<ApplyResp> respPage = pageResult.convert(item -> BeanUtils.copy(item, ApplyResp.class));
        return ResultUtils.ok(respPage);
    }

    @Operation(summary = "编辑申请")
    @PutMapping
    public Result<Void> update(@RequestBody ApplyReq req) {
        ApplyDTO dto = BeanUtils.copy(req, ApplyDTO.class);
        dto.setLeaderId(req.getUserUuid());
        applyService.update(dto);
        return ResultUtils.ok(null);
    }

    @Operation(summary = "删除申请")
    @DeleteMapping("/{uuid}")
    public Result<Void> remove(@PathVariable String uuid) {
        applyService.remove(uuid);
        return ResultUtils.ok(null);
    }

    @Operation(summary = "撤销申请")
    @PutMapping("/cancel/{uuid}")
    public Result<Void> cancel(@PathVariable String uuid) {
        applyService.cancel(uuid);
        return ResultUtils.ok(null);
    }

    @Operation(summary = "计算考勤时长")
    @PostMapping("/calc")
    public Result<BigDecimal> calculateLength(@RequestBody ApplyCalcReq req) {
        BigDecimal length = applyService.calculateLength(req.getStartTime(), req.getEndTime(), req.getRuleUuid());
        return ResultUtils.ok(length);
    }
}
