package com.hailang.service;

import com.hailang.service.dto.RuleDTO;

import java.util.List;

public interface RuleService {
    List<RuleDTO> list();

    RuleDTO getByUuid(String uuid);

    RuleDTO save(RuleDTO dto);

    RuleDTO update(RuleDTO dto);

    boolean removeByUuid(String uuid);
}
