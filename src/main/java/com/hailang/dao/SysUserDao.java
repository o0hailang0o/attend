package com.hailang.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hailang.entity.SysUser;
import org.apache.ibatis.annotations.Select;

public interface SysUserDao extends BaseMapper<SysUser> {

    @Select("select * from sys_user where uuid = #{uuid} and is_delete = 1")
    SysUser selectByUuid(String uuid);
}
