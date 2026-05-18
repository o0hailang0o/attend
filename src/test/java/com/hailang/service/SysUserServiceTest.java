package com.hailang.service;

import com.hailang.config.TestAdminConfig;
import com.hailang.dao.SysUserDao;
import com.hailang.service.dto.LoginDTO;
import com.hailang.service.dto.LoginResultDTO;
import com.hailang.service.dto.SysUserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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

    @BeforeEach
    void setUp() {
        sysUserDao.delete(null);

        Set<String> redisKeys = stringRedisTemplate.keys("sysUser_*");
        if (redisKeys != null && !redisKeys.isEmpty()) {
            stringRedisTemplate.delete(redisKeys);
        }

        sysUserDao.insert(TestAdminConfig.ADMIN);
    }

    @Test
    void testLogin() {
        LoginDTO dto = new LoginDTO();
        dto.setAccount("admin");
        dto.setPassword("123456");
        LoginResultDTO result = sysUserService.login(dto);
        assertNotNull(result);
        assertNotNull(result.getToken());
    }

    @Test
    void testGetByUuid() {
        SysUserDTO dto = sysUserService.getByUuid(TestAdminConfig.ADMIN_UUID);
        assertNotNull(dto);
        assertEquals(TestAdminConfig.ADMIN_UUID, dto.getUuid());
    }
}
