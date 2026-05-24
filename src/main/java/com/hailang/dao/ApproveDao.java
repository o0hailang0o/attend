package com.hailang.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hailang.controller.resp.ApproveApplyResp;
import com.hailang.controller.resp.WorkflowStepResp;
import com.hailang.entity.Approve;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApproveDao extends BaseMapper<Approve> {
    IPage<ApproveApplyResp> selectApproveApplyPage(IPage<ApproveApplyResp> page, String leaderUuid);

    List<WorkflowStepResp> selectWorkflowByApplyUuids(@Param("applyUuids") List<String> applyUuids);
}
