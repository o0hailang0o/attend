package com.hailang.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hailang.entity.SysUser;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAdminConfig {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static final SysUser ADMIN = new SysUser();
    public static final String ADMIN_UUID = UUID.randomUUID().toString().replace("-", "");
    public static final String ADMIN_JSON;

    static {
        ADMIN.setUuid(ADMIN_UUID);
        ADMIN.setAccount("admin");
        ADMIN.setName("管理员");
        ADMIN.setPassword("e10adc3949ba59abbe56e057f20f883e");
        ADMIN.setGender(1);
        ADMIN.setIsDelete(1);
        try {
            ADMIN_JSON = MAPPER.writeValueAsString(ADMIN);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static RequestPostProcessor adminUser() {
        return request -> {
            request.setAttribute("sysUser", ADMIN);
            request.addHeader("Authorization", "Bearer " + ADMIN_UUID);
            return request;
        };
    }

    public static void mockRedis(StringRedisTemplate stringRedisTemplate) {
        ValueOperations<String, String> ops = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(ops);
        when(ops.get("sysUser_" + ADMIN_UUID)).thenReturn(ADMIN_JSON);
    }
}
