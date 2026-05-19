package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.RuleDao;
import com.hailang.entity.Rule;
import com.hailang.service.RuleService;
import com.hailang.service.dto.RuleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {

    private final RuleDao ruleDao;

    @Override
    public List<RuleDTO> list() {
        return ruleDao.selectList(Wrappers.<Rule>lambdaQuery().eq(Rule::getIsDelete, 1)).stream()
                .map(rule -> BeanUtils.copy(rule, RuleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public RuleDTO getByUuid(String uuid) {
        Rule rule = ruleDao.selectByUuid(uuid);
        return rule == null ? null : BeanUtils.copy(rule, RuleDTO.class);
    }

    @Override
    public RuleDTO save(RuleDTO dto) {
        Rule rule = BeanUtils.copy(dto, Rule.class);
        rule.setUuid(UUID.randomUUID().toString().replace("-", ""));
        rule.setIsDelete(1);
        validate(rule);
        ruleDao.insert(rule);
        return BeanUtils.copy(rule, RuleDTO.class);
    }

    @Override
    public RuleDTO update(RuleDTO dto) {
        Rule rule = BeanUtils.copy(dto, Rule.class);
        validate(rule);
        ruleDao.update(rule, new LambdaQueryWrapper<Rule>().eq(Rule::getUuid, rule.getUuid()));
        return BeanUtils.copy(rule, RuleDTO.class);
    }

    @Override
    public boolean removeByUuid(String uuid) {
        return ruleDao.update(null,
                Wrappers.<Rule>lambdaUpdate()
                        .eq(Rule::getUuid, uuid)
                        .set(Rule::getIsDelete, 0)) > 0;
    }

    private void validate(Rule rule) {
        if (rule.getStartTime() == null) {
            throw new RuntimeException("上班时间不能为空");
        }
        if (rule.getEndTime() == null) {
            throw new RuntimeException("下班时间不能为空");
        }
        if (!rule.getStartTime().isBefore(rule.getEndTime())) {
            throw new RuntimeException("上班时间必须小于下班时间");
        }

        if (Integer.valueOf(1).equals(rule.getMiddleRest())) {
            if (rule.getMiddleStart() == null) {
                throw new RuntimeException("午休开始时间不能为空");
            }
            if (rule.getMiddleEnd() == null) {
                throw new RuntimeException("午休结束时间不能为空");
            }
            if (!rule.getStartTime().isBefore(rule.getMiddleStart())) {
                throw new RuntimeException("上班时间必须小于午休开始时间");
            }
            if (!rule.getMiddleStart().isBefore(rule.getMiddleEnd())) {
                throw new RuntimeException("午休开始时间必须小于午休结束时间");
            }
            if (!rule.getMiddleEnd().isBefore(rule.getEndTime())) {
                throw new RuntimeException("午休结束时间必须小于下班时间");
            }
        }

        if (rule.getFlexibility() != null && rule.getFlexibility() < 0) {
            throw new RuntimeException("弹性时间不能为负数");
        }

        if (rule.getAccuracy() != null) {
            BigDecimal acc = rule.getAccuracy();
            if (acc.compareTo(BigDecimal.valueOf(0.5)) != 0 && acc.compareTo(BigDecimal.valueOf(1)) != 0) {
                throw new RuntimeException("精确度只能为0.5或1");
            }
        }
    }
}
