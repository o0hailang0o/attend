package com.hailang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hailang.config.TestAdminConfig;
import com.hailang.dao.SysUserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SysUserControllerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;

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

        stringRedisTemplate.opsForValue().set(
                "sysUser_" + TestAdminConfig.ADMIN_UUID,
                TestAdminConfig.ADMIN_JSON,
                30, TimeUnit.MINUTES);
    }

    @Test
    void testLogin() throws Exception {
        String body = "{\"account\":\"admin\",\"password\":\"123456\"}";
        mockMvc.perform(post("/sysUser/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void testList() throws Exception {
        mockMvc.perform(get("/sysUser").with(TestAdminConfig.adminUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.total").isNumber());
    }

    @Test
    void testGetByUuid() throws Exception {
        mockMvc.perform(get("/sysUser/" + TestAdminConfig.ADMIN_UUID)
                        .with(TestAdminConfig.adminUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.uuid").value(TestAdminConfig.ADMIN_UUID));
    }
}
