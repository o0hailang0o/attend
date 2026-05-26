package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hailang.config.AuthContext;
import com.hailang.config.utils.BeanUtils;
import com.hailang.controller.resp.ApproveApplyResp;
import com.hailang.controller.resp.WorkflowStepResp;
import com.hailang.dao.ApplyDao;
import com.hailang.dao.ApproveDao;
import com.hailang.entity.Apply;
import com.hailang.entity.Approve;
import com.hailang.entity.SysUser;
import com.hailang.service.ApproveService;
import com.hailang.service.DailyAttendanceService;
import com.hailang.service.LeaveBalanceService;
import com.hailang.service.dto.ApproveDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApproveServiceImpl extends ServiceImpl<ApproveDao, Approve> implements ApproveService {

    private final ApplyDao applyDao;
    private final LeaveBalanceService leaveBalanceService;
    private final DailyAttendanceService dailyAttendanceService;

    public ApproveServiceImpl(ApplyDao applyDao, LeaveBalanceService leaveBalanceService,
                              DailyAttendanceService dailyAttendanceService) {
        this.applyDao = applyDao;
        this.leaveBalanceService = leaveBalanceService;
        this.dailyAttendanceService = dailyAttendanceService;
    }

    @Override
    public IPage<ApproveApplyResp> listMyApprove(int page, int size) {
        SysUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("未登录");
        }
        Page<ApproveApplyResp> pageParam = new Page<>(page, size);
        IPage<ApproveApplyResp> result = baseMapper.selectApproveApplyPage(pageParam, currentUser.getUuid());

        List<String> applyUuids = result.getRecords().stream()
                .map(ApproveApplyResp::getApplyUuid)
                .collect(Collectors.toList());
        if (!applyUuids.isEmpty()) {
            List<WorkflowStepResp> steps = baseMapper.selectWorkflowByApplyUuids(applyUuids);
            Map<String, List<WorkflowStepResp>> workflowMap = steps.stream()
                    .collect(Collectors.groupingBy(WorkflowStepResp::getApplyUuid));
            result.getRecords().forEach(r -> r.setWorkflow(workflowMap.get(r.getApplyUuid())));
        }

        return result;
    }

    @Override
    public IPage<ApproveDTO> listByApprover(String leaderUuid, int page, int size) {
        Page<Approve> pageParam = new Page<>(page, size);
        Page<Approve> result = baseMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Approve>()
                        .eq(Approve::getLeaderUuid, leaderUuid)
                        .eq(Approve::getIsDelete, 1)
                        .eq(Approve::getStatus, 4)
                        .orderByDesc(Approve::getCreateTime));
        return result.convert(item -> BeanUtils.copy(item, ApproveDTO.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pass(String approveUuid) {
        Approve approve = baseMapper.selectOne(
                new LambdaQueryWrapper<Approve>()
                        .eq(Approve::getUuid, approveUuid)
                        .eq(Approve::getIsDelete, 1));
        if (approve == null) {
            throw new RuntimeException("审批记录不存在");
        }
        if (approve.getStatus() != 4) {
            throw new RuntimeException("审批已处理");
        }

        baseMapper.update(null,
                Wrappers.<Approve>lambdaUpdate()
                        .eq(Approve::getUuid, approveUuid)
                        .set(Approve::getStatus, 1));

        Approve next = baseMapper.selectOne(
                new LambdaQueryWrapper<Approve>()
                        .eq(Approve::getApplyUuid, approve.getApplyUuid())
                        .eq(Approve::getOrder, approve.getOrder() + 1)
                        .eq(Approve::getIsDelete, 1));

        if (next != null) {
            baseMapper.update(null,
                    Wrappers.<Approve>lambdaUpdate()
                            .eq(Approve::getUuid, next.getUuid())
                            .set(Approve::getStatus, 4));
            applyDao.update(null,
                    Wrappers.<Apply>lambdaUpdate()
                            .eq(Apply::getUuid, approve.getApplyUuid())
                            .set(Apply::getStatus, 5));
        } else {
            applyDao.update(null,
                    Wrappers.<Apply>lambdaUpdate()
                            .eq(Apply::getUuid, approve.getApplyUuid())
                            .set(Apply::getStatus, 9));

            Apply apply = applyDao.selectOne(
                    new LambdaQueryWrapper<Apply>()
                            .eq(Apply::getUuid, approve.getApplyUuid())
                            .eq(Apply::getIsDelete, 1));
            if (apply != null && apply.getStartTime() != null && apply.getEndTime() != null
                    && apply.getApplyUserUuid() != null) {
                LocalDate startDate = apply.getStartTime().toLocalDate();
                LocalDate endDate = apply.getEndTime().toLocalDate();
                dailyAttendanceService.calculate(startDate, endDate, apply.getApplyUserUuid());
            }
        }
    }

    @Override
    public void reject(String approveUuid, String reject) {
        Approve approve = baseMapper.selectOne(
                new LambdaQueryWrapper<Approve>()
                        .eq(Approve::getUuid, approveUuid)
                        .eq(Approve::getIsDelete, 1));
        if (approve == null) {
            throw new RuntimeException("审批记录不存在");
        }
        if (approve.getStatus() != 4) {
            throw new RuntimeException("审批已处理");
        }

        baseMapper.update(null,
                Wrappers.<Approve>lambdaUpdate()
                        .eq(Approve::getUuid, approveUuid)
                        .set(Approve::getStatus, 3)
                        .set(Approve::getReject, reject));

        baseMapper.update(null,
                Wrappers.<Approve>lambdaUpdate()
                        .eq(Approve::getApplyUuid, approve.getApplyUuid())
                        .in(Approve::getStatus, 4, 5)
                        .ne(Approve::getUuid, approveUuid)
                        .set(Approve::getStatus, 3));

        applyDao.update(null,
                Wrappers.<Apply>lambdaUpdate()
                        .eq(Apply::getUuid, approve.getApplyUuid())
                        .set(Apply::getStatus, 3)
                        .set(Apply::getReject, reject));
    }
}
