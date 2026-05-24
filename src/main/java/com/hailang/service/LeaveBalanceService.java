package com.hailang.service;

import com.hailang.service.dto.LeaveBalanceDTO;

import java.math.BigDecimal;

public interface LeaveBalanceService {
    LeaveBalanceDTO getCurrent();
    LeaveBalanceDTO getByAccount(String account);
    void deductAnnual(String userUuid, BigDecimal hours);
    void deductComp(String userUuid, BigDecimal hours);
}
