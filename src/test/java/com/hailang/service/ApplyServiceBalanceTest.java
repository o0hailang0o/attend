package com.hailang.service;

import com.hailang.dao.ApplyDao;
import com.hailang.dao.ApproveDao;
import com.hailang.dao.LeaderDao;
import com.hailang.dao.LeaveBalanceDao;
import com.hailang.dao.RuleDao;
import com.hailang.entity.Apply;
import com.hailang.entity.Leader;
import com.hailang.entity.LeaveBalance;
import com.hailang.entity.Rule;
import com.hailang.entity.SysUser;
import com.hailang.service.dto.ApplyDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ApplyServiceBalanceTest {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private LeaveBalanceDao leaveBalanceDao;

    @Autowired
    private LeaderDao leaderDao;

    @Autowired
    private ApplyDao applyDao;

    @Autowired
    private ApproveDao approveDao;

    @Autowired
    private RuleDao ruleDao;

    private String ruleUuid;
    private String testUserUuid;
    private String leaderUuid;

    @BeforeEach
    void setUp() {
        leaveBalanceDao.delete(null);
        leaderDao.delete(null);
        applyDao.delete(null);
        approveDao.delete(null);
        ruleDao.delete(null);

        ruleUuid = UUID.randomUUID().toString().replace("-", "");
        Rule rule = new Rule();
        rule.setUuid(ruleUuid);
        rule.setName("标准规则");
        rule.setStartTime(LocalTime.of(9, 0));
        rule.setEndTime(LocalTime.of(18, 0));
        rule.setFlexibility(0);
        rule.setMiddleRest(1);
        rule.setMiddleStart(LocalTime.of(12, 0));
        rule.setMiddleEnd(LocalTime.of(13, 0));
        rule.setVacation(1);
        rule.setComp(1);
        rule.setAccuracy(BigDecimal.valueOf(0.5));
        rule.setIsDelete(1);
        ruleDao.insert(rule);

        testUserUuid = UUID.randomUUID().toString().replace("-", "");
        leaderUuid = UUID.randomUUID().toString().replace("-", "");

        SysUser testUser = new SysUser();
        testUser.setUuid(testUserUuid);
        testUser.setRuleUuid(ruleUuid);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("sysUser", testUser);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        LeaveBalance bal = new LeaveBalance();
        bal.setUuid(UUID.randomUUID().toString().replace("-", ""));
        bal.setUserUuid(testUserUuid);
        bal.setYear(LocalDate.now().getYear());
        bal.setAnnualRemainingHours(BigDecimal.valueOf(8));
        bal.setCompRemainingHours(BigDecimal.valueOf(4));
        bal.setIsDelete(1);
        leaveBalanceDao.insert(bal);

        Leader leader = new Leader();
        leader.setId(System.currentTimeMillis());
        leader.setLeaderUuid(leaderUuid);
        leader.setLeaderName("测试审批人");
        leaderDao.insert(leader);
    }

    private ApplyDTO makeDTO(int type, LocalDateTime start, LocalDateTime end) {
        ApplyDTO dto = new ApplyDTO();
        dto.setType(type);
        dto.setMonth(start.toLocalDate().withDayOfMonth(1));
        dto.setStartTime(start);
        dto.setEndTime(end);
        dto.setLeaderUuid(leaderUuid);
        dto.setReason("test");
        return dto;
    }

    @Test
    void annualLeaveSufficientBalance() {
        LocalDate base = LocalDate.of(2025, 5, 5);
        assertDoesNotThrow(() ->
                applyService.submit(makeDTO(1, base.atTime(9, 0), base.atTime(10, 0))));
    }

    @Test
    void annualLeaveInsufficientBalance() {
        LocalDate base = LocalDate.of(2025, 5, 5);
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> applyService.submit(makeDTO(1, base.atTime(9, 0), base.plusDays(1).atTime(18, 0))));
        assertEquals("年假余额不足", e.getMessage());
    }

    @Test
    void compLeaveSufficientBalance() {
        LocalDate base = LocalDate.of(2025, 5, 5);
        assertDoesNotThrow(() ->
                applyService.submit(makeDTO(4, base.atTime(9, 0), base.atTime(10, 0))));
    }

    @Test
    void compLeaveInsufficientBalance() {
        LocalDate base = LocalDate.of(2025, 5, 5);
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> applyService.submit(makeDTO(4, base.atTime(9, 0), base.atTime(18, 0))));
        assertEquals("调休假余额不足", e.getMessage());
    }

    @Test
    void personalLeaveNoBalanceCheck() {
        LocalDate base = LocalDate.of(2025, 5, 5);
        assertDoesNotThrow(() ->
                applyService.submit(makeDTO(2, base.atTime(9, 0), base.plusDays(1).atTime(18, 0))));
    }

    @Test
    void noBalanceRecordThrowsException() {
        leaveBalanceDao.delete(null);

        LocalDate base = LocalDate.of(2025, 5, 5);
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> applyService.submit(makeDTO(1, base.atTime(9, 0), base.atTime(10, 0))));
        assertEquals("年假余额不足", e.getMessage());
    }

    private String insertApply(int type, BigDecimal length) {
        Apply apply = new Apply();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        apply.setUuid(uuid);
        apply.setApplyUserUuid(testUserUuid);
        apply.setType(type);
        apply.setLengthType(1);
        apply.setLength(length);
        apply.setMonth(LocalDate.of(2025, 5, 1));
        apply.setStartTime(LocalDateTime.of(2025, 5, 5, 9, 0));
        apply.setEndTime(LocalDateTime.of(2025, 5, 5, 10, 0));
        apply.setLeaderUuid(leaderUuid);
        apply.setReason("test");
        apply.setStatus(1);
        apply.setIsDelete(1);
        applyDao.insert(apply);
        return uuid;
    }

    private ApplyDTO makeUpdateDTO(String uuid, Integer type, BigDecimal length) {
        ApplyDTO dto = new ApplyDTO();
        dto.setUuid(uuid);
        dto.setMonth(LocalDate.of(2025, 5, 1));
        dto.setLengthType(1);
        dto.setStartTime(LocalDateTime.of(2025, 5, 5, 9, 0));
        dto.setEndTime(LocalDateTime.of(2025, 5, 5, 10, 0));
        dto.setReason("test");
        dto.setLeaderUuid(leaderUuid);
        if (type != null) dto.setType(type);
        if (length != null) dto.setLength(length);
        return dto;
    }

    @Test
    void updateCompLeaveInsufficientOnTypeChange() {
        String uuid = insertApply(2, BigDecimal.valueOf(1));
        ApplyDTO dto = makeUpdateDTO(uuid, 4, BigDecimal.valueOf(8));
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> applyService.update(dto));
        assertEquals("调休假余额不足", e.getMessage());
    }

    @Test
    void updateCompLeaveSufficientOnTypeChange() {
        String uuid = insertApply(2, BigDecimal.valueOf(1));
        ApplyDTO dto = makeUpdateDTO(uuid, 4, BigDecimal.valueOf(3));
        assertDoesNotThrow(() -> applyService.update(dto));
    }

    @Test
    void updateCompLeaveInsufficientOnLengthIncrease() {
        String uuid = insertApply(4, BigDecimal.valueOf(1));
        ApplyDTO dto = makeUpdateDTO(uuid, null, BigDecimal.valueOf(8));
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> applyService.update(dto));
        assertEquals("调休假余额不足", e.getMessage());
    }

    @Test
    void updateCompLeaveSufficientOnLengthDecrease() {
        String uuid = insertApply(4, BigDecimal.valueOf(3));
        ApplyDTO dto = makeUpdateDTO(uuid, 4, BigDecimal.valueOf(1));
        assertDoesNotThrow(() -> applyService.update(dto));
    }

    @Test
    void updateAnnualLeaveInsufficientOnTypeChange() {
        String uuid = insertApply(2, BigDecimal.valueOf(1));
        ApplyDTO dto = makeUpdateDTO(uuid, 1, BigDecimal.valueOf(16));
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> applyService.update(dto));
        assertEquals("年假余额不足", e.getMessage());
    }

    @Test
    void updateAnnualLeaveSufficientOnTypeChange() {
        String uuid = insertApply(2, BigDecimal.valueOf(1));
        ApplyDTO dto = makeUpdateDTO(uuid, 1, BigDecimal.valueOf(4));
        assertDoesNotThrow(() -> applyService.update(dto));
    }

    @Test
    void updateNonBalancedTypeDoesNotCheckBalance() {
        String uuid = insertApply(2, BigDecimal.valueOf(1));
        ApplyDTO dto = makeUpdateDTO(uuid, 2, BigDecimal.valueOf(999));
        assertDoesNotThrow(() -> applyService.update(dto));
    }
}
