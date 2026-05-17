package com.hailang.controller;

import com.hailang.config.utils.BeanUtils;
import com.hailang.config.utils.Result;
import com.hailang.config.utils.ResultUtils;
import com.hailang.controller.req.AttendanceApplyReq;
import com.hailang.controller.resp.AttendanceApplyResp;
import com.hailang.service.AttendanceApplyService;
import com.hailang.service.dto.AttendanceApplyDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "考勤申请")
@RestController
@RequestMapping("/attendanceApply")
@RequiredArgsConstructor
public class AttendanceApplyController {

    private final AttendanceApplyService attendanceApplyService;

    @Operation(summary = "提交考勤申请")
    @PostMapping
    public Result<AttendanceApplyResp> apply(@RequestBody AttendanceApplyReq req) {
        AttendanceApplyDTO dto = BeanUtils.copy(req, AttendanceApplyDTO.class);
        AttendanceApplyDTO result = attendanceApplyService.apply(dto);
        return ResultUtils.ok(BeanUtils.copy(result, AttendanceApplyResp.class));
    }

    @Operation(summary = "查询个人考勤申请列表")
    @GetMapping
    public Result<List<AttendanceApplyResp>> list(@RequestParam String userUuid) {
        List<AttendanceApplyDTO> list = attendanceApplyService.listByUser(userUuid);
        return ResultUtils.ok(BeanUtils.copyList(list, AttendanceApplyResp.class));
    }
}
