package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.ApplyDao;
import com.hailang.entity.Apply;
import com.hailang.service.ApplyService;
import com.hailang.service.dto.ApplyDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ApplyServiceImpl extends ServiceImpl<ApplyDao, Apply> implements ApplyService {

    @Override
    public void submit(ApplyDTO dto) {
        Apply entity = BeanUtils.copy(dto, Apply.class);
        entity.setUuid(UUID.randomUUID().toString().replace("-", ""));
        entity.setStatus(1);
        entity.setIsDelete(1);
        baseMapper.insert(entity);
    }

    @Override
    public IPage<ApplyDTO> listByUser(String userUuid, int page, int size) {
        Page<Apply> pageParam = new Page<>(page, size);
        Page<Apply> result = baseMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Apply>()
                        .eq(Apply::getLeaderId, userUuid)
                        .eq(Apply::getIsDelete, 1)
                        .orderByDesc(Apply::getCreateTime)
        );
        return result.convert(item -> BeanUtils.copy(item, ApplyDTO.class));
    }
}
