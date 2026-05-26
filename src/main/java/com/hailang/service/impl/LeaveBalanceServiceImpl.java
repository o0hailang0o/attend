package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hailang.config.AuthContext;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.LeaveBalanceDao;
import com.hailang.dao.SysUserDao;
import com.hailang.entity.LeaveBalance;
import com.hailang.entity.SysUser;
import com.hailang.service.LeaveBalanceService;
import com.hailang.service.dto.LeaveBalanceDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceDao leaveBalanceDao;
    private final SysUserDao sysUserDao;

    public LeaveBalanceServiceImpl(LeaveBalanceDao leaveBalanceDao, SysUserDao sysUserDao) {
        this.leaveBalanceDao = leaveBalanceDao;
        this.sysUserDao = sysUserDao;
    }

    @Override
    public LeaveBalanceDTO getCurrent() {
        SysUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("未登录");
        }
        LeaveBalance entity = leaveBalanceDao.selectOne(
                new LambdaQueryWrapper<LeaveBalance>()
                        .eq(LeaveBalance::getUserUuid, currentUser.getUuid())
                        .eq(LeaveBalance::getYear, LocalDate.now().getYear())
                        .eq(LeaveBalance::getIsDelete, 1));
        if (entity == null) {
            return null;
        }
        return BeanUtils.copy(entity, LeaveBalanceDTO.class);
    }

    @Override
    public LeaveBalanceDTO getByAccount(String account) {
        SysUser user = sysUserDao.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getAccount, account)
                        .eq(SysUser::getIsDelete, 1));
        if (user == null) {
            return null;
        }
        LeaveBalance entity = leaveBalanceDao.selectOne(
                new LambdaQueryWrapper<LeaveBalance>()
                        .eq(LeaveBalance::getUserUuid, user.getUuid())
                        .eq(LeaveBalance::getYear, LocalDate.now().getYear())
                        .eq(LeaveBalance::getIsDelete, 1));
        if (entity == null) {
            return null;
        }
        return BeanUtils.copy(entity, LeaveBalanceDTO.class);
    }

    @Override
    public LeaveBalanceDTO getByUserUuid(String userUuid) {
        LeaveBalance entity = leaveBalanceDao.selectOne(
                new LambdaQueryWrapper<LeaveBalance>()
                        .eq(LeaveBalance::getUserUuid, userUuid)
                        .eq(LeaveBalance::getYear, LocalDate.now().getYear())
                        .eq(LeaveBalance::getIsDelete, 1));
        if (entity == null) {
            return null;
        }
        return BeanUtils.copy(entity, LeaveBalanceDTO.class);
    }

    @Override
    public void deductAnnual(String userUuid, BigDecimal hours) {
        LeaveBalance entity = leaveBalanceDao.selectOne(
                new LambdaQueryWrapper<LeaveBalance>()
                        .eq(LeaveBalance::getUserUuid, userUuid)
                        .eq(LeaveBalance::getYear, LocalDate.now().getYear())
                        .eq(LeaveBalance::getIsDelete, 1));
        if (entity == null || entity.getAnnualRemainingHours().compareTo(hours) < 0) {
            throw new RuntimeException("年假余额不足");
        }
        leaveBalanceDao.update(null,
                Wrappers.<LeaveBalance>lambdaUpdate()
                        .eq(LeaveBalance::getUuid, entity.getUuid())
                        .set(LeaveBalance::getAnnualRemainingHours, entity.getAnnualRemainingHours().subtract(hours)));
    }

    @Override
    public void deductComp(String userUuid, BigDecimal hours) {
        LeaveBalance entity = leaveBalanceDao.selectOne(
                new LambdaQueryWrapper<LeaveBalance>()
                        .eq(LeaveBalance::getUserUuid, userUuid)
                        .eq(LeaveBalance::getYear, LocalDate.now().getYear())
                        .eq(LeaveBalance::getIsDelete, 1));
        if (entity == null || entity.getCompRemainingHours().compareTo(hours) < 0) {
            throw new RuntimeException("调休假余额不足");
        }
        leaveBalanceDao.update(null,
                Wrappers.<LeaveBalance>lambdaUpdate()
                        .eq(LeaveBalance::getUuid, entity.getUuid())
                        .set(LeaveBalance::getCompRemainingHours, entity.getCompRemainingHours().subtract(hours)));
    }

    @Override
    public void restoreAnnual(String userUuid, BigDecimal hours) {
        LeaveBalance entity = leaveBalanceDao.selectOne(
                new LambdaQueryWrapper<LeaveBalance>()
                        .eq(LeaveBalance::getUserUuid, userUuid)
                        .eq(LeaveBalance::getYear, LocalDate.now().getYear())
                        .eq(LeaveBalance::getIsDelete, 1));
        if (entity == null) {
            return;
        }
        leaveBalanceDao.update(null,
                Wrappers.<LeaveBalance>lambdaUpdate()
                        .eq(LeaveBalance::getUuid, entity.getUuid())
                        .set(LeaveBalance::getAnnualRemainingHours, entity.getAnnualRemainingHours().add(hours)));
    }

    @Override
    public void restoreComp(String userUuid, BigDecimal hours) {
        LeaveBalance entity = leaveBalanceDao.selectOne(
                new LambdaQueryWrapper<LeaveBalance>()
                        .eq(LeaveBalance::getUserUuid, userUuid)
                        .eq(LeaveBalance::getYear, LocalDate.now().getYear())
                        .eq(LeaveBalance::getIsDelete, 1));
        if (entity == null) {
            return;
        }
        leaveBalanceDao.update(null,
                Wrappers.<LeaveBalance>lambdaUpdate()
                        .eq(LeaveBalance::getUuid, entity.getUuid())
                        .set(LeaveBalance::getCompRemainingHours, entity.getCompRemainingHours().add(hours)));
    }
}
