package com.hailang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hailang.dao.RuleDao;
import com.hailang.entity.Rule;
import com.hailang.service.RuleService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RuleServiceImpl extends ServiceImpl<RuleDao, Rule> implements RuleService {

    @Override
    public boolean save(Rule rule) {
        validate(rule);
        return super.save(rule);
    }

    @Override
    public boolean updateById(Rule rule) {
        validate(rule);
        return super.updateById(rule);
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
