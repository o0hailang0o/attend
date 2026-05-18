package com.hailang.service;

import com.hailang.entity.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RuleServiceTest {

    @Autowired
    private RuleService ruleService;

    private Rule validRule() {
        Rule r = new Rule();
        r.setUuid(UUID.randomUUID().toString().replace("-", ""));
        r.setName("标准规则");
        r.setStartTime(LocalTime.of(8, 0));
        r.setEndTime(LocalTime.of(17, 0));
        r.setFlexibility(2);
        r.setMiddleRest(1);
        r.setMiddleStart(LocalTime.of(12, 0));
        r.setMiddleEnd(LocalTime.of(13, 0));
        r.setVacation(1);
        r.setComp(1);
        r.setAccuracy(BigDecimal.valueOf(0.5));
        return r;
    }

    @Test
    void saveSuccess() {
        assertDoesNotThrow(() -> ruleService.save(validRule()));
    }

    @Test
    void startTimeNull() {
        Rule r = validRule();
        r.setStartTime(null);
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(r));
        assertEquals("上班时间不能为空", e.getMessage());
    }

    @Test
    void endTimeNull() {
        Rule r = validRule();
        r.setEndTime(null);
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(r));
        assertEquals("下班时间不能为空", e.getMessage());
    }

    @Test
    void startTimeNotBeforeEndTime() {
        Rule r = validRule();
        r.setStartTime(LocalTime.of(17, 0));
        r.setEndTime(LocalTime.of(8, 0));
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(r));
        assertEquals("上班时间必须小于下班时间", e.getMessage());
    }

    @Test
    void middleStartNull() {
        Rule r = validRule();
        r.setMiddleStart(null);
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(r));
        assertEquals("午休开始时间不能为空", e.getMessage());
    }

    @Test
    void middleEndNull() {
        Rule r = validRule();
        r.setMiddleEnd(null);
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(r));
        assertEquals("午休结束时间不能为空", e.getMessage());
    }

    @Test
    void startTimeNotBeforeMiddleStart() {
        Rule r = validRule();
        r.setStartTime(LocalTime.of(12, 30));
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(r));
        assertEquals("上班时间必须小于午休开始时间", e.getMessage());
    }

    @Test
    void middleStartNotBeforeMiddleEnd() {
        Rule r = validRule();
        r.setMiddleStart(LocalTime.of(14, 0));
        r.setMiddleEnd(LocalTime.of(13, 0));
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(r));
        assertEquals("午休开始时间必须小于午休结束时间", e.getMessage());
    }

    @Test
    void middleEndNotBeforeEndTime() {
        Rule r = validRule();
        r.setMiddleEnd(LocalTime.of(17, 30));
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(r));
        assertEquals("午休结束时间必须小于下班时间", e.getMessage());
    }

    @Test
    void flexibilityNegative() {
        Rule r = validRule();
        r.setFlexibility(-1);
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(r));
        assertEquals("弹性时间不能为负数", e.getMessage());
    }

    @Test
    void accuracyInvalid() {
        Rule r = validRule();
        r.setAccuracy(BigDecimal.valueOf(2));
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.save(r));
        assertEquals("精确度只能为0.5或1", e.getMessage());
    }

    @Test
    void updateByIdSuccess() {
        Rule r = validRule();
        ruleService.save(r);
        r.setName("新名称");
        assertDoesNotThrow(() -> ruleService.updateById(r));
    }

    @Test
    void updateByIdInvalid() {
        Rule r = validRule();
        ruleService.save(r);
        r.setEndTime(null);
        RuntimeException e = assertThrows(RuntimeException.class, () -> ruleService.updateById(r));
        assertEquals("下班时间不能为空", e.getMessage());
    }
}
