package com.hailang.service;

import com.hailang.service.dto.LeaderDTO;

import java.util.List;

public interface LeaderService {
    List<LeaderDTO> list(String excludeUuid);

    LeaderDTO getByLeaderUuid(String leaderUuid);

    LeaderDTO save(LeaderDTO dto);

    LeaderDTO update(LeaderDTO dto);
}
