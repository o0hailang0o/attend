package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hailang.dao.SysUserDao;
import com.hailang.entity.SysUser;
import com.hailang.service.SysUserService;
import com.hailang.service.dto.LoginDTO;
import com.hailang.service.dto.LoginResultDTO;
import com.hailang.config.utils.BeanUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {

    @Override
    public LoginResultDTO login(LoginDTO dto) {
        SysUser user = baseMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String md5Password = DigestUtils.md5Hex(dto.getPassword());
        if (!Objects.equals(user.getPassword(), md5Password)) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        user.setToken(token);
        baseMapper.updateById(user);

        LoginResultDTO result = BeanUtils.copy(user, LoginResultDTO.class);
        result.setUserId(user.getId());
        return result;
    }
}
