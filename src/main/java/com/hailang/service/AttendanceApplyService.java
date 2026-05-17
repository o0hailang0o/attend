package com.hailang.service;

import com.hailang.service.dto.AttendanceApplyDTO;

import java.util.List;

public interface AttendanceApplyService {
    AttendanceApplyDTO apply(AttendanceApplyDTO dto);
    List<AttendanceApplyDTO> listByUser(String userUuid);
}
