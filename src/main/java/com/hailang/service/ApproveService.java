package com.hailang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hailang.service.dto.ApproveDTO;

public interface ApproveService {
    IPage<ApproveDTO> listByApprover(String leaderUuid, int page, int size);
    void pass(String approveUuid);
    void reject(String approveUuid, String reject);
}
