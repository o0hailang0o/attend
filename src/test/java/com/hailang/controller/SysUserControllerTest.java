package com.hailang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hailang.dao.SysUserDao;
import com.hailang.entity.SysUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
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

    private RequestPostProcessor adminUser;
    private String adminUuid;

    @BeforeEach
    void setUp() throws Exception {
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

        String json = MAPPER.writeValueAsString(admin);
        stringRedisTemplate.opsForValue().set("sysUser_" + adminUuid, json, 30, TimeUnit.MINUTES);

        adminUser = request -> {
            request.setAttribute("sysUser", admin);
            request.addHeader("token", adminUuid);
            return request;
        };
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
        mockMvc.perform(get("/sysUser").with(adminUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetByUuid() throws Exception {
        mockMvc.perform(get("/sysUser/" + adminUuid).with(adminUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.uuid").value(adminUuid));
    }
}
