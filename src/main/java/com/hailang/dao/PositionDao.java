package com.hailang.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hailang.entity.Position;
import org.apache.ibatis.annotations.Select;

public interface PositionDao extends BaseMapper<Position> {

    @Select("select * from position where uuid = #{uuid} and is_delete = 1")
    Position selectByUuid(String uuid);
}
