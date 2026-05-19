package com.hailang.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hailang.entity.Rule;
import org.apache.ibatis.annotations.Select;

public interface RuleDao extends BaseMapper<Rule> {

    @Select("select * from rule where uuid = #{uuid} and is_delete = 1")
    Rule selectByUuid(String uuid);
}
