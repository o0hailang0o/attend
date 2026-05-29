package com.hailang.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hailang.dao.ApplyDao;
import com.hailang.dao.ApproveDao;
import com.hailang.dao.DailyAttendanceDao;
import com.hailang.dao.DeptDao;
import com.hailang.dao.DoorAccessDao;
import com.hailang.dao.LeaderDao;
import com.hailang.dao.LeaveBalanceDao;
import com.hailang.dao.RuleDao;
import com.hailang.dao.SysUserDao;
import com.hailang.entity.Apply;
import com.hailang.entity.Approve;
import com.hailang.entity.Dept;
import com.hailang.entity.DoorAccess;
import com.hailang.entity.Leader;
import com.hailang.entity.LeaveBalance;
import com.hailang.entity.Rule;
import com.hailang.entity.SysUser;
import com.hailang.service.dto.ApplyDTO;
import com.hailang.service.dto.DailyAttendanceDTO;
import com.hailang.service.dto.LeaveBalanceDTO;
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
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DailyAttendanceFlowTest {

    @Autowired
    private DailyAttendanceService dailyAttendanceService;
    @Autowired
    private ApplyService applyService;
    @Autowired
    private ApproveService approveService;
    @Autowired
    private LeaveBalanceService leaveBalanceService;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private DeptDao deptDao;
    @Autowired
    private LeaveBalanceDao leaveBalanceDao;
    @Autowired
    private LeaderDao leaderDao;
    @Autowired
    private ApplyDao applyDao;
    @Autowired
    private ApproveDao approveDao;
    @Autowired
    private DoorAccessDao doorAccessDao;
    @Autowired
    private DailyAttendanceDao dailyAttendanceDao;

    private String empUuid;
    private String treeLeaderUuid;
    private String ruleUuid;

    @BeforeEach
    void setUp() {
        dailyAttendanceDao.delete(null);
        doorAccessDao.delete(null);
        applyDao.delete(null);
        approveDao.delete(null);
        leaderDao.delete(null);
        leaveBalanceDao.delete(null);
        sysUserDao.delete(null);
        deptDao.delete(null);
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
        rule.setOvertimeApply(1);
        rule.setIsDelete(1);
        ruleDao.insert(rule);

        Dept dept = new Dept();
        dept.setUuid(UUID.randomUUID().toString().replace("-", ""));
        dept.setName("运维组");
        dept.setIsDelete(1);
        deptDao.insert(dept);

        empUuid = UUID.randomUUID().toString().replace("-", "");
        SysUser emp = new SysUser();
        emp.setUuid(empUuid);
        emp.setName("测试员工");
        emp.setAccount("testemp");
        emp.setPassword("e10adc3949ba59abbe56e057f20f883e");
        emp.setRuleUuid(ruleUuid);
        emp.setRuleName("标准规则");
        emp.setDeptUuid(dept.getUuid());
        emp.setDeptName("运维组");
        emp.setGender(1);
        emp.setIsDelete(1);
        sysUserDao.insert(emp);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("sysUser", emp);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String approver1Uuid = UUID.randomUUID().toString().replace("-", "");
        Leader approver1 = new Leader();
        approver1.setId(1001L);
        approver1.setLeaderUuid(approver1Uuid);
        approver1.setLeaderName("审批人1");
        leaderDao.insert(approver1);

        String approver2Uuid = UUID.randomUUID().toString().replace("-", "");
        Leader approver2 = new Leader();
        approver2.setId(1002L);
        approver2.setLeaderUuid(approver2Uuid);
        approver2.setLeaderName("审批人2");
        leaderDao.insert(approver2);

        treeLeaderUuid = UUID.randomUUID().toString().replace("-", "");
        Leader treeLeader = new Leader();
        treeLeader.setId(1003L);
        treeLeader.setLeaderUuid(treeLeaderUuid);
        treeLeader.setLeaderName("部门主管");
        treeLeader.setTree("-1001-1002-");
        leaderDao.insert(treeLeader);

        LeaveBalance bal = new LeaveBalance();
        bal.setUuid(UUID.randomUUID().toString().replace("-", ""));
        bal.setUserUuid(empUuid);
        bal.setYear(LocalDate.now().getYear());
        bal.setAnnualRemainingHours(BigDecimal.valueOf(16));
        bal.setCompRemainingHours(BigDecimal.valueOf(8));
        bal.setIsDelete(1);
        leaveBalanceDao.insert(bal);
    }

    @Test
    void fullWeekAttendanceCalculation() {
        LocalDate mon = LocalDate.of(2026, 5, 11);
        LocalDate tue = LocalDate.of(2026, 5, 12);
        LocalDate wed = LocalDate.of(2026, 5, 13);
        LocalDate thu = LocalDate.of(2026, 5, 14);
        LocalDate fri = LocalDate.of(2026, 5, 15);

        // ========== 阶段① 创建门禁打卡记录 ==========
        createDoorAccess(mon, LocalTime.of(8, 55));
        createDoorAccess(mon, LocalTime.of(18, 5));
        createDoorAccess(tue, LocalTime.of(9, 15));
        createDoorAccess(tue, LocalTime.of(18, 0));
        createDoorAccess(thu, LocalTime.of(9, 0));
        createDoorAccess(thu, LocalTime.of(12, 0));

        // ========== 阶段② 提交年假申请 (周三全天 9:00-18:00 = 8h) ==========
        ApplyDTO annualDto = new ApplyDTO();
        annualDto.setType(1);
        annualDto.setLength(BigDecimal.valueOf(8));
        annualDto.setMonth(LocalDate.of(2026, 5, 1));
        annualDto.setStartTime(LocalDateTime.of(2026, 5, 13, 9, 0));
        annualDto.setEndTime(LocalDateTime.of(2026, 5, 13, 18, 0));
        annualDto.setLeaderUuid(treeLeaderUuid);
        annualDto.setReason("年假测试");
        applyService.submit(annualDto);

        LeaveBalanceDTO bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getAnnualRemainingHours()), "提交年假后：年假16→8");
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getCompRemainingHours()), "调休假不变");

        // ========== 阶段③ 提交调休假申请 (周四下午 13:00-18:00 = 5h) ==========
        ApplyDTO compDto = new ApplyDTO();
        compDto.setType(4);
        compDto.setLength(BigDecimal.valueOf(5));
        compDto.setMonth(LocalDate.of(2026, 5, 1));
        compDto.setStartTime(LocalDateTime.of(2026, 5, 14, 13, 0));
        compDto.setEndTime(LocalDateTime.of(2026, 5, 14, 18, 0));
        compDto.setLeaderUuid(treeLeaderUuid);
        compDto.setReason("调休假测试");
        applyService.submit(compDto);

        bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getAnnualRemainingHours()), "年假不变");
        assertEquals(0, BigDecimal.valueOf(3).compareTo(bal.getCompRemainingHours()), "提交调休后：调休假8→3");

        // ========== 阶段④ 审批通过 → 自动重算考勤明细 ==========
        List<Apply> applies = applyDao.selectList(null);
        Apply annualApply = applies.stream()
                .filter(a -> Integer.valueOf(1).equals(a.getType())).findFirst().orElseThrow();
        approveAllSteps(annualApply.getUuid());

        Apply compApply = applies.stream()
                .filter(a -> Integer.valueOf(4).equals(a.getType())).findFirst().orElseThrow();
        approveAllSteps(compApply.getUuid());

        annualApply = applyDao.selectOne(new LambdaQueryWrapper<Apply>()
                .eq(Apply::getUuid, annualApply.getUuid()).eq(Apply::getIsDelete, 1));
        assertEquals(9, annualApply.getStatus(), "年假申请审批通过(status=9)");

        compApply = applyDao.selectOne(new LambdaQueryWrapper<Apply>()
                .eq(Apply::getUuid, compApply.getUuid()).eq(Apply::getIsDelete, 1));
        assertEquals(9, compApply.getStatus(), "调休假申请审批通过(status=9)");

        // ========== 阶段⑤ 全量重算考勤（覆盖整个周期） ==========
        dailyAttendanceService.calculate(mon, fri, empUuid);

        // ========== 阶段⑥ 验证考勤明细 ==========
        List<DailyAttendanceDTO> records = dailyAttendanceService.queryByDateRange(empUuid, mon, fri);
        assertEquals(5, records.size(), "应生成5天考勤记录");

        // --- 周一：正常出勤 (08:55-18:05) ---
        DailyAttendanceDTO monRec = findByDate(records, mon);
        assertNotNull(monRec, "周一记录存在");
        assertEquals(LocalTime.of(8, 55), monRec.getClockIn(), "周一上班08:55");
        assertEquals(LocalTime.of(18, 5), monRec.getClockOut(), "周一下班18:05");
        assertEquals(0, BigDecimal.valueOf(9.2).compareTo(monRec.getActualWorkHours()), "周一实际工时9.2h");
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(monRec.getRecognizedHours()), "周一认定工时8.0h");
        assertEquals(0, BigDecimal.ZERO.compareTo(monRec.getLeaveHours()), "周一无请假");
        assertEquals(0, BigDecimal.ZERO.compareTo(monRec.getAnnualLeaveHours()), "周一无年假");
        assertEquals(0, BigDecimal.ZERO.compareTo(monRec.getCompLeaveHours()), "周一无调休");
        assertEquals(1, monRec.getDayType(), "周一工作日");
        assertEquals(1, monRec.getStatus(), "周一状态：正常(1)");

        // --- 周二：迟到 (09:15到) ---
        DailyAttendanceDTO tueRec = findByDate(records, tue);
        assertNotNull(tueRec, "周二记录存在");
        assertEquals(LocalTime.of(9, 15), tueRec.getClockIn(), "周二上班09:15");
        assertEquals(LocalTime.of(18, 0), tueRec.getClockOut(), "周二下班18:00");
        assertEquals(0, BigDecimal.valueOf(8.8).compareTo(tueRec.getActualWorkHours()), "周二实际工时8.8h");
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(tueRec.getRecognizedHours()), "周二认定工时8.0h");
        assertEquals(0, BigDecimal.ZERO.compareTo(tueRec.getLeaveHours()), "周二无请假");
        assertEquals(2, tueRec.getStatus(), "周二状态：迟到(2)");

        // --- 周三：全天年假 (9:00-18:00 = 8h，扣午休1h后实际8h) ---
        DailyAttendanceDTO wedRec = findByDate(records, wed);
        assertNotNull(wedRec, "周三记录存在");
        assertNull(wedRec.getClockIn(), "周三无打卡");
        assertNull(wedRec.getClockOut(), "周三无打卡");
        assertEquals(0, BigDecimal.ZERO.compareTo(wedRec.getActualWorkHours()), "周三实际工时0");
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(wedRec.getLeaveHours()), "周三请假8h");
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(wedRec.getAnnualLeaveHours()), "周三年假8h");
        assertEquals(0, BigDecimal.ZERO.compareTo(wedRec.getCompLeaveHours()), "周三无调休");
        assertEquals(0, BigDecimal.ZERO.compareTo(wedRec.getRecognizedHours()), "周三认定工时0(8-8=0)");
        assertEquals(5, wedRec.getStatus(), "周三状态：补正(5)");

        // --- 周四：上午出勤 + 下午调休 (09:00-12:00出勤, 13:00-18:00调休5h) ---
        DailyAttendanceDTO thuRec = findByDate(records, thu);
        assertNotNull(thuRec, "周四记录存在");
        assertEquals(LocalTime.of(9, 0), thuRec.getClockIn(), "周四上班09:00");
        assertEquals(LocalTime.of(12, 0), thuRec.getClockOut(), "周四下班12:00");
        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(thuRec.getActualWorkHours()), "周四实际工时3.0h");
        assertEquals(0, BigDecimal.valueOf(5.0).compareTo(thuRec.getLeaveHours()), "周四请假5h");
        assertEquals(0, BigDecimal.valueOf(5.0).compareTo(thuRec.getCompLeaveHours()), "周四调休5h");
        assertEquals(0, BigDecimal.ZERO.compareTo(thuRec.getAnnualLeaveHours()), "周四无年假");
        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(thuRec.getRecognizedHours()), "周四认定工时3.0h(8-5=3)");
        assertEquals(5, thuRec.getStatus(), "周四状态：补正(5)");

        // --- 周五：缺勤（无打卡、无请假） ---
        DailyAttendanceDTO friRec = findByDate(records, fri);
        assertNotNull(friRec, "周五记录存在");
        assertNull(friRec.getClockIn(), "周五无打卡");
        assertNull(friRec.getClockOut(), "周五无打卡");
        assertEquals(0, BigDecimal.ZERO.compareTo(friRec.getActualWorkHours()), "周五实际工时0");
        assertEquals(0, BigDecimal.ZERO.compareTo(friRec.getLeaveHours()), "周五无请假");
        assertEquals(0, BigDecimal.ZERO.compareTo(friRec.getAnnualLeaveHours()), "周五无年假");
        assertEquals(0, BigDecimal.ZERO.compareTo(friRec.getCompLeaveHours()), "周五无调休");
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(friRec.getRecognizedHours()), "周五认定工时8.0h(应出勤)");
        assertEquals(4, friRec.getStatus(), "周五状态：缺勤(4)");

        // ========== 阶段⑦ 验证最终假期余额 ==========
        bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getAnnualRemainingHours()), "最终年假余额8h");
        assertEquals(0, BigDecimal.valueOf(3).compareTo(bal.getCompRemainingHours()), "最终调休假余额3h");
    }

    private void createDoorAccess(LocalDate date, LocalTime time) {
        DoorAccess da = new DoorAccess();
        da.setUuid(UUID.randomUUID().toString().replace("-", ""));
        da.setEmployeeUuid(empUuid);
        da.setEmployeeName("测试员工");
        da.setWorkNum("001");
        da.setDoorNo("A001");
        da.setDirection(0);
        da.setAccessDatetime(LocalDateTime.of(date, time));
        da.setIsDelete(1);
        doorAccessDao.insert(da);
    }

    private void approveAllSteps(String applyUuid) {
        List<Approve> approves = approveDao.selectList(
                new LambdaQueryWrapper<Approve>()
                        .eq(Approve::getApplyUuid, applyUuid)
                        .eq(Approve::getIsDelete, 1)
                        .orderByAsc(Approve::getOrder));
        for (Approve approve : approves) {
            approveService.pass(approve.getUuid());
        }
    }

    private DailyAttendanceDTO findByDate(List<DailyAttendanceDTO> records, LocalDate date) {
        return records.stream()
                .filter(r -> date.equals(r.getDate()))
                .findFirst()
                .orElse(null);
    }

    @Test
    void crossDayLeaveCalculation() {
        // 设置跨天请假场景：周五下午到周一上午（周五 14:00-18:00 + 周一 9:00-12:00）
        LocalDate fri = LocalDate.of(2026, 5, 15);
        LocalDate sat = LocalDate.of(2026, 5, 16);
        LocalDate sun = LocalDate.of(2026, 5, 17);
        LocalDate mon = LocalDate.of(2026, 5, 18);
        LocalDate tue = LocalDate.of(2026, 5, 19);

        // ========== 阶段① 创建门禁打卡记录 ==========
        createDoorAccess(fri, LocalTime.of(14, 0));  // 周五开始请假
        createDoorAccess(fri, LocalTime.of(18, 0));  // 周五请假结束
        createDoorAccess(mon, LocalTime.of(9, 0));   // 周一继续请假
        createDoorAccess(mon, LocalTime.of(12, 0));  // 周一请假结束
        createDoorAccess(tue, LocalTime.of(9, 15));  // 周二上班
        createDoorAccess(tue, LocalTime.of(18, 0));  // 周二下班

        // ========== 阶段② 提交跨天年假申请（周五14:00-周一12:00） ==========
        ApplyDTO annualDto = new ApplyDTO();
        annualDto.setType(1);
        annualDto.setLength(BigDecimal.valueOf(14));  // 总时长14h（周五4h + 周一3h）
        annualDto.setMonth(LocalDate.of(2026, 5, 1));
        annualDto.setStartTime(LocalDateTime.of(2026, 5, 15, 14, 0));
        annualDto.setEndTime(LocalDateTime.of(2026, 5, 18, 12, 0));
        annualDto.setLeaderUuid(treeLeaderUuid);
        annualDto.setReason("跨天年假测试");
        applyService.submit(annualDto);

        LeaveBalanceDTO bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(2).compareTo(bal.getAnnualRemainingHours()), "提交跨天年假后：年假16→2");
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getCompRemainingHours()), "调休假不变");

        // ========== 阶段③ 审批通过 → 自动重算考勤明细 ==========
        List<Apply> applies = applyDao.selectList(null);
        Apply annualApply = applies.stream()
                .filter(a -> Integer.valueOf(1).equals(a.getType())).findFirst().orElseThrow();
        approveAllSteps(annualApply.getUuid());

        annualApply = applyDao.selectOne(new LambdaQueryWrapper<Apply>()
                .eq(Apply::getUuid, annualApply.getUuid()).eq(Apply::getIsDelete, 1));
        assertEquals(9, annualApply.getStatus(), "跨天年假申请审批通过(status=9)");

        // ========== 阶段④ 全量重算考勤（覆盖整个周期） ==========
        dailyAttendanceService.calculate(fri, tue, empUuid);

        // ========== 阶段⑤ 验证考勤明细 ==========
        List<DailyAttendanceDTO> records = dailyAttendanceService.queryByDateRange(empUuid, fri, tue);
        assertEquals(5, records.size(), "应生成5天考勤记录");

        // --- 周五：年假（跨天逻辑分配8h）---
        DailyAttendanceDTO friRec = findByDate(records, fri);
        assertNotNull(friRec, "周五记录存在");
        assertEquals(LocalTime.of(14, 0), friRec.getClockIn(), "周五上班14:00");
        assertEquals(LocalTime.of(18, 0), friRec.getClockOut(), "周五下班18:00");
        assertEquals(0, BigDecimal.valueOf(4.0).compareTo(friRec.getActualWorkHours()), "周五实际工时4.0h");
        assertEquals(0, BigDecimal.ZERO.compareTo(friRec.getCompLeaveHours()), "周五无调休");
        assertEquals(5, friRec.getStatus(), "周五状态：补正(5)");

        // --- 周六：周末（跨天年假虽分配8h，但 dayType=2 优先，状态=正常）---
        DailyAttendanceDTO satRec = findByDate(records, sat);
        assertNotNull(satRec, "周六记录存在");
        assertNull(satRec.getClockIn(), "周六无打卡");
        assertNull(satRec.getClockOut(), "周六无打卡");
        assertEquals(0, BigDecimal.ZERO.compareTo(satRec.getActualWorkHours()), "周六实际工时0");
        assertEquals(2, satRec.getDayType(), "周六休息日");
        assertEquals(1, satRec.getStatus(), "周六状态：正常(1)");

        // --- 周日：周末 ---
        DailyAttendanceDTO sunRec = findByDate(records, sun);
        assertNotNull(sunRec, "周日记录存在");
        assertNull(sunRec.getClockIn(), "周日无打卡");
        assertNull(sunRec.getClockOut(), "周日无打卡");
        assertEquals(0, BigDecimal.ZERO.compareTo(sunRec.getActualWorkHours()), "周日实际工时0");
        assertEquals(2, sunRec.getDayType(), "周日休息日");
        assertEquals(1, sunRec.getStatus(), "周日状态：正常(1)");

        // --- 周一：年假（跨天逻辑分配8h）---
        DailyAttendanceDTO monRec = findByDate(records, mon);
        assertNotNull(monRec, "周一记录存在");
        assertEquals(LocalTime.of(9, 0), monRec.getClockIn(), "周一上班9:00");
        assertEquals(LocalTime.of(12, 0), monRec.getClockOut(), "周一下班12:00");
        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(monRec.getActualWorkHours()), "周一实际工时3.0h");
        assertEquals(0, BigDecimal.ZERO.compareTo(monRec.getCompLeaveHours()), "周一无调休");
        assertEquals(5, monRec.getStatus(), "周一状态：补正(5)");

        // --- 周二：正常出勤（9:15到）---
        DailyAttendanceDTO tueRec = findByDate(records, tue);
        assertNotNull(tueRec, "周二记录存在");
        assertEquals(LocalTime.of(9, 15), tueRec.getClockIn(), "周二上班09:15");
        assertEquals(LocalTime.of(18, 0), tueRec.getClockOut(), "周二下班18:00");
        assertEquals(0, BigDecimal.valueOf(8.8).compareTo(tueRec.getActualWorkHours()), "周二实际工时8.8h");
        assertEquals(0, BigDecimal.ZERO.compareTo(tueRec.getLeaveHours()), "周二无请假");
        assertEquals(0, BigDecimal.ZERO.compareTo(tueRec.getAnnualLeaveHours()), "周二无年假");
        assertEquals(0, BigDecimal.ZERO.compareTo(tueRec.getCompLeaveHours()), "周二无调休");
        assertEquals(0, BigDecimal.valueOf(8.0).compareTo(tueRec.getRecognizedHours()), "周二认定工时8.0h");
        assertEquals(2, tueRec.getStatus(), "周二状态：迟到(2)");

        // ========== 阶段⑥ 验证最终假期余额 ==========
        bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(2).compareTo(bal.getAnnualRemainingHours()), "最终年假余额2h");
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getCompRemainingHours()), "最终调休假余额8h（不变）");
    }

    @Test
    void marriageLeaveAttendanceCalculation() {
        // 验证婚假（type=5）场景：
        //   1. 提交婚假申请不扣余额（deduct_balance=0）
        //   2. 审批通过后自动重算考勤明细
        //   3. 婚假工时归入 leaveHours 但不计入 annualLeaveHours / compLeaveHours
        //   4. 余额不变

        LocalDate start = LocalDate.of(2026, 6, 1);
        LocalDate end = LocalDate.of(2026, 6, 10);
        // 6/1 6/2 6/3 6/4 6/5 | 6/6(Sat) 6/7(Sun) | 6/8 6/9 6/10
        // 10 个日历日，其中 2 个周末，8 个工作日

        // ========== 阶段① 提交婚假申请 ==========
        ApplyDTO dto = new ApplyDTO();
        dto.setType(5);
        dto.setLength(BigDecimal.valueOf(80));
        dto.setMonth(LocalDate.of(2026, 6, 1));
        dto.setStartTime(LocalDateTime.of(2026, 6, 1, 9, 0));
        dto.setEndTime(LocalDateTime.of(2026, 6, 10, 18, 0));
        dto.setLeaderUuid(treeLeaderUuid);
        dto.setReason("婚假测试");
        applyService.submit(dto);

        LeaveBalanceDTO bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(16).compareTo(bal.getAnnualRemainingHours()), "婚假不扣年假");
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getCompRemainingHours()), "婚假不扣调休假");

        // ========== 阶段② 审批通过 → 自动重算考勤 ==========
        List<Apply> applies = applyDao.selectList(null);
        Apply marriageApply = applies.stream()
                .filter(a -> Integer.valueOf(5).equals(a.getType())).findFirst().orElseThrow();
        approveAllSteps(marriageApply.getUuid());

        marriageApply = applyDao.selectOne(new LambdaQueryWrapper<Apply>()
                .eq(Apply::getUuid, marriageApply.getUuid()).eq(Apply::getIsDelete, 1));
        assertEquals(9, marriageApply.getStatus(), "婚假申请审批通过(status=9)");

        // ========== 阶段③ 验证考勤明细（10天） ==========
        List<DailyAttendanceDTO> records = dailyAttendanceService.queryByDateRange(empUuid, start, end);
        assertEquals(10, records.size(), "应生成10天考勤记录");

        for (int i = 0; i < 10; i++) {
            LocalDate date = start.plusDays(i);
            DailyAttendanceDTO rec = findByDate(records, date);
            assertNotNull(rec, date + "记录存在");

            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                // 周末：dayType=2，状态始终 1（正常），不问请假
                assertEquals(2, rec.getDayType(), date + "周末");
                assertEquals(1, rec.getStatus(), date + "状态正常");
                assertEquals(0, BigDecimal.ZERO.compareTo(rec.getAnnualLeaveHours()), date + "无年假");
                assertEquals(0, BigDecimal.ZERO.compareTo(rec.getCompLeaveHours()), date + "无调休");
            } else {
                // 工作日：婚假 8h → leaveHours=8, annualLeaveHours=0, compLeaveHours=0, 认定工时=0
                assertEquals(1, rec.getDayType(), date + "工作日");
                assertEquals(5, rec.getStatus(), date + "状态补正");
                assertEquals(0, BigDecimal.valueOf(8.0).compareTo(rec.getLeaveHours()), date + "请假8h");
                assertEquals(0, BigDecimal.ZERO.compareTo(rec.getAnnualLeaveHours()), date + "无年假");
                assertEquals(0, BigDecimal.ZERO.compareTo(rec.getCompLeaveHours()), date + "无调休");
                assertEquals(0, BigDecimal.ZERO.compareTo(rec.getRecognizedHours()), date + "认定工时0");
            }
        }

        // ========== 阶段④ 验证余额不变 ==========
        bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(16).compareTo(bal.getAnnualRemainingHours()), "最终年假不变");
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getCompRemainingHours()), "最终调休假不变");
    }

    @Test
    void otherLeaveTypesAttendanceCalculation() {
        // 一次性验证 6 种假期类型（均为 deduct_balance=0）：
        //   事假(2) 病假(3) 丧假(6) 产假(7) 陪产假(8) 工伤假(9)
        // 共同特征：
        //   1. 提交不扣余额
        //   2. 工时归入 leaveHours，不计入 annualLeaveHours / compLeaveHours
        //   3. 余额不变

        // {type, day}  2026年6月全部选工作日
        int[][] defs = {{2, 15}, {3, 16}, {6, 17}, {7, 18}, {8, 19}, {9, 22}};
        String[] names = {"事假", "病假", "丧假", "产假", "陪产假", "工伤假"};

        // ========== 阶段① 依次提交 6 条申请 ==========
        for (int i = 0; i < defs.length; i++) {
            int type = defs[i][0];
            int day = defs[i][1];
            ApplyDTO dto = new ApplyDTO();
            dto.setType(type);
            dto.setLength(BigDecimal.valueOf(8));
            dto.setMonth(LocalDate.of(2026, 6, 1));
            dto.setStartTime(LocalDateTime.of(2026, 6, day, 9, 0));
            dto.setEndTime(LocalDateTime.of(2026, 6, day, 18, 0));
            dto.setLeaderUuid(treeLeaderUuid);
            dto.setReason(names[i] + "测试");
            applyService.submit(dto);
        }

        LeaveBalanceDTO bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(16).compareTo(bal.getAnnualRemainingHours()), "年假不变");
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getCompRemainingHours()), "调休假不变");

        // ========== 阶段② 逐个审批通过 ==========
        List<Apply> allApplies = applyDao.selectList(null);
        for (int[] def : defs) {
            Apply apply = allApplies.stream()
                    .filter(a -> Integer.valueOf(def[0]).equals(a.getType())).findFirst().orElseThrow();
            approveAllSteps(apply.getUuid());

            apply = applyDao.selectOne(new LambdaQueryWrapper<Apply>()
                    .eq(Apply::getUuid, apply.getUuid()).eq(Apply::getIsDelete, 1));
            assertEquals(9, apply.getStatus(), def[0] + "审批通过(status=9)");
        }

        // ========== 阶段③ 验证考勤明细 ==========
        LocalDate start = LocalDate.of(2026, 6, 15);
        LocalDate end = LocalDate.of(2026, 6, 22);
        List<DailyAttendanceDTO> records = dailyAttendanceService.queryByDateRange(empUuid, start, end);

        for (int i = 0; i < defs.length; i++) {
            int type = defs[i][0];
            int day = defs[i][1];
            String name = names[i];
            LocalDate date = LocalDate.of(2026, 6, day);

            DailyAttendanceDTO rec = findByDate(records, date);
            assertNotNull(rec, name + "记录存在");
            assertEquals(1, rec.getDayType(), name + "工作日");
            assertEquals(5, rec.getStatus(), name + "状态补正(5)");
            assertEquals(0, BigDecimal.valueOf(8.0).compareTo(rec.getLeaveHours()), name + "请假8h");
            assertEquals(0, BigDecimal.ZERO.compareTo(rec.getAnnualLeaveHours()), name + "无年假");
            assertEquals(0, BigDecimal.ZERO.compareTo(rec.getCompLeaveHours()), name + "无调休");
            assertEquals(0, BigDecimal.ZERO.compareTo(rec.getRecognizedHours()), name + "认定工时0");
        }

        // 周末（6/20-6/21）不在任何 calculate 范围内，无记录
        assertNull(findByDate(records, LocalDate.of(2026, 6, 20)), "周六无记录");
        assertNull(findByDate(records, LocalDate.of(2026, 6, 21)), "周日无记录");

        // ========== 阶段④ 验证余额不变 ==========
        bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(16).compareTo(bal.getAnnualRemainingHours()), "最终年假不变");
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getCompRemainingHours()), "最终调休假不变");
    }
}
