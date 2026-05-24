package com.hailang.service;

import com.hailang.dao.RuleDao;
import com.hailang.service.dto.RuleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RuleServiceTest {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleDao ruleDao;

    @BeforeEach
    void setUp() {
        ruleDao.delete(null);
    }

    private RuleDTO validRule() {
        RuleDTO dto = new RuleDTO();
        dto.setName("标准规则");
        dto.setStartTime(LocalTime.of(8, 0));
        dto.setEndTime(LocalTime.of(17, 0));
        dto.setFlexibility(2);
        dto.setMiddleRest(1);
        dto.setMiddleStart(LocalTime.of(12, 0));
        dto.setMiddleEnd(LocalTime.of(13, 0));
        dto.setVacation(1);
        dto.setComp(1);
        dto.setAccuracy(BigDecimal.valueOf(0.5));
        return dto;
    }

    @Test
    void saveSuccess() {
        assertDoesNotThrow(() -> ruleService.save(validRule()));
    }

    @Test
    void startTimeNull() {
        RuleDTO dto = validRule();
        dto.setStartTime(null);
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(dto));
        assertEquals("上班时间不能为空", e.getMessage());
    }

    @Test
    void endTimeNull() {
        RuleDTO dto = validRule();
        dto.setEndTime(null);
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(dto));
        assertEquals("下班时间不能为空", e.getMessage());
    }

    @Test
    void startTimeNotBeforeEndTime() {
        RuleDTO dto = validRule();
        dto.setStartTime(LocalTime.of(17, 0));
        dto.setEndTime(LocalTime.of(8, 0));
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(dto));
        assertEquals("上班时间必须小于下班时间", e.getMessage());
    }

    @Test
    void middleStartNull() {
        RuleDTO dto = validRule();
        dto.setMiddleStart(null);
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(dto));
        assertEquals("午休开始时间不能为空", e.getMessage());
    }

    @Test
    void middleEndNull() {
        RuleDTO dto = validRule();
        dto.setMiddleEnd(null);
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(dto));
        assertEquals("午休结束时间不能为空", e.getMessage());
    }

    @Test
    void startTimeNotBeforeMiddleStart() {
        RuleDTO dto = validRule();
        dto.setStartTime(LocalTime.of(12, 30));
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(dto));
        assertEquals("上班时间必须小于午休开始时间", e.getMessage());
    }

    @Test
    void middleStartNotBeforeMiddleEnd() {
        RuleDTO dto = validRule();
        dto.setMiddleStart(LocalTime.of(14, 0));
        dto.setMiddleEnd(LocalTime.of(13, 0));
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(dto));
        assertEquals("午休开始时间必须小于午休结束时间", e.getMessage());
    }

    @Test
    void middleEndNotBeforeEndTime() {
        RuleDTO dto = validRule();
        dto.setMiddleEnd(LocalTime.of(17, 30));
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(dto));
        assertEquals("午休结束时间必须小于下班时间", e.getMessage());
    }

    @Test
    void flexibilityNegative() {
        RuleDTO dto = validRule();
        dto.setFlexibility(-1);
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(dto));
        assertEquals("弹性时间不能为负数", e.getMessage());
    }

    @Test
    void accuracyInvalid() {
        RuleDTO dto = validRule();
        dto.setAccuracy(BigDecimal.valueOf(2));
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(dto));
        assertEquals("精确度只能为0.5或1", e.getMessage());
    }

    @Test
    void updateSuccess() {
        RuleDTO dto = ruleService.save(validRule());
        dto.setName("新名称");
        assertDoesNotThrow(() -> ruleService.update(dto));
    }

    @Test
    void updateInvalid() {
        RuleDTO dto = ruleService.save(validRule());
        dto.setEndTime(null);
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.update(dto));
        assertEquals("下班时间不能为空", e.getMessage());
    }
}
