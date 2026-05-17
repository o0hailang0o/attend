package com.hailang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hailang.entity.SysUser;
import com.hailang.service.dto.LoginDTO;
import com.hailang.service.dto.LoginResultDTO;
import com.hailang.service.dto.SysUserDTO;

public interface SysUserService extends IService<SysUser> {
    LoginResultDTO login(LoginDTO dto);
    SysUserDTO getByUuid(String uuid);
    boolean removeByUuid(String uuid);
}
