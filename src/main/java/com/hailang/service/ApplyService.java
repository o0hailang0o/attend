package com.hailang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hailang.service.dto.ApplyDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ApplyService {
    void submit(ApplyDTO dto);
    void update(ApplyDTO dto);
    void remove(String uuid);
    ApplyDTO getByUuid(String uuid);
    IPage<ApplyDTO> listByUser(String userUuid, LocalDateTime month, int page, int size);
    void cancel(String uuid);
    BigDecimal calculateLength(LocalDateTime startTime, LocalDateTime endTime, String ruleUuid);
}
