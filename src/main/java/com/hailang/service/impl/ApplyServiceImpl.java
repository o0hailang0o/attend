package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.ApplyDao;
import com.hailang.entity.Apply;
import com.hailang.service.ApplyService;
import com.hailang.service.RuleService;
import com.hailang.service.dto.ApplyDTO;
import com.hailang.service.dto.RuleDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ApplyServiceImpl extends ServiceImpl<ApplyDao, Apply> implements ApplyService {

    private final RuleService ruleService;

    public ApplyServiceImpl(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @Override
    public void submit(ApplyDTO dto) {
        Apply entity = BeanUtils.copy(dto, Apply.class);
        entity.setUuid(UUID.randomUUID().toString().replace("-", ""));
        entity.setStatus(1);
        entity.setIsDelete(1);
        baseMapper.insert(entity);
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
    public IPage<ApplyDTO> listByUser(String userUuid, int page, int size) {
        Page<Apply> pageParam = new Page<>(page, size);
        Page<Apply> result = baseMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Apply>()
                        .eq(Apply::getLeaderId, userUuid)
                        .eq(Apply::getIsDelete, 1)
                        .orderByDesc(Apply::getCreateTime)
        );
        return result.convert(item -> BeanUtils.copy(item, ApplyDTO.class));
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
                        .set(Apply::getLength, dto.getLength()));
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
        if (entity.getStatus() != 1) {
            throw new RuntimeException("仅待审批状态的申请可以删除");
        }
        baseMapper.update(null,
                Wrappers.<Apply>lambdaUpdate()
                        .eq(Apply::getUuid, uuid)
                        .set(Apply::getIsDelete, 0));
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
                        .set(Apply::getStatus, 3));
    }

    @Override
    public BigDecimal calculateLength(LocalDateTime startTime, LocalDateTime endTime, String ruleUuid) {
        RuleDTO rule = ruleService.getByUuid(ruleUuid);
        if (rule == null) {
            throw new RuntimeException("考勤规则不存在");
        }

        BigDecimal totalHours = BigDecimal.ZERO;
        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime dayStart = LocalDateTime.of(date, rule.getStartTime());
            LocalDateTime dayEnd = LocalDateTime.of(date, rule.getEndTime());

            LocalDateTime leaveStart = date.equals(startDate) ? startTime : dayStart;
            LocalDateTime leaveEnd = date.equals(endDate) ? endTime : dayEnd;

            LocalDateTime overlapStart = leaveStart.isBefore(dayStart) ? dayStart : leaveStart;
            LocalDateTime overlapEnd = leaveEnd.isAfter(dayEnd) ? dayEnd : leaveEnd;

            if (!overlapStart.isBefore(overlapEnd)) {
                continue;
            }

            long minutes = Duration.between(overlapStart, overlapEnd).toMinutes();

            if (Integer.valueOf(1).equals(rule.getMiddleRest())) {
                LocalDateTime lunchStart = LocalDateTime.of(date, rule.getMiddleStart());
                LocalDateTime lunchEnd = LocalDateTime.of(date, rule.getMiddleEnd());
                LocalDateTime lunchOverlapStart = overlapStart.isBefore(lunchStart) ? lunchStart : overlapStart;
                LocalDateTime lunchOverlapEnd = overlapEnd.isAfter(lunchEnd) ? lunchEnd : overlapEnd;
                if (lunchOverlapStart.isBefore(lunchOverlapEnd)) {
                    minutes -= Duration.between(lunchOverlapStart, lunchOverlapEnd).toMinutes();
                }
            }

            totalHours = totalHours.add(BigDecimal.valueOf(minutes)
                    .divide(BigDecimal.valueOf(60), 10, RoundingMode.HALF_UP));
        }

        if (rule.getAccuracy() != null) {
            totalHours = totalHours.divide(rule.getAccuracy(), 0, RoundingMode.FLOOR)
                    .multiply(rule.getAccuracy());
        }

        return totalHours;
    }
}
