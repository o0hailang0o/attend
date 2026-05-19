package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.DeptDao;
import com.hailang.entity.Dept;
import com.hailang.service.DeptService;
import com.hailang.service.dto.DeptDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final DeptDao deptDao;

    @Override
    public List<DeptDTO> list() {
        return deptDao.selectList(Wrappers.<Dept>lambdaQuery().eq(Dept::getIsDelete, 1)).stream()
                .map(dept -> BeanUtils.copy(dept, DeptDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public DeptDTO getByUuid(String uuid) {
        Dept dept = deptDao.selectByUuid(uuid);
        return dept == null ? null : BeanUtils.copy(dept, DeptDTO.class);
    }

    @Override
    public DeptDTO save(DeptDTO dto) {
        Dept dept = BeanUtils.copy(dto, Dept.class);
        dept.setUuid(UUID.randomUUID().toString().replace("-", ""));
        dept.setIsDelete(1);
        deptDao.insert(dept);
        return BeanUtils.copy(dept, DeptDTO.class);
    }

    @Override
    public DeptDTO update(DeptDTO dto) {
        Dept dept = BeanUtils.copy(dto, Dept.class);
        deptDao.update(dept, Wrappers.<Dept>lambdaUpdate().eq(Dept::getUuid, dept.getUuid()));
        return BeanUtils.copy(dept, DeptDTO.class);
    }

    @Override
    public boolean removeByUuid(String uuid) {
        return deptDao.update(null,
                Wrappers.<Dept>lambdaUpdate()
                        .eq(Dept::getUuid, uuid)
                        .set(Dept::getIsDelete, 0)) > 0;
    }
}
