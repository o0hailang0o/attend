package com.hailang.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hailang.entity.Leader;
import org.apache.ibatis.annotations.Select;

public interface LeaderDao extends BaseMapper<Leader> {

    @Select("select * from leader where leader_uuid = #{leaderUuid}")
    Leader selectByLeaderUuid(String leaderUuid);
}
