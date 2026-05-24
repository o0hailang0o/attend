package com.hailang.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hailang.entity.DoorAccess;
import org.apache.ibatis.annotations.Select;

public interface DoorAccessDao extends BaseMapper<DoorAccess> {

    @Select("select * from door_access where uuid = #{uuid} and is_delete = 1")
    DoorAccess selectByUuid(String uuid);
}
