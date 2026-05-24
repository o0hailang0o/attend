package com.hailang.service;

import com.hailang.dao.ApplyDao;
import com.hailang.dao.DailyAttendanceDao;
import com.hailang.dao.DoorAccessDao;
import com.hailang.dao.RuleDao;
import com.hailang.dao.SysUserDao;
import com.hailang.entity.Apply;
import com.hailang.entity.DailyAttendance;
import com.hailang.entity.DoorAccess;
import com.hailang.entity.Rule;
import com.hailang.entity.SysUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestExecutionListeners(value = {
        ServletTestExecutionListener.class,
        DirtiesContextBeforeModesTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
class DailyAttendanceServiceTest {

    @Autowired
    private DailyAttendanceService dailyAttendanceService;

    @Autowired
    private DailyAttendanceDao dailyAttendanceDao;

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private DoorAccessDao doorAccessDao;

    @Autowired
    private ApplyDao applyDao;

    @Autowired
    private RuleDao ruleDao;

    private String userUuid;
    private String userUuid2;
    private String ruleWithLunchUuid;

    private final LocalDate DAY1 = LocalDate.of(2026, 5, 11);  // Monday
    private final LocalDate DAY2 = LocalDate.of(2026, 5, 12);  // Tuesday
    private final LocalDate DAY3 = LocalDate.of(2026, 5, 13);  // Wednesday
    private final LocalDate SAT = LocalDate.of(2026, 5, 16);   // Saturday
    private final LocalDate HOLIDAY = LocalDate.of(2026, 5, 1); // Labour Day

    @BeforeEach
    void setUp() {
        dailyAttendanceDao.delete(null);
        doorAccessDao.delete(null);
        applyDao.delete(null);
        sysUserDao.delete(null);
        ruleDao.delete(null);

        Rule rule = new Rule();
        rule.setUuid(UUID.randomUUID().toString().replace("-", ""));
        ruleWithLunchUuid = rule.getUuid();
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

        userUuid = insertUser("张三", ruleWithLunchUuid);
        userUuid2 = insertUser("李四", ruleWithLunchUuid);
    }

    private String insertUser(String name, String ruleUuid) {
        SysUser user = new SysUser();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        user.setUuid(uuid);
        user.setName(name);
        user.setAccount(name);
        user.setPassword("e10adc3949ba59abbe56e057f20f883e");
        user.setRuleUuid(ruleUuid);
        user.setRuleName("标准规则");
        user.setGender(1);
        user.setIsDelete(1);
        sysUserDao.insert(user);
        return uuid;
    }

    private void insertDoorAccess(String employeeUuid, LocalDate date, LocalTime time, int direction) {
        DoorAccess da = new DoorAccess();
        da.setUuid(UUID.randomUUID().toString().replace("-", ""));
        da.setEmployeeUuid(employeeUuid);
        da.setEmployeeName("测试");
        da.setWorkNum("001");
        da.setDoorNo("D001");
        da.setAccessDate(date);
        da.setAccessTime(time);
        da.setDirection(direction);
        da.setIsDelete(1);
        doorAccessDao.insert(da);
    }

    private String insertApply(String userUuid, int type, LocalDateTime start, LocalDateTime end, BigDecimal length) {
        Apply apply = new Apply();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        apply.setUuid(uuid);
        apply.setMonth(start);
        apply.setType(type);
        apply.setLengthType(1);
        apply.setStartTime(start);
        apply.setEndTime(end);
        apply.setLength(length);
        apply.setApplyUserUuid(userUuid);
        apply.setLeaderUuid("leader");
        apply.setReason("test");
        apply.setStatus(2);
        apply.setIsDelete(1);
        applyDao.insert(apply);
        return uuid;
    }

    @Test
    void normalWorkDay() {
        insertDoorAccess(userUuid, DAY1, LocalTime.of(8, 55), 0);
        insertDoorAccess(userUuid, DAY1, LocalTime.of(18, 5), 1);

        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);

        List<DailyAttendance> records = dailyAttendanceDao.selectList(null);
        assertEquals(1, records.size());
        DailyAttendance da = records.get(0);
        assertEquals(userUuid, da.getEmployeeUuid());
        assertEquals(DAY1, da.getDate());
        assertEquals(LocalTime.of(8, 55), da.getClockIn());
        assertEquals(LocalTime.of(18, 5), da.getClockOut());
        assertEquals(0, BigDecimal.valueOf(9.2).compareTo(da.getActualWorkHours()));
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(da.getRecognizedHours()));
        assertEquals(0, BigDecimal.ZERO.compareTo(da.getLeaveHours()));
        assertEquals(1, da.getDayType());
    }

    @Test
    void noDoorAccess() {
        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);

        List<DailyAttendance> records = dailyAttendanceDao.selectList(null);
        assertEquals(1, records.size());
        DailyAttendance da = records.get(0);
        assertNull(da.getClockIn());
        assertNull(da.getClockOut());
        assertEquals(0, BigDecimal.ZERO.compareTo(da.getActualWorkHours()));
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(da.getRecognizedHours()));
    }

    @Test
    void fullDayAnnualLeave() {
        insertApply(userUuid, 1,
                LocalDateTime.of(DAY1, LocalTime.of(9, 0)),
                LocalDateTime.of(DAY1, LocalTime.of(18, 0)),
                BigDecimal.valueOf(8));

        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);

        DailyAttendance da = dailyAttendanceDao.selectList(null).get(0);
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(da.getLeaveHours()));
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(da.getAnnualLeaveHours()));
        assertEquals(0, BigDecimal.ZERO.compareTo(da.getCompLeaveHours()));
        assertEquals(0, BigDecimal.ZERO.compareTo(da.getRecognizedHours()));
    }

    @Test
    void halfDayAnnualLeaveMorning() {
        insertApply(userUuid, 1,
                LocalDateTime.of(DAY1, LocalTime.of(9, 0)),
                LocalDateTime.of(DAY1, LocalTime.of(12, 0)),
                BigDecimal.valueOf(3));

        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);

        DailyAttendance da = dailyAttendanceDao.selectList(null).get(0);
        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(da.getLeaveHours()));
        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(da.getAnnualLeaveHours()));
        assertEquals(0, BigDecimal.valueOf(5.0).compareTo(da.getRecognizedHours()));
    }

    @Test
    void halfDayCompLeaveAfternoon() {
        insertApply(userUuid, 4,
                LocalDateTime.of(DAY1, LocalTime.of(13, 0)),
                LocalDateTime.of(DAY1, LocalTime.of(18, 0)),
                BigDecimal.valueOf(5));

        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);

        DailyAttendance da = dailyAttendanceDao.selectList(null).get(0);
        assertEquals(0, BigDecimal.valueOf(5.0).compareTo(da.getLeaveHours()));
        assertEquals(0, BigDecimal.ZERO.compareTo(da.getAnnualLeaveHours()));
        assertEquals(0, BigDecimal.valueOf(5.0).compareTo(da.getCompLeaveHours()));
        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(da.getRecognizedHours()));
    }

    @Test
    void crossDayLeave() {
        insertApply(userUuid, 1,
                LocalDateTime.of(DAY1, LocalTime.of(14, 0)),
                LocalDateTime.of(DAY2, LocalTime.of(16, 0)),
                BigDecimal.valueOf(10));

        dailyAttendanceService.calculate(DAY1, DAY2, userUuid);

        List<DailyAttendance> records = dailyAttendanceDao.selectList(null);
        assertEquals(2, records.size());

        DailyAttendance d1 = records.stream().filter(r -> r.getDate().equals(DAY1)).findFirst().get();
        assertEquals(0, BigDecimal.valueOf(4.0).compareTo(d1.getLeaveHours()));

        DailyAttendance d2 = records.stream().filter(r -> r.getDate().equals(DAY2)).findFirst().get();
        assertEquals(0, BigDecimal.valueOf(6.0).compareTo(d2.getLeaveHours()));
    }

    @Test
    void crossLunchLeave() {
        insertApply(userUuid, 1,
                LocalDateTime.of(DAY1, LocalTime.of(11, 0)),
                LocalDateTime.of(DAY1, LocalTime.of(14, 0)),
                BigDecimal.valueOf(2));

        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);

        DailyAttendance da = dailyAttendanceDao.selectList(null).get(0);
        assertEquals(0, BigDecimal.valueOf(2.0).compareTo(da.getLeaveHours()));
    }

    @Test
    void weekendDayType() {
        dailyAttendanceService.calculate(SAT, SAT, userUuid);

        DailyAttendance da = dailyAttendanceDao.selectList(null).get(0);
        assertEquals(2, da.getDayType());
    }

    @Test
    void holidayDayType() {
        dailyAttendanceService.calculate(HOLIDAY, HOLIDAY, userUuid);

        DailyAttendance da = dailyAttendanceDao.selectList(null).get(0);
        assertEquals(3, da.getDayType());
    }

    @Test
    void calculateAllUsers() {
        insertDoorAccess(userUuid, DAY1, LocalTime.of(9, 0), 0);
        insertDoorAccess(userUuid2, DAY1, LocalTime.of(9, 30), 0);

        dailyAttendanceService.calculate(DAY1, DAY1, null);

        List<DailyAttendance> records = dailyAttendanceDao.selectList(null);
        assertEquals(2, records.size());
    }

    @Test
    void recalculateIdempotent() {
        insertDoorAccess(userUuid, DAY1, LocalTime.of(9, 0), 0);
        insertDoorAccess(userUuid, DAY1, LocalTime.of(18, 0), 1);

        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);
        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);

        List<DailyAttendance> records = dailyAttendanceDao.selectList(null);
        assertEquals(1, records.size());
    }

    @Test
    void userNotFound() {
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> dailyAttendanceService.calculate(DAY1, DAY1, "nonexistent-uuid"));
        assertEquals("用户不存在", e.getMessage());
    }

    @Test
    void multipleApplyOnSameDay() {
        insertApply(userUuid, 1,
                LocalDateTime.of(DAY1, LocalTime.of(9, 0)),
                LocalDateTime.of(DAY1, LocalTime.of(12, 0)),
                BigDecimal.valueOf(3));
        insertApply(userUuid, 4,
                LocalDateTime.of(DAY1, LocalTime.of(14, 0)),
                LocalDateTime.of(DAY1, LocalTime.of(16, 0)),
                BigDecimal.valueOf(2));

        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);

        DailyAttendance da = dailyAttendanceDao.selectList(null).get(0);
        assertEquals(0, BigDecimal.valueOf(5.0).compareTo(da.getLeaveHours()));
        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(da.getAnnualLeaveHours()));
        assertEquals(0, BigDecimal.valueOf(2.0).compareTo(da.getCompLeaveHours()));
        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(da.getRecognizedHours()));
    }

    @Test
    void onlyApprovedApplyCounts() {
        Apply notApproved = new Apply();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        notApproved.setUuid(uuid);
        notApproved.setMonth(LocalDateTime.of(DAY1, LocalTime.of(9, 0)));
        notApproved.setType(1);
        notApproved.setLengthType(1);
        notApproved.setStartTime(LocalDateTime.of(DAY1, LocalTime.of(9, 0)));
        notApproved.setEndTime(LocalDateTime.of(DAY1, LocalTime.of(18, 0)));
        notApproved.setLength(BigDecimal.valueOf(8));
        notApproved.setApplyUserUuid(userUuid);
        notApproved.setLeaderUuid("leader");
        notApproved.setReason("test");
        notApproved.setStatus(1);
        notApproved.setIsDelete(1);
        applyDao.insert(notApproved);

        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);

        DailyAttendance da = dailyAttendanceDao.selectList(null).get(0);
        assertEquals(0, BigDecimal.ZERO.compareTo(da.getLeaveHours()));
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(da.getRecognizedHours()));
    }

    @Test
    void annualAndCompLeaveHours() {
        insertDoorAccess(userUuid, DAY1, LocalTime.of(8, 50), 0);
        insertDoorAccess(userUuid, DAY1, LocalTime.of(18, 10), 1);

        insertApply(userUuid, 1,
                LocalDateTime.of(DAY1, LocalTime.of(9, 0)),
                LocalDateTime.of(DAY1, LocalTime.of(12, 0)),
                BigDecimal.valueOf(3));

        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);

        DailyAttendance da = dailyAttendanceDao.selectList(null).get(0);
        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(da.getLeaveHours()));
        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(da.getAnnualLeaveHours()));
        assertEquals(0, BigDecimal.valueOf(5.0).compareTo(da.getRecognizedHours()));
    }

    @Test
    void recognizedHoursFloorToAccuracy() {
        insertApply(userUuid, 1,
                LocalDateTime.of(DAY1, LocalTime.of(9, 20)),
                LocalDateTime.of(DAY1, LocalTime.of(12, 0)),
                BigDecimal.valueOf(2.5));

        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);

        DailyAttendance da = dailyAttendanceDao.selectList(null).get(0);
        assertEquals(0, BigDecimal.valueOf(2.5).compareTo(da.getLeaveHours()));
        assertEquals(0, BigDecimal.valueOf(5.5).compareTo(da.getRecognizedHours()));
    }

    @Test
    void workHoursExceedsStdHours() {
        insertDoorAccess(userUuid, DAY1, LocalTime.of(7, 0), 0);
        insertDoorAccess(userUuid, DAY1, LocalTime.of(20, 0), 1);

        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);

        DailyAttendance da = dailyAttendanceDao.selectList(null).get(0);
        assertEquals(0, BigDecimal.valueOf(13.0).compareTo(da.getActualWorkHours()));
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(da.getRecognizedHours()));
    }

    @Test
    void noRuleUser() {
        String noRuleUserUuid = insertUser("王五", null);

        insertDoorAccess(noRuleUserUuid, DAY1, LocalTime.of(9, 0), 0);
        insertDoorAccess(noRuleUserUuid, DAY1, LocalTime.of(18, 0), 1);

        dailyAttendanceService.calculate(DAY1, DAY1, noRuleUserUuid);

        DailyAttendance da = dailyAttendanceDao.selectList(null).get(0);
        assertNotNull(da.getClockIn());
        assertNotNull(da.getClockOut());
        assertEquals(LocalTime.of(9, 0), da.getClockIn());
        assertEquals(LocalTime.of(18, 0), da.getClockOut());
        assertEquals(0, BigDecimal.valueOf(9.0).compareTo(da.getActualWorkHours()));
        assertEquals(0, BigDecimal.ZERO.compareTo(da.getRecognizedHours()));
    }

    @Test
    void dateRangeMultipleDays() {
        insertDoorAccess(userUuid, DAY1, LocalTime.of(9, 0), 0);
        insertDoorAccess(userUuid, DAY1, LocalTime.of(18, 0), 1);
        insertDoorAccess(userUuid, DAY2, LocalTime.of(9, 0), 0);
        insertDoorAccess(userUuid, DAY2, LocalTime.of(18, 0), 1);
        insertDoorAccess(userUuid, DAY3, LocalTime.of(9, 0), 0);
        insertDoorAccess(userUuid, DAY3, LocalTime.of(18, 0), 1);

        dailyAttendanceService.calculate(DAY1, DAY3, userUuid);

        List<DailyAttendance> records = dailyAttendanceDao.selectList(null);
        assertEquals(3, records.size());
        records.forEach(r -> assertEquals(0, BigDecimal.valueOf(8.0).compareTo(r.getRecognizedHours())));
    }

    @Test
    void leaveDoesNotAffectOtherUsers() {
        insertApply(userUuid, 1,
                LocalDateTime.of(DAY1, LocalTime.of(9, 0)),
                LocalDateTime.of(DAY1, LocalTime.of(18, 0)),
                BigDecimal.valueOf(8));

        dailyAttendanceService.calculate(DAY1, DAY2, null);

        List<DailyAttendance> records = dailyAttendanceDao.selectList(null);
        DailyAttendance d1 = records.stream().filter(r -> r.getEmployeeUuid().equals(userUuid)).findFirst().get();
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(d1.getLeaveHours()));

        DailyAttendance d2 = records.stream().filter(r -> r.getEmployeeUuid().equals(userUuid2)).findFirst().get();
        assertEquals(0, BigDecimal.ZERO.compareTo(d2.getLeaveHours()));
    }

    @Test
    void leaveNotAppliedOnlyStatus2() {
        int[] statuses = {1, 3, 4, 5, 9};
        for (int s : statuses) {
            Apply a = new Apply();
            a.setUuid(UUID.randomUUID().toString().replace("-", ""));
            a.setMonth(LocalDateTime.of(DAY1, LocalTime.of(9, 0)));
            a.setType(1);
            a.setLengthType(1);
            a.setStartTime(LocalDateTime.of(DAY1, LocalTime.of(9, 0)));
            a.setEndTime(LocalDateTime.of(DAY1, LocalTime.of(18, 0)));
            a.setLength(BigDecimal.valueOf(8));
            a.setApplyUserUuid(userUuid);
            a.setLeaderUuid("leader");
            a.setReason("test");
            a.setStatus(s);
            a.setIsDelete(1);
            applyDao.insert(a);
        }

        dailyAttendanceService.calculate(DAY1, DAY1, userUuid);

        DailyAttendance da = dailyAttendanceDao.selectList(null).get(0);
        assertEquals(0, BigDecimal.ZERO.compareTo(da.getLeaveHours()));
    }
}
