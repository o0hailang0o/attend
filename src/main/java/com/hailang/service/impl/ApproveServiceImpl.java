package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.ApplyDao;
import com.hailang.dao.ApproveDao;
import com.hailang.entity.Apply;
import com.hailang.entity.Approve;
import com.hailang.service.ApproveService;
import com.hailang.service.dto.ApproveDTO;
import org.springframework.stereotype.Service;

@Service
public class ApproveServiceImpl extends ServiceImpl<ApproveDao, Approve> implements ApproveService {

    private final ApplyDao applyDao;

    public ApproveServiceImpl(ApplyDao applyDao) {
        this.applyDao = applyDao;
    }

    @Override
    public IPage<ApproveDTO> listByApprover(String leaderId, int page, int size) {
        Page<Approve> pageParam = new Page<>(page, size);
        Page<Approve> result = baseMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Approve>()
                        .eq(Approve::getLeaderId, leaderId)
                        .eq(Approve::getIsDelete, 1)
                        .eq(Approve::getStatus, 0)
                        .orderByDesc(Approve::getCreateTime));
        return result.convert(item -> BeanUtils.copy(item, ApproveDTO.class));
    }

    @Override
    public void pass(String approveUuid) {
        Approve approve = baseMapper.selectOne(
                new LambdaQueryWrapper<Approve>()
                        .eq(Approve::getUuid, approveUuid)
                        .eq(Approve::getIsDelete, 1));
        if (approve == null) {
            throw new RuntimeException("审批记录不存在");
        }
        if (approve.getStatus() != 0) {
            throw new RuntimeException("审批已处理");
        }

        baseMapper.update(null,
                Wrappers.<Approve>lambdaUpdate()
                        .eq(Approve::getUuid, approveUuid)
                        .set(Approve::getStatus, 1));

        applyDao.update(null,
                Wrappers.<Apply>lambdaUpdate()
                        .eq(Apply::getUuid, approve.getApplyUuid())
                        .set(Apply::getStatus, 2));
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
        if (approve.getStatus() != 0) {
            throw new RuntimeException("审批已处理");
        }

        baseMapper.update(null,
                Wrappers.<Approve>lambdaUpdate()
                        .eq(Approve::getUuid, approveUuid)
                        .set(Approve::getStatus, 3)
                        .set(Approve::getReject, reject));

        applyDao.update(null,
                Wrappers.<Apply>lambdaUpdate()
                        .eq(Apply::getUuid, approve.getApplyUuid())
                        .set(Apply::getStatus, 9)
                        .set(Apply::getReject, reject));
    }
}
