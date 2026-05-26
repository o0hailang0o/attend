package com.hailang.service;

import com.hailang.service.dto.LeaveBalanceDTO;

import java.math.BigDecimal;

public interface LeaveBalanceService {
    LeaveBalanceDTO getCurrent();
    LeaveBalanceDTO getByAccount(String account);
    LeaveBalanceDTO getByUserUuid(String userUuid);
    void deductAnnual(String userUuid, BigDecimal hours);
    void deductComp(String userUuid, BigDecimal hours);
    void restoreAnnual(String userUuid, BigDecimal hours);
    void restoreComp(String userUuid, BigDecimal hours);
}
