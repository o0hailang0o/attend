package com.hailang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hailang.service.dto.ApplyDTO;

public interface ApplyService {
    void submit(ApplyDTO dto);
    IPage<ApplyDTO> listByUser(String userUuid, int page, int size);
}
