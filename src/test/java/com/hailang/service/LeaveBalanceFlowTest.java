package com.hailang.service;

import com.hailang.dao.ApplyDao;
import com.hailang.dao.ApproveDao;
import com.hailang.dao.DeptDao;
import com.hailang.dao.LeaderDao;
import com.hailang.dao.LeaveBalanceDao;
import com.hailang.dao.RuleDao;
import com.hailang.dao.SysUserDao;
import com.hailang.entity.Apply;
import com.hailang.entity.Approve;
import com.hailang.entity.Dept;
import com.hailang.entity.Leader;
import com.hailang.entity.LeaveBalance;
import com.hailang.entity.Rule;
import com.hailang.entity.SysUser;
import com.hailang.service.dto.ApplyDTO;
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
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LeaveBalanceFlowTest {

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

    private String empUuid;
    private String approver1Uuid;
    private String approver2Uuid;
    private String treeLeaderUuid;
    private String ruleUuid;

    @BeforeEach
    void setUp() {
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

        approver1Uuid = UUID.randomUUID().toString().replace("-", "");
        Leader approver1 = new Leader();
        approver1.setId(1001L);
        approver1.setLeaderUuid(approver1Uuid);
        approver1.setLeaderName("审批人1");
        leaderDao.insert(approver1);

        approver2Uuid = UUID.randomUUID().toString().replace("-", "");
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
    void annualLeaveSubmitThenCancelRestoresBalance() {
        LeaveBalanceDTO bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(16).compareTo(bal.getAnnualRemainingHours()), "初始年假应为16");
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getCompRemainingHours()), "初始调休假应为8");

        // submit annual leave (8h) — immediately deduct
        ApplyDTO annualDto = new ApplyDTO();
        annualDto.setType(1);
        annualDto.setLength(BigDecimal.valueOf(8));
        annualDto.setMonth(LocalDateTime.of(LocalDate.now().getYear(), 5, 1, 0, 0));
        annualDto.setStartTime(LocalDateTime.now());
        annualDto.setEndTime(LocalDateTime.now().plusHours(8));
        annualDto.setLeaderUuid(treeLeaderUuid);
        annualDto.setReason("年假测试");
        applyService.submit(annualDto);

        bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getAnnualRemainingHours()), "提交即扣减：年假16→8");
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getCompRemainingHours()), "调休假不变");

        // cancel before any approval — balance restored
        List<Apply> applies = applyDao.selectList(null);
        assertEquals(1, applies.size());
        assertEquals(1, applies.get(0).getStatus(), "提交后状态为1");
        applyService.cancel(applies.get(0).getUuid());

        bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(16).compareTo(bal.getAnnualRemainingHours()), "取消后年假恢复16");
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getCompRemainingHours()), "调休假不变");
    }

    @Test
    void compLeaveSubmitThenRejectThenDeleteRestoresBalance() {
        LeaveBalanceDTO bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getCompRemainingHours()), "初始调休假为8");

        // submit comp leave (4h) — immediately deduct
        ApplyDTO dto = new ApplyDTO();
        dto.setType(4);
        dto.setLength(BigDecimal.valueOf(4));
        dto.setMonth(LocalDateTime.of(LocalDate.now().getYear(), 5, 2, 0, 0));
        dto.setStartTime(LocalDateTime.now().plusDays(1));
        dto.setEndTime(LocalDateTime.now().plusDays(1).plusHours(4));
        dto.setLeaderUuid(treeLeaderUuid);
        dto.setReason("调休假测试");
        applyService.submit(dto);

        bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(4).compareTo(bal.getCompRemainingHours()), "提交即扣减：调休假8→4");
        assertEquals(0, BigDecimal.valueOf(16).compareTo(bal.getAnnualRemainingHours()), "年假不变");

        // first approve passes — no deduction
        List<Approve> approves = approveDao.selectList(null);
        Approve first = approves.stream().filter(a -> a.getOrder() == 1).findFirst().get();
        approveService.pass(first.getUuid());
        bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(4).compareTo(bal.getCompRemainingHours()), "一级审批后仍为4");

        // second approve is rejected — balance NOT restored
        Approve second = approves.stream().filter(a -> a.getOrder() == 2).findFirst().get();
        approveService.reject(second.getUuid(), "驳回调休假");

        bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(4).compareTo(bal.getCompRemainingHours()), "驳回后调休假不恢复，仍为4");
        assertEquals(0, BigDecimal.valueOf(16).compareTo(bal.getAnnualRemainingHours()), "年假不变");

        // user deletes the rejected application — balance restored
        List<Apply> applies = applyDao.selectList(null);
        assertEquals(1, applies.size());
        assertEquals(3, applies.get(0).getStatus(), "驳回后状态为3");
        applyService.remove(applies.get(0).getUuid());

        bal = leaveBalanceService.getByUserUuid(empUuid);
        assertEquals(0, BigDecimal.valueOf(8).compareTo(bal.getCompRemainingHours()), "删除后调休假恢复8");
        assertEquals(0, BigDecimal.valueOf(16).compareTo(bal.getAnnualRemainingHours()), "年假不变");
    }
}
