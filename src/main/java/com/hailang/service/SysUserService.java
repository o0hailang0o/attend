package com.hailang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hailang.entity.SysUser;
import com.hailang.service.dto.LoginDTO;
import com.hailang.service.dto.LoginResultDTO;

public interface SysUserService extends IService<SysUser> {
    LoginResultDTO login(LoginDTO dto);
}
