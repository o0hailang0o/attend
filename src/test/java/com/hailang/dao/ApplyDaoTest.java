package com.hailang.dao;

import com.hailang.entity.Apply;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ApplyDaoTest {

    @Autowired
    private ApplyDao applyDao;

    private String testUuid;

    @BeforeEach
    void setUp() {
        applyDao.delete(null);

        Apply apply = new Apply();
        testUuid = UUID.randomUUID().toString().replace("-", "");
        apply.setUuid(testUuid);
        apply.setMonth(LocalDateTime.now());
        apply.setType(1);
        apply.setLengthType(1);
        apply.setStartTime(LocalDateTime.now());
        apply.setEndTime(LocalDateTime.now().plusHours(8));
        apply.setLength(BigDecimal.valueOf(8));
        apply.setLeaderId("leader_uuid");
        apply.setReject("");
        apply.setStatus(1);
        apply.setCreateTime(LocalDateTime.now());
        applyDao.insert(apply);
    }

    @Test
    void testSelectList() {
        List<Apply> list = applyDao.selectList(null);
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void testInsert() {
        Apply apply = new Apply();
        apply.setUuid(UUID.randomUUID().toString().replace("-", ""));
        apply.setMonth(LocalDateTime.now());
        apply.setType(1);
        apply.setLengthType(1);
        apply.setStartTime(LocalDateTime.now());
        apply.setEndTime(LocalDateTime.now().plusHours(8));
        apply.setLength(BigDecimal.valueOf(8));
        apply.setLeaderId("leader_uuid");
        apply.setReject("");
        apply.setStatus(1);
        apply.setCreateTime(LocalDateTime.now());
        int rows = applyDao.insert(apply);
        assertEquals(1, rows);
        assertNotNull(apply.getId());

        List<Apply> list = applyDao.selectList(null);
        assertEquals(2, list.size());
    }
}
