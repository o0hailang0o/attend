package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hailang.config.AuthContext;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.ApplyDao;
import com.hailang.dao.ApproveDao;
import com.hailang.dao.LeaderDao;
import com.hailang.entity.Apply;
import com.hailang.controller.resp.WorkflowStepResp;
import com.hailang.entity.Approve;
import com.hailang.entity.Leader;
import com.hailang.entity.SysUser;
import com.hailang.service.ApplyService;
import com.hailang.service.LeaveBalanceService;
import com.hailang.service.RuleService;
import com.hailang.service.dto.ApplyDTO;
import com.hailang.service.dto.LeaveBalanceDTO;
import com.hailang.service.dto.RuleDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ApplyServiceImpl extends ServiceImpl<ApplyDao, Apply> implements ApplyService {

    private final RuleService ruleService;
    private final LeaderDao leaderDao;
    private final ApproveDao approveDao;
    private final LeaveBalanceService leaveBalanceService;

    public ApplyServiceImpl(RuleService ruleService, LeaderDao leaderDao, ApproveDao approveDao,
                            LeaveBalanceService leaveBalanceService) {
        this.ruleService = ruleService;
        this.leaderDao = leaderDao;
        this.approveDao = approveDao;
        this.leaveBalanceService = leaveBalanceService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(ApplyDTO dto) {
        SysUser currentUser = AuthContext.getCurrentUser();
        String applicantUuid = currentUser != null ? currentUser.getUuid() : null;

        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            throw new RuntimeException("开始时间和结束时间不能为空");
        }
        if (dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new RuntimeException("开始时间不能晚于结束时间");
        }
        if (dto.getStartTime().getYear() != dto.getEndTime().getYear()
                || dto.getStartTime().getMonth() != dto.getEndTime().getMonth()) {
            throw new RuntimeException("开始时间和结束时间不允许跨月");
        }

        if (currentUser == null || currentUser.getRuleUuid() == null) {
            throw new RuntimeException("当前用户未配置考勤规则");
        }
        dto.setLength(calculateLength(dto.getStartTime(), dto.getEndTime(), currentUser.getRuleUuid()));
        if (dto.getLength().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("时长必须大于0");
        }

        if (Integer.valueOf(1).equals(dto.getType())) {
            LeaveBalanceDTO bal = leaveBalanceService.getCurrent();
            if (bal == null || bal.getAnnualRemainingHours() == null || bal.getAnnualRemainingHours().compareTo(dto.getLength()) < 0) {
                throw new RuntimeException("年假余额不足");
            }
            leaveBalanceService.deductAnnual(applicantUuid, dto.getLength());
        } else if (Integer.valueOf(4).equals(dto.getType())) {
            LeaveBalanceDTO bal = leaveBalanceService.getCurrent();
            if (bal == null || bal.getCompRemainingHours() == null || bal.getCompRemainingHours().compareTo(dto.getLength()) < 0) {
                throw new RuntimeException("调休假余额不足");
            }
            leaveBalanceService.deductComp(applicantUuid, dto.getLength());
        }

        Leader leader = leaderDao.selectByLeaderUuid(dto.getLeaderUuid());

        Apply entity = BeanUtils.copy(dto, Apply.class);
        entity.setUuid(UUID.randomUUID().toString().replace("-", ""));
        entity.setApplyUserUuid(applicantUuid);
        entity.setStatus(1);
        entity.setIsDelete(1);
        baseMapper.insert(entity);

        if (leader != null && leader.getTree() != null && !leader.getTree().isEmpty() && !"-".equals(leader.getTree())) {
            String tree = leader.getTree();
            if (tree.startsWith("-")) tree = tree.substring(1);
            if (tree.endsWith("-")) tree = tree.substring(0, tree.length() - 1);
            String[] ids = tree.split("-");
            int order = 1;
            for (String idStr : ids) {
                Leader approver = leaderDao.selectById(Long.parseLong(idStr));
                if (approver != null) {
                    Approve approve = new Approve();
                    approve.setUuid(UUID.randomUUID().toString().replace("-", ""));
                    approve.setApplyUuid(entity.getUuid());
                    approve.setOrder(order);
                    approve.setLeaderUuid(approver.getLeaderUuid());
                    approve.setStatus(order == 1 ? 4 : 5);
                    approve.setIsDelete(1);
                    approveDao.insert(approve);
                    order++;
                }
            }
        }
    }

    @Override
    public ApplyDTO getByUuid(String uuid) {
        Apply entity = baseMapper.selectOne(
                new LambdaQueryWrapper<Apply>()
                        .eq(Apply::getUuid, uuid)
                        .eq(Apply::getIsDelete, 1));
        if (entity == null) {
            throw new RuntimeException("申请不存在");
        }
        return BeanUtils.copy(entity, ApplyDTO.class);
    }

    @Override
    public IPage<ApplyDTO> listByUser(String userUuid, LocalDate month, int page, int size) {
        Page<Apply> pageParam = new Page<>(page, size);
        Page<Apply> result = baseMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Apply>()
                        .eq(Apply::getApplyUserUuid, userUuid)
                        .eq(month != null, Apply::getMonth, month)
                        .eq(Apply::getIsDelete, 1)
                        .orderByDesc(Apply::getCreateTime)
        );
        IPage<ApplyDTO> dtoPage = result.convert(item -> {
            ApplyDTO dto = BeanUtils.copy(item, ApplyDTO.class);
            if (dto.getStatus() != null) {
                switch (dto.getStatus()) {
                    case 1: dto.setStatusName("提交"); break;
                    case 2: dto.setStatusName("保存"); break;
                    case 3: dto.setStatusName("驳回"); break;
                    case 4: dto.setStatusName("待审批"); break;
                    case 5: dto.setStatusName("审批中"); break;
                    case 9: dto.setStatusName("审批通过"); break;
                }
            }
            return dto;
        });

        List<String> applyUuids = dtoPage.getRecords().stream()
                .map(ApplyDTO::getUuid)
                .collect(Collectors.toList());
        if (!applyUuids.isEmpty()) {
            List<WorkflowStepResp> steps = approveDao.selectWorkflowByApplyUuids(applyUuids);
            Map<String, List<WorkflowStepResp>> workflowMap = steps.stream()
                    .collect(Collectors.groupingBy(WorkflowStepResp::getApplyUuid));
            dtoPage.getRecords().forEach(r -> r.setWorkflow(workflowMap.get(r.getUuid())));
        }

        return dtoPage;
    }

    @Override
    public void update(ApplyDTO dto) {
        Apply entity = baseMapper.selectOne(
                new LambdaQueryWrapper<Apply>()
                        .eq(Apply::getUuid, dto.getUuid())
                        .eq(Apply::getIsDelete, 1));
        if (entity == null) {
            throw new RuntimeException("申请不存在");
        }
        if (entity.getStatus() != 1) {
            throw new RuntimeException("仅待审批状态的申请可以编辑");
        }
        baseMapper.update(null,
                Wrappers.<Apply>lambdaUpdate()
                        .eq(Apply::getUuid, dto.getUuid())
                        .set(Apply::getMonth, dto.getMonth())
                        .set(Apply::getType, dto.getType())
                        .set(Apply::getLengthType, dto.getLengthType())
                        .set(Apply::getStartTime, dto.getStartTime())
                        .set(Apply::getEndTime, dto.getEndTime())
                        .set(Apply::getLength, dto.getLength())
                        .set(Apply::getReason, dto.getReason())
                        .set(Apply::getLeaderUuid, dto.getLeaderUuid()));
    }

    @Override
    public void remove(String uuid) {
        Apply entity = baseMapper.selectOne(
                new LambdaQueryWrapper<Apply>()
                        .eq(Apply::getUuid, uuid)
                        .eq(Apply::getIsDelete, 1));
        if (entity == null) {
            throw new RuntimeException("申请不存在");
        }
        if (entity.getStatus() != 1 && entity.getStatus() != 3) {
            throw new RuntimeException("仅待审批或已驳回的申请可以删除");
        }
        baseMapper.update(null,
                Wrappers.<Apply>lambdaUpdate()
                        .eq(Apply::getUuid, uuid)
                        .set(Apply::getIsDelete, 0));
        restoreBalanceIfNeeded(entity);
    }

    @Override
    public void cancel(String uuid) {
        Apply entity = baseMapper.selectOne(
                new LambdaQueryWrapper<Apply>()
                        .eq(Apply::getUuid, uuid)
                        .eq(Apply::getIsDelete, 1));
        if (entity == null) {
            throw new RuntimeException("申请不存在");
        }
        if (entity.getStatus() != 1) {
            throw new RuntimeException("只能撤销已提交的申请");
        }
        baseMapper.update(null,
                Wrappers.<Apply>lambdaUpdate()
                        .eq(Apply::getUuid, uuid)
                        .set(Apply::getIsDelete, 0));
        restoreBalanceIfNeeded(entity);
    }

    private void restoreBalanceIfNeeded(Apply entity) {
        if (entity == null || entity.getLength() == null
                || entity.getLength().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        if (Integer.valueOf(1).equals(entity.getType())) {
            leaveBalanceService.restoreAnnual(entity.getApplyUserUuid(), entity.getLength());
        } else if (Integer.valueOf(4).equals(entity.getType())) {
            leaveBalanceService.restoreComp(entity.getApplyUserUuid(), entity.getLength());
        }
    }

    @Override
    public BigDecimal calculateLength(LocalDateTime startTime, LocalDateTime endTime, String ruleUuid) {
        RuleDTO rule = ruleService.getByUuid(ruleUuid);
        if (rule == null) {
            throw new RuntimeException("考勤规则不存在");
        }

        long dailyMinutes = Duration.between(rule.getStartTime(), rule.getEndTime()).toMinutes();
        if (Integer.valueOf(1).equals(rule.getMiddleRest()) && rule.getMiddleStart() != null && rule.getMiddleEnd() != null) {
            dailyMinutes -= Duration.between(rule.getMiddleStart(), rule.getMiddleEnd()).toMinutes();
        }

        BigDecimal totalHours;
        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();

        if (startDate.equals(endDate)) {
            long minutes = Duration.between(startTime, endTime).toMinutes();
            if (Integer.valueOf(1).equals(rule.getMiddleRest()) && rule.getMiddleStart() != null && rule.getMiddleEnd() != null) {
                LocalDate date = startDate;
                LocalDateTime lunchStart = LocalDateTime.of(date, rule.getMiddleStart());
                LocalDateTime lunchEnd = LocalDateTime.of(date, rule.getMiddleEnd());
                LocalDateTime lunchOverlapStart = startTime.isBefore(lunchStart) ? lunchStart : startTime;
                LocalDateTime lunchOverlapEnd = endTime.isAfter(lunchEnd) ? lunchEnd : endTime;
                if (lunchOverlapStart.isBefore(lunchOverlapEnd)) {
                    minutes -= Duration.between(lunchOverlapStart, lunchOverlapEnd).toMinutes();
                }
            }
            totalHours = BigDecimal.valueOf(minutes)
                    .divide(BigDecimal.valueOf(60), 10, RoundingMode.HALF_UP);
        } else {
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            totalHours = BigDecimal.valueOf(dailyMinutes * days)
                    .divide(BigDecimal.valueOf(60), 10, RoundingMode.HALF_UP);
        }

        if (rule.getAccuracy() != null) {
            totalHours = totalHours.divide(rule.getAccuracy(), 0, RoundingMode.CEILING)
                    .multiply(rule.getAccuracy());
        }

        return totalHours;
    }
}
