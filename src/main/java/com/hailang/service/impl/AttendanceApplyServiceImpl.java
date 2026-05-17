package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.AttendanceApplyDao;
import com.hailang.entity.AttendanceApply;
import com.hailang.service.AttendanceApplyService;
import com.hailang.service.dto.AttendanceApplyDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AttendanceApplyServiceImpl extends ServiceImpl<AttendanceApplyDao, AttendanceApply> implements AttendanceApplyService {

    @Override
    public AttendanceApplyDTO apply(AttendanceApplyDTO dto) {
        AttendanceApply entity = BeanUtils.copy(dto, AttendanceApply.class);
        entity.setUuid(UUID.randomUUID().toString().replace("-", ""));
        entity.setStatus(0);
        baseMapper.insert(entity);
        return BeanUtils.copy(entity, AttendanceApplyDTO.class);
    }

    @Override
    public List<AttendanceApplyDTO> listByUser(String userUuid) {
        List<AttendanceApply> list = baseMapper.selectList(
                new LambdaQueryWrapper<AttendanceApply>()
                        .eq(AttendanceApply::getUserUuid, userUuid)
                        .orderByDesc(AttendanceApply::getCreateTime)
        );
        return BeanUtils.copyList(list, AttendanceApplyDTO.class);
    }
}
