package com.hailang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hailang.config.TestAdminConfig;
import com.hailang.dao.RuleDao;
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

    @Autowired
    private RuleDao ruleDao;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void setUp() {
        TestAdminConfig.mockRedis(stringRedisTemplate);
        ruleDao.delete(null);
    }

    @Test
    void testSave() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "标准规则");
        body.put("startTime", "08:00");
        body.put("endTime", "17:00");
        body.put("flexibility", 2);
        body.put("middleRest", 1);
        body.put("middleStart", "12:00");
        body.put("middleEnd", "13:00");
        body.put("vacation", 1);
        body.put("comp", 1);
        body.put("accuracy", 0.5);

        mockMvc.perform(post("/rule")
                        .with(TestAdminConfig.adminUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("标准规则"));
    }

    @Test
    void testList() throws Exception {
        mockMvc.perform(get("/rule").with(TestAdminConfig.adminUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testSaveAndQuery() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "标准规则");
        body.put("startTime", "08:00");
        body.put("endTime", "17:00");
        body.put("flexibility", 2);
        body.put("middleRest", 1);
        body.put("middleStart", "12:00");
        body.put("middleEnd", "13:00");
        body.put("vacation", 1);
        body.put("comp", 1);
        body.put("accuracy", 0.5);

        mockMvc.perform(post("/rule")
                        .with(TestAdminConfig.adminUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/rule").with(TestAdminConfig.adminUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("标准规则"))
                .andExpect(jsonPath("$.data[0].startTime").value("08:00"))
                .andExpect(jsonPath("$.data[0].endTime").value("17:00"))
                .andExpect(jsonPath("$.data[0].flexibility").value(2))
                .andExpect(jsonPath("$.data[0].middleRest").value(1))
                .andExpect(jsonPath("$.data[0].middleStart").value("12:00"))
                .andExpect(jsonPath("$.data[0].middleEnd").value("13:00"))
                .andExpect(jsonPath("$.data[0].vacation").value(1))
                .andExpect(jsonPath("$.data[0].comp").value(1))
                .andExpect(jsonPath("$.data[0].accuracy").value(0.5));
    }
}
