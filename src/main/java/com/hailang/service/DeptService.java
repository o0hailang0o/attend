package com.hailang.service;

import com.hailang.service.dto.DeptDTO;

import java.util.List;

public interface DeptService {
    List<DeptDTO> list();

    DeptDTO getByUuid(String uuid);

    DeptDTO save(DeptDTO dto);

    DeptDTO update(DeptDTO dto);

    boolean removeByUuid(String uuid);
}
