package com.hailang.controller;

import com.hailang.config.utils.BeanUtils;
import com.hailang.config.utils.Result;
import com.hailang.config.utils.ResultUtils;
import com.hailang.controller.req.DailyAttendanceReq;
import com.hailang.controller.resp.DailyAttendanceResp;
import com.hailang.service.DailyAttendanceService;
import com.hailang.service.dto.DailyAttendanceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "每日考勤统计")
@RestController
@RequestMapping("/dailyAttendance")
@RequiredArgsConstructor
public class DailyAttendanceController {

    private final DailyAttendanceService dailyAttendanceService;

    @Operation(summary = "根据日期和员工uuid查询每日考勤统计（支持日期范围）")
    @GetMapping
    public Result<List<DailyAttendanceResp>> query(
            @RequestParam(required = false) String employeeUuid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<DailyAttendanceDTO> dtoList;
        if (date != null) {
            dtoList = dailyAttendanceService.queryByDateAndEmployee(employeeUuid, date);
        } else {
            dtoList = dailyAttendanceService.queryByDateRange(employeeUuid, startDate, endDate);
        }
        List<DailyAttendanceResp> respList = dtoList.stream()
                .map(dto -> BeanUtils.copy(dto, DailyAttendanceResp.class))
                .collect(Collectors.toList());
        for (DailyAttendanceResp r : respList) {
            if (r.getDayType() != null) {
                switch (r.getDayType()) {
                    case 1: r.setDayTypeName("工作日"); break;
                    case 2: r.setDayTypeName("休息日"); break;
                    case 3: r.setDayTypeName("假日"); break;
                }
            }
            if (r.getStatus() != null) {
                switch (r.getStatus()) {
                    case 1: r.setStatusName("正常"); break;
                    case 2: r.setStatusName("迟到"); break;
                    case 3: r.setStatusName("早退"); break;
                    case 4: r.setStatusName("缺勤"); break;
                    case 5: r.setStatusName("补正"); break;
                }
            }
        }
        return ResultUtils.ok(respList);
    }

    @Operation(summary = "根据uuid查询每日考勤统计")
    @GetMapping("/{uuid}")
    public Result<DailyAttendanceResp> getByUuid(@PathVariable String uuid) {
        DailyAttendanceDTO dto = dailyAttendanceService.getByUuid(uuid);
        return ResultUtils.ok(BeanUtils.copy(dto, DailyAttendanceResp.class));
    }

    @Operation(summary = "新增每日考勤统计")
    @PostMapping
    public Result<DailyAttendanceResp> save(@RequestBody DailyAttendanceReq req) {
        DailyAttendanceDTO dto = BeanUtils.copy(req, DailyAttendanceDTO.class);
        DailyAttendanceDTO result = dailyAttendanceService.save(dto);
        return ResultUtils.ok(BeanUtils.copy(result, DailyAttendanceResp.class));
    }

    @Operation(summary = "修改每日考勤统计")
    @PutMapping
    public Result<DailyAttendanceResp> update(@RequestBody DailyAttendanceReq req) {
        DailyAttendanceDTO dto = BeanUtils.copy(req, DailyAttendanceDTO.class);
        DailyAttendanceDTO result = dailyAttendanceService.update(dto);
        return ResultUtils.ok(BeanUtils.copy(result, DailyAttendanceResp.class));
    }

    @Operation(summary = "根据uuid删除每日考勤统计")
    @DeleteMapping("/{uuid}")
    public Result<Boolean> removeByUuid(@PathVariable String uuid) {
        return ResultUtils.ok(dailyAttendanceService.removeByUuid(uuid));
    }

    @Operation(summary = "计算每日考勤统计（根据打卡记录和请假记录）")
    @PostMapping("/calculate")
    public Result<Void> calculate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String userUuid) {
        dailyAttendanceService.calculate(startDate, endDate, userUuid);
        return ResultUtils.ok(null);
    }
}
