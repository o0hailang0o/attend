package com.hailang.dao;

import com.hailang.entity.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RuleDaoTest {

    @Autowired
    private RuleDao ruleDao;

    private String testUuid;

    @BeforeEach
    void setUp() {
        ruleDao.delete(null);

        Rule rule = new Rule();
        testUuid = UUID.randomUUID().toString().replace("-", "");
        rule.setUuid(testUuid);
        rule.setName("标准规则");
        rule.setStartTime(LocalTime.of(8, 0));
        rule.setEndTime(LocalTime.of(17, 0));
        rule.setFlexibility(2);
        rule.setMiddleRest(1);
        rule.setMiddleStart(LocalTime.of(12, 0));
        rule.setMiddleEnd(LocalTime.of(13, 0));
        rule.setVacation(1);
        rule.setComp(1);
        rule.setAccuracy(BigDecimal.valueOf(0.5));
        ruleDao.insert(rule);
    }

    @Test
    void testSelectList() {
        List<Rule> list = ruleDao.selectList(null);
        assertNotNull(list);
        assertEquals(1, list.size());

        Rule r = list.get(0);
        assertEquals("标准规则", r.getName());
        assertEquals(LocalTime.of(8, 0), r.getStartTime());
        assertEquals(LocalTime.of(17, 0), r.getEndTime());
        assertEquals(2, r.getFlexibility());
        assertEquals(1, r.getMiddleRest());
        assertEquals(LocalTime.of(12, 0), r.getMiddleStart());
        assertEquals(LocalTime.of(13, 0), r.getMiddleEnd());
        assertEquals(1, r.getVacation());
        assertEquals(1, r.getComp());
        assertEquals(0, BigDecimal.valueOf(0.5).compareTo(r.getAccuracy()));
    }

    @Test
    void testInsert() {
        Rule rule = new Rule();
        rule.setUuid(UUID.randomUUID().toString().replace("-", ""));
        rule.setName("新规则");
        rule.setStartTime(LocalTime.of(9, 0));
        rule.setEndTime(LocalTime.of(18, 0));
        rule.setFlexibility(1);
        rule.setMiddleRest(0);
        rule.setVacation(0);
        rule.setComp(0);
        rule.setAccuracy(BigDecimal.valueOf(1));
        int rows = ruleDao.insert(rule);
        assertEquals(1, rows);
        assertNotNull(rule.getId());

        List<Rule> list = ruleDao.selectList(null);
        assertEquals(2, list.size());
    }

    @Test
    void testSelectByUuid() {
        Rule r = ruleDao.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Rule>()
                        .eq(Rule::getUuid, testUuid));
        assertNotNull(r);
        assertEquals(testUuid, r.getUuid());
    }
}
