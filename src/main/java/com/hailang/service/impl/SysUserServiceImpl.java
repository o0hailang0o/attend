package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hailang.config.utils.BeanUtils;
import com.hailang.controller.req.SysUserQueryReq;
import com.hailang.dao.SysUserDao;
import com.hailang.entity.SysUser;
import com.hailang.service.SysUserService;
import com.hailang.service.dto.LoginDTO;
import com.hailang.service.dto.LoginResultDTO;
import com.hailang.service.dto.SysUserDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
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
        entity.setIsDelete(1);
        return super.save(entity);
    }

    @Override
    public IPage<SysUser> list(int page, int size, SysUserQueryReq req) {
        return baseMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getIsDelete, 1)
                        .eq(req.getDeptUuid() != null && !req.getDeptUuid().isEmpty(), SysUser::getDeptUuid, req.getDeptUuid())
                        .eq(req.getPositionUuid() != null && !req.getPositionUuid().isEmpty(), SysUser::getPositionUuid, req.getPositionUuid())
                        .eq(req.getRuleUuid() != null && !req.getRuleUuid().isEmpty(), SysUser::getRuleUuid, req.getRuleUuid())
                        .like(req.getName() != null && !req.getName().isEmpty(), SysUser::getName, req.getName())
                        .like(req.getLevel() != null && !req.getLevel().isEmpty(), SysUser::getLevel, req.getLevel())
                        .like(req.getPosition() != null && !req.getPosition().isEmpty(), SysUser::getPosition, req.getPosition())
                        .like(req.getCompanyId() != null && !req.getCompanyId().isEmpty(), SysUser::getCompanyId, req.getCompanyId())
        );
    }

    @Override
    public LoginResultDTO login(LoginDTO dto) {
        SysUser user = baseMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getAccount, dto.getAccount())
                        .eq(SysUser::getIsDelete, 1)
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
    public void updateByUuid(SysUser entity) {
        baseMapper.update(entity, Wrappers.<SysUser>lambdaUpdate().eq(SysUser::getUuid, entity.getUuid()));
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
        return baseMapper.update(null,
                Wrappers.<SysUser>lambdaUpdate()
                        .eq(SysUser::getUuid, uuid)
                        .set(SysUser::getIsDelete, 0)) > 0;
    }

    @Override
    public void batchUpdateRule(List<String> uuids, String ruleUuid, String ruleName) {
        if (uuids == null || uuids.isEmpty()) {
            throw new RuntimeException("用户列表不能为空");
        }
        if (ruleUuid == null || ruleUuid.isEmpty()) {
            throw new RuntimeException("考勤规则不能为空");
        }
        baseMapper.update(null,
                Wrappers.<SysUser>lambdaUpdate()
                        .in(SysUser::getUuid, uuids)
                        .set(SysUser::getRuleUuid, ruleUuid)
                        .set(SysUser::getRuleName, ruleName));
    }
}
