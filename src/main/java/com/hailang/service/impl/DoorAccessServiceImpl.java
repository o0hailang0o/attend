package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.DoorAccessDao;
import com.hailang.entity.DoorAccess;
import com.hailang.service.DoorAccessService;
import com.hailang.service.dto.DoorAccessDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoorAccessServiceImpl implements DoorAccessService {

    private final DoorAccessDao doorAccessDao;

    @Override
    public List<DoorAccessDTO> queryByDateAndEmployee(String employeeUuid, LocalDate date) {
        LambdaQueryWrapper<DoorAccess> wrapper = Wrappers.<DoorAccess>lambdaQuery()
                .eq(DoorAccess::getIsDelete, 1);
        if (employeeUuid != null && !employeeUuid.isEmpty()) {
            wrapper.eq(DoorAccess::getEmployeeUuid, employeeUuid);
        }
        if (date != null) {
            wrapper.ge(DoorAccess::getAccessDatetime, date.atStartOfDay())
                    .lt(DoorAccess::getAccessDatetime, date.plusDays(1).atStartOfDay());
        }
        return doorAccessDao.selectList(wrapper).stream()
                .map(record -> BeanUtils.copy(record, DoorAccessDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public DoorAccessDTO getByUuid(String uuid) {
        DoorAccess record = doorAccessDao.selectByUuid(uuid);
        return record == null ? null : BeanUtils.copy(record, DoorAccessDTO.class);
    }

    @Override
    public DoorAccessDTO save(DoorAccessDTO dto) {
        DoorAccess record = BeanUtils.copy(dto, DoorAccess.class);
        record.setUuid(UUID.randomUUID().toString().replace("-", ""));
        record.setIsDelete(1);
        doorAccessDao.insert(record);
        return BeanUtils.copy(record, DoorAccessDTO.class);
    }

    @Override
    public DoorAccessDTO update(DoorAccessDTO dto) {
        DoorAccess record = BeanUtils.copy(dto, DoorAccess.class);
        doorAccessDao.update(record, new LambdaQueryWrapper<DoorAccess>().eq(DoorAccess::getUuid, record.getUuid()));
        return BeanUtils.copy(record, DoorAccessDTO.class);
    }

    @Override
    public boolean removeByUuid(String uuid) {
        return doorAccessDao.update(null,
                Wrappers.<DoorAccess>lambdaUpdate()
                        .eq(DoorAccess::getUuid, uuid)
                        .set(DoorAccess::getIsDelete, 0)) > 0;
    }
}
