package com.hailang.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hailang.entity.DailyAttendance;
import org.apache.ibatis.annotations.Select;

public interface DailyAttendanceDao extends BaseMapper<DailyAttendance> {

    @Select("select * from daily_attendance where uuid = #{uuid} and is_delete = 1")
    DailyAttendance selectByUuid(String uuid);
}
