package com.hailang.service;

import com.hailang.dao.RuleDao;
import com.hailang.service.dto.RuleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ApplyServiceTest {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleDao ruleDao;

    private String ruleWithLunchUuid;
    private String ruleNoLunchUuid;
    private String ruleAcc1Uuid;
    private String ruleNightUuid;

    @BeforeEach
    void setUp() {
        ruleDao.delete(null);

        RuleDTO r1 = new RuleDTO();
        r1.setName("标准规则");
        r1.setStartTime(LocalTime.of(9, 0));
        r1.setEndTime(LocalTime.of(18, 0));
        r1.setFlexibility(0);
        r1.setMiddleRest(1);
        r1.setMiddleStart(LocalTime.of(12, 0));
        r1.setMiddleEnd(LocalTime.of(13, 0));
        r1.setAccuracy(BigDecimal.valueOf(0.5));
        ruleWithLunchUuid = ruleService.save(r1).getUuid();

        RuleDTO r2 = new RuleDTO();
        r2.setName("无午休规则");
        r2.setStartTime(LocalTime.of(9, 0));
        r2.setEndTime(LocalTime.of(18, 0));
        r2.setFlexibility(0);
        r2.setMiddleRest(0);
        r2.setAccuracy(BigDecimal.valueOf(0.5));
        ruleNoLunchUuid = ruleService.save(r2).getUuid();

        RuleDTO r3 = new RuleDTO();
        r3.setName("取整规则");
        r3.setStartTime(LocalTime.of(9, 0));
        r3.setEndTime(LocalTime.of(18, 0));
        r3.setFlexibility(0);
        r3.setMiddleRest(1);
        r3.setMiddleStart(LocalTime.of(12, 0));
        r3.setMiddleEnd(LocalTime.of(13, 0));
        r3.setAccuracy(BigDecimal.valueOf(1));
        ruleAcc1Uuid = ruleService.save(r3).getUuid();

        RuleDTO r4 = new RuleDTO();
        r4.setName("夜班规则");
        r4.setStartTime(LocalTime.of(13, 0));
        r4.setEndTime(LocalTime.of(22, 0));
        r4.setFlexibility(0);
        r4.setMiddleRest(1);
        r4.setMiddleStart(LocalTime.of(18, 0));
        r4.setMiddleEnd(LocalTime.of(19, 0));
        r4.setAccuracy(BigDecimal.valueOf(0.5));
        ruleNightUuid = ruleService.save(r4).getUuid();
    }

    @Test
    void fullDayWithLunch() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 9, 0),
                LocalDateTime.of(2024, 6, 3, 18, 0),
                ruleWithLunchUuid);
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(result));
    }

    @Test
    void fullDayNoLunch() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 9, 0),
                LocalDateTime.of(2024, 6, 3, 18, 0),
                ruleNoLunchUuid);
        assertEquals(0, BigDecimal.valueOf(9.0).compareTo(result));
    }

    @Test
    void morningLeave() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 9, 0),
                LocalDateTime.of(2024, 6, 3, 12, 0),
                ruleWithLunchUuid);
        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(result));
    }

    @Test
    void afternoonLeave() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 13, 0),
                LocalDateTime.of(2024, 6, 3, 18, 0),
                ruleWithLunchUuid);
        assertEquals(0, BigDecimal.valueOf(5.0).compareTo(result));
    }

    @Test
    void crossLunch() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 11, 30),
                LocalDateTime.of(2024, 6, 3, 14, 30),
                ruleWithLunchUuid);
        assertEquals(0, BigDecimal.valueOf(2.0).compareTo(result));
    }

    @Test
    void startBeforeWorkSameDay() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 8, 0),
                LocalDateTime.of(2024, 6, 3, 12, 0),
                ruleWithLunchUuid);
        assertEquals(0, BigDecimal.valueOf(4.0).compareTo(result));
    }

    @Test
    void endAfterWorkSameDay() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 14, 0),
                LocalDateTime.of(2024, 6, 3, 20, 0),
                ruleWithLunchUuid);
        assertEquals(0, BigDecimal.valueOf(6.0).compareTo(result));
    }

    @Test
    void multiDays() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 9, 0),
                LocalDateTime.of(2024, 6, 5, 18, 0),
                ruleWithLunchUuid);
        assertEquals(0, BigDecimal.valueOf(24.0).compareTo(result));
    }

    @Test
    void accuracy0_5Floor() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 9, 0),
                LocalDateTime.of(2024, 6, 3, 17, 40),
                ruleWithLunchUuid);
        assertEquals(0, BigDecimal.valueOf(7.5).compareTo(result));
    }

    @Test
    void accuracy1Floor() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 9, 0),
                LocalDateTime.of(2024, 6, 3, 17, 50),
                ruleAcc1Uuid);
        assertEquals(0, BigDecimal.valueOf(7.0).compareTo(result));
    }

    @Test
    void ruleNotFound() {
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> applyService.calculateLength(
                        LocalDateTime.of(2024, 6, 3, 9, 0),
                        LocalDateTime.of(2024, 6, 3, 18, 0),
                        "nonexistent-uuid"));
        assertEquals("考勤规则不存在", e.getMessage());
    }

    @Test
    void sameDayLeaveOnLunchExact() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 12, 0),
                LocalDateTime.of(2024, 6, 3, 13, 0),
                ruleWithLunchUuid);
        assertEquals(0, BigDecimal.valueOf(0).compareTo(result));
    }

    @Test
    void multiDayPartialFirstLast() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 11, 0),
                LocalDateTime.of(2024, 6, 5, 15, 0),
                ruleWithLunchUuid);
        assertEquals(0, BigDecimal.valueOf(24.0).compareTo(result));
    }

    /* ───── 夜班 13:00-22:00 晚饭 18:00-19:00 ───── */

    @Test
    void nightFullDay() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 13, 0),
                LocalDateTime.of(2024, 6, 3, 22, 0),
                ruleNightUuid);
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(result));
    }

    @Test
    void nightAfternoon() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 13, 0),
                LocalDateTime.of(2024, 6, 3, 18, 0),
                ruleNightUuid);
        assertEquals(0, BigDecimal.valueOf(5.0).compareTo(result));
    }

    @Test
    void nightEvening() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 19, 0),
                LocalDateTime.of(2024, 6, 3, 22, 0),
                ruleNightUuid);
        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(result));
    }

    @Test
    void nightCrossDinner() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 17, 0),
                LocalDateTime.of(2024, 6, 3, 20, 0),
                ruleNightUuid);
        assertEquals(0, BigDecimal.valueOf(2.0).compareTo(result));
    }

    @Test
    void nightMultiDay() {
        BigDecimal result = applyService.calculateLength(
                LocalDateTime.of(2024, 6, 3, 13, 0),
                LocalDateTime.of(2024, 6, 5, 22, 0),
                ruleNightUuid);
        assertEquals(0, BigDecimal.valueOf(24.0).compareTo(result));
    }
}
