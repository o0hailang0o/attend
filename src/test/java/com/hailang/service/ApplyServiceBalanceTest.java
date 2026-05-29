package com.hailang.service;

import com.hailang.dao.ApplyDao;
import com.hailang.dao.ApproveDao;
import com.hailang.dao.LeaderDao;
import com.hailang.dao.LeaveBalanceDao;
import com.hailang.entity.Leader;
import com.hailang.entity.LeaveBalance;
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

    private String testUserUuid;
    private String leaderUuid;

    @BeforeEach
    void setUp() {
        leaveBalanceDao.delete(null);
        leaderDao.delete(null);
        applyDao.delete(null);
        approveDao.delete(null);

        testUserUuid = UUID.randomUUID().toString().replace("-", "");
        leaderUuid = UUID.randomUUID().toString().replace("-", "");

        SysUser testUser = new SysUser();
        testUser.setUuid(testUserUuid);

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

    private ApplyDTO makeDTO(int type, double hours) {
        ApplyDTO dto = new ApplyDTO();
        dto.setType(type);
        dto.setLength(BigDecimal.valueOf(hours));
        dto.setMonth(LocalDate.of(2025, 5, 1));
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now().plusHours(1));
        dto.setLeaderUuid(leaderUuid);
        dto.setReason("test");
        return dto;
    }

    @Test
    void annualLeaveSufficientBalance() {
        assertDoesNotThrow(() -> applyService.submit(makeDTO(1, 8)));
    }

    @Test
    void annualLeaveInsufficientBalance() {
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> applyService.submit(makeDTO(1, 16)));
        assertEquals("年假余额不足", e.getMessage());
    }

    @Test
    void compLeaveSufficientBalance() {
        assertDoesNotThrow(() -> applyService.submit(makeDTO(4, 4)));
    }

    @Test
    void compLeaveInsufficientBalance() {
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> applyService.submit(makeDTO(4, 8)));
        assertEquals("调休假余额不足", e.getMessage());
    }

    @Test
    void personalLeaveNoBalanceCheck() {
        assertDoesNotThrow(() -> applyService.submit(makeDTO(2, 100)));
    }

    @Test
    void noBalanceRecordThrowsException() {
        leaveBalanceDao.delete(null);

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> applyService.submit(makeDTO(1, 1)));
        assertEquals("年假余额不足", e.getMessage());
    }
}
