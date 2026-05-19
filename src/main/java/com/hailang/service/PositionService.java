package com.hailang.service;

import com.hailang.service.dto.PositionDTO;

import java.util.List;

public interface PositionService {
    List<PositionDTO> list();

    PositionDTO getByUuid(String uuid);

    PositionDTO save(PositionDTO dto);

    PositionDTO update(PositionDTO dto);

    boolean removeByUuid(String uuid);
}
