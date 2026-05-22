package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.LeaderDao;
import com.hailang.entity.Leader;
import com.hailang.service.LeaderService;
import com.hailang.service.dto.LeaderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderServiceImpl implements LeaderService {

    private final LeaderDao leaderDao;

    @Override
    public List<LeaderDTO> list(String excludeUuid) {
        LambdaQueryWrapper<Leader> wrapper = new LambdaQueryWrapper<>();
        if (excludeUuid != null && !excludeUuid.isEmpty()) {
            wrapper.ne(Leader::getLeaderUuid, excludeUuid);
        }
        return leaderDao.selectList(wrapper).stream()
                .map(leader -> BeanUtils.copy(leader, LeaderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public LeaderDTO getByLeaderUuid(String leaderUuid) {
        Leader leader = leaderDao.selectByLeaderUuid(leaderUuid);
        return leader == null ? null : BeanUtils.copy(leader, LeaderDTO.class);
    }

    @Override
    public LeaderDTO save(LeaderDTO dto) {
        Leader leader = BeanUtils.copy(dto, Leader.class);
        leader.setLeaderUuid(UUID.randomUUID().toString().replace("-", ""));
        leaderDao.insert(leader);
        return BeanUtils.copy(leader, LeaderDTO.class);
    }

    @Override
    public LeaderDTO update(LeaderDTO dto) {
        Leader leader = BeanUtils.copy(dto, Leader.class);
        leaderDao.update(leader, Wrappers.<Leader>lambdaUpdate().eq(Leader::getLeaderUuid, leader.getLeaderUuid()));
        return BeanUtils.copy(leader, LeaderDTO.class);
    }

}
