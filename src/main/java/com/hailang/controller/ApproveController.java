package com.hailang.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hailang.config.utils.BeanUtils;
import com.hailang.config.utils.Result;
import com.hailang.config.utils.ResultUtils;
import com.hailang.controller.req.ApproveReq;
import com.hailang.controller.resp.ApproveResp;
import com.hailang.service.ApproveService;
import com.hailang.service.dto.ApproveDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "审批")
@RestController
@RequestMapping("/approve")
@RequiredArgsConstructor
public class ApproveController {

    private final ApproveService approveService;

    @Operation(summary = "分页查询待审批列表")
    @GetMapping
    public Result<IPage<ApproveResp>> list(@RequestParam String leaderId,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        IPage<ApproveDTO> pageResult = approveService.listByApprover(leaderId, page, size);
        IPage<ApproveResp> respPage = pageResult.convert(item -> BeanUtils.copy(item, ApproveResp.class));
        return ResultUtils.ok(respPage);
    }

    @Operation(summary = "审批通过")
    @PutMapping("/pass")
    public Result<Void> pass(@RequestBody ApproveReq req) {
        approveService.pass(req.getUuid());
        return ResultUtils.ok(null);
    }

    @Operation(summary = "驳回申请")
    @PutMapping("/reject")
    public Result<Void> reject(@RequestBody ApproveReq req) {
        approveService.reject(req.getUuid(), req.getReject());
        return ResultUtils.ok(null);
    }
}
