package com.hailang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hailang.config.TestAdminConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RuleControllerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void setUp() {
        TestAdminConfig.mockRedis(stringRedisTemplate);
    }

    @Test
    void testSave() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("uuid", UUID.randomUUID().toString().replace("-", ""));
        body.put("name", "标准规则");
        body.put("startTime", "08:00:00");
        body.put("endTime", "17:00:00");
        body.put("flexibility", 2);
        body.put("middleRest", 1);
        body.put("middleStart", "12:00:00");
        body.put("middleEnd", "13:00:00");
        body.put("vacation", 1);
        body.put("comp", 1);
        body.put("accuracy", 0.5);

        mockMvc.perform(post("/rule")
                        .with(TestAdminConfig.adminUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testList() throws Exception {
        mockMvc.perform(get("/rule").with(TestAdminConfig.adminUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSaveAndQuery() throws Exception {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> body = new HashMap<>();
        body.put("uuid", uuid);
        body.put("name", "标准规则");
        body.put("startTime", "08:00:00");
        body.put("endTime", "17:00:00");
        body.put("flexibility", 2);
        body.put("middleRest", 1);
        body.put("middleStart", "12:00:00");
        body.put("middleEnd", "13:00:00");
        body.put("vacation", 1);
        body.put("comp", 1);
        body.put("accuracy", 0.5);

        mockMvc.perform(post("/rule")
                        .with(TestAdminConfig.adminUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(body)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rule").with(TestAdminConfig.adminUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("标准规则"))
                .andExpect(jsonPath("$[0].startTime").value("08:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("17:00:00"))
                .andExpect(jsonPath("$[0].flexibility").value(2))
                .andExpect(jsonPath("$[0].middleRest").value(1))
                .andExpect(jsonPath("$[0].middleStart").value("12:00:00"))
                .andExpect(jsonPath("$[0].middleEnd").value("13:00:00"))
                .andExpect(jsonPath("$[0].vacation").value(1))
                .andExpect(jsonPath("$[0].comp").value(1))
                .andExpect(jsonPath("$[0].accuracy").value(0.5));
    }
}
