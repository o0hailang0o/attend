package com.hailang.service;

import com.hailang.service.dto.LeaveTypeDTO;

import java.util.List;

public interface LeaveTypeService {
    List<LeaveTypeDTO> list();

    LeaveTypeDTO getByUuid(String uuid);

    LeaveTypeDTO save(LeaveTypeDTO dto);

    LeaveTypeDTO update(LeaveTypeDTO dto);

    boolean removeByUuid(String uuid);
}
