package com.hailang.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hailang.entity.Dept;
import org.apache.ibatis.annotations.Select;

public interface DeptDao extends BaseMapper<Dept> {

    @Select("select * from dept where uuid = #{uuid} and is_delete = 1")
    Dept selectByUuid(String uuid);
}
