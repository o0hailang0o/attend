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
import com.hailang.service.dto.SysUserDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {

    private final ObjectMapper objectMapper;

    private final StringRedisTemplate stringRedisTemplate;

    public SysUserServiceImpl(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean save(SysUser entity) {
        if (entity.getUuid() == null) {
            entity.setUuid(UUID.randomUUID().toString().replace("-", ""));
        }
        return super.save(entity);
    }

    @Override
    public LoginResultDTO login(LoginDTO dto) {
        SysUser user = baseMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getAccout, dto.getAccout())
        );
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String md5Password = DigestUtils.md5Hex(dto.getPassword());
        if (!Objects.equals(user.getPassword(), md5Password)) {
            throw new RuntimeException("密码错误");
        }
        user.setPassword(null);
        try {
            String json = objectMapper.writeValueAsString(user);
            stringRedisTemplate.opsForValue().set("sysUser_" + user.getUuid(), json, 30, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败", e);
        }

        LoginResultDTO result = BeanUtils.copy(user, LoginResultDTO.class);
        result.setToken(user.getUuid());
        result.setUserId(user.getId());
        return result;
    }

    @Override
    public SysUserDTO getByUuid(String uuid) {
        SysUser user = baseMapper.selectByUuid(uuid);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return BeanUtils.copy(user, SysUserDTO.class);
    }

    @Override
    public boolean removeByUuid(String uuid) {
        SysUser user = baseMapper.selectByUuid(uuid);
        if (user == null) {
            return false;
        }
        return removeById(user.getId());
    }
}
