package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.LeaveTypeDao;
import com.hailang.entity.LeaveType;
import com.hailang.service.LeaveTypeService;
import com.hailang.service.dto.LeaveTypeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeDao leaveTypeDao;

    @Override
    public List<LeaveTypeDTO> list() {
        return leaveTypeDao.selectList(
                Wrappers.<LeaveType>lambdaQuery()
                        .eq(LeaveType::getIsDelete, 1)
                        .orderByAsc(LeaveType::getSortOrder)
        ).stream()
                .map(lt -> BeanUtils.copy(lt, LeaveTypeDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public LeaveTypeDTO getByUuid(String uuid) {
        LeaveType entity = leaveTypeDao.selectByUuid(uuid);
        return entity == null ? null : BeanUtils.copy(entity, LeaveTypeDTO.class);
    }

    @Override
    public LeaveTypeDTO save(LeaveTypeDTO dto) {
        LeaveType entity = BeanUtils.copy(dto, LeaveType.class);
        entity.setUuid(UUID.randomUUID().toString().replace("-", ""));
        entity.setIsDelete(1);
        leaveTypeDao.insert(entity);
        return BeanUtils.copy(entity, LeaveTypeDTO.class);
    }

    @Override
    public LeaveTypeDTO update(LeaveTypeDTO dto) {
        LeaveType entity = BeanUtils.copy(dto, LeaveType.class);
        leaveTypeDao.update(entity,
                Wrappers.<LeaveType>lambdaUpdate()
                        .eq(LeaveType::getUuid, entity.getUuid()));
        return BeanUtils.copy(entity, LeaveTypeDTO.class);
    }

    @Override
    public boolean removeByUuid(String uuid) {
        return leaveTypeDao.update(null,
                Wrappers.<LeaveType>lambdaUpdate()
                        .eq(LeaveType::getUuid, uuid)
                        .set(LeaveType::getIsDelete, 0)) > 0;
    }
}
