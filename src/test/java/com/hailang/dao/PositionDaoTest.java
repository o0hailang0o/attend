package com.hailang.dao;

import com.hailang.entity.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PositionDaoTest {

    @Autowired
    private PositionDao positionDao;

    private String testUuid;

    @BeforeEach
    void setUp() {
        positionDao.delete(null);

        Position position = new Position();
        testUuid = UUID.randomUUID().toString().replace("-", "");
        position.setUuid(testUuid);
        position.setName("测试职位");
        positionDao.insert(position);
    }

    @Test
    void testSelectList() {
        List<Position> list = positionDao.selectList(null);
        assertNotNull(list);
        assertEquals(1, list.size());

        Position p = list.get(0);
        assertEquals("测试职位", p.getName());
    }

    @Test
    void testInsert() {
        Position position = new Position();
        position.setUuid(UUID.randomUUID().toString().replace("-", ""));
        position.setName("新职位");
        int rows = positionDao.insert(position);
        assertEquals(1, rows);
        assertNotNull(position.getId());

        List<Position> list = positionDao.selectList(null);
        assertEquals(2, list.size());
    }

    @Test
    void testSelectByUuid() {
        Position p = positionDao.selectByUuid(testUuid);
        assertNotNull(p);
        assertEquals(testUuid, p.getUuid());
    }
}
