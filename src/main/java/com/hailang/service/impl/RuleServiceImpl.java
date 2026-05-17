package com.hailang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hailang.dao.RuleDao;
import com.hailang.entity.Rule;
import com.hailang.service.RuleService;
import org.springframework.stereotype.Service;

@Service
public class RuleServiceImpl extends ServiceImpl<RuleDao, Rule> implements RuleService {
}
