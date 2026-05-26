package com.hailang.service;

import com.hailang.service.dto.DailyAttendanceDTO;

import java.time.LocalDate;
import java.util.List;

public interface DailyAttendanceService {
    List<DailyAttendanceDTO> queryByDateAndEmployee(String employeeUuid, LocalDate date);
    List<DailyAttendanceDTO> queryByDateRange(String employeeUuid, LocalDate startDate, LocalDate endDate);
    DailyAttendanceDTO getByUuid(String uuid);
    DailyAttendanceDTO save(DailyAttendanceDTO dto);
    DailyAttendanceDTO update(DailyAttendanceDTO dto);
    boolean removeByUuid(String uuid);
    void calculate(LocalDate startDate, LocalDate endDate, String userUuid);
}
