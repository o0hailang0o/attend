package com.hailang.service;

import com.hailang.service.dto.DoorAccessDTO;

import java.time.LocalDate;
import java.util.List;

public interface DoorAccessService {
    List<DoorAccessDTO> queryByDateAndEmployee(String employeeUuid, LocalDate date);

    DoorAccessDTO getByUuid(String uuid);

    DoorAccessDTO save(DoorAccessDTO dto);

    DoorAccessDTO update(DoorAccessDTO dto);

    boolean removeByUuid(String uuid);
}
