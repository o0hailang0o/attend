package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.PositionDao;
import com.hailang.entity.Position;
import com.hailang.service.PositionService;
import com.hailang.service.dto.PositionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionDao positionDao;

    @Override
    public List<PositionDTO> list() {
        return positionDao.selectList(Wrappers.<Position>lambdaQuery().eq(Position::getIsDelete, 1)).stream()
                .map(position -> BeanUtils.copy(position, PositionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PositionDTO getByUuid(String uuid) {
        Position position = positionDao.selectByUuid(uuid);
        return position == null ? null : BeanUtils.copy(position, PositionDTO.class);
    }

    @Override
    public PositionDTO save(PositionDTO dto) {
        Position position = BeanUtils.copy(dto, Position.class);
        position.setUuid(UUID.randomUUID().toString().replace("-", ""));
        position.setIsDelete(1);
        positionDao.insert(position);
        return BeanUtils.copy(position, PositionDTO.class);
    }

    @Override
    public PositionDTO update(PositionDTO dto) {
        Position position = BeanUtils.copy(dto, Position.class);
        positionDao.update(position, Wrappers.<Position>lambdaUpdate().eq(Position::getUuid, position.getUuid()));
        return BeanUtils.copy(position, PositionDTO.class);
    }

    @Override
    public boolean removeByUuid(String uuid) {
        return positionDao.update(null,
                Wrappers.<Position>lambdaUpdate()
                        .eq(Position::getUuid, uuid)
                        .set(Position::getIsDelete, 0)) > 0;
    }
}
