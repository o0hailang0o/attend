package com.hailang.service;

import com.hailang.dao.SysUserDao;
import com.hailang.entity.SysUser;
import com.hailang.service.dto.LoginDTO;
import com.hailang.service.dto.LoginResultDTO;
import com.hailang.service.dto.SysUserDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SysUserServiceTest {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String adminUuid;

    @BeforeEach
    void setUp() {
        sysUserDao.delete(null);

        Set<String> redisKeys = stringRedisTemplate.keys("sysUser_*");
        if (redisKeys != null && !redisKeys.isEmpty()) {
            stringRedisTemplate.delete(redisKeys);
        }

        SysUser admin = new SysUser();
        adminUuid = UUID.randomUUID().toString().replace("-", "");
        admin.setUuid(adminUuid);
        admin.setAccout("admin");
        admin.setName("管理员");
        admin.setPassword(DigestUtils.md5Hex("123456"));
        admin.setGender(1);
        sysUserDao.insert(admin);
    }

    @Test
    void testLogin() {
        LoginDTO dto = new LoginDTO();
        dto.setAccout("admin");
        dto.setPassword("123456");
        LoginResultDTO result = sysUserService.login(dto);
        assertNotNull(result);
        assertNotNull(result.getToken());
    }

    @Test
    void testGetByUuid() {
        SysUserDTO dto = sysUserService.getByUuid(adminUuid);
        assertNotNull(dto);
        assertEquals(adminUuid, dto.getUuid());
    }
}
