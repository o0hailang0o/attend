package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.SysUserDao;
import com.hailang.entity.SysUser;
import com.hailang.service.SysUserService;
import com.hailang.service.dto.LoginDTO;
import com.hailang.service.dto.LoginResultDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final StringRedisTemplate stringRedisTemplate;

    public SysUserServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

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
        String uuid = UUID.randomUUID().toString().replace("-", "");
        user.setUuid(uuid);
        user.setPassword(null);
        try {
            String json = MAPPER.writeValueAsString(user);
            stringRedisTemplate.opsForValue().set("token_" + uuid, json, 30, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败", e);
        }

        LoginResultDTO result = BeanUtils.copy(user, LoginResultDTO.class);
        result.setToken(uuid);
        result.setUserId(user.getId());
        return result;
    }
}
