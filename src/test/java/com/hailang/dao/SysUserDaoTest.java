package com.hailang.dao;

import com.hailang.entity.SysUser;
import org.apache.commons.codec.digest.DigestUtils;
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
class SysUserDaoTest {

    @Autowired
    private SysUserDao sysUserDao;

    private String testUuid;

    @BeforeEach
    void setUp() {
        sysUserDao.delete(null);

        SysUser user = new SysUser();
        testUuid = UUID.randomUUID().toString().replace("-", "");
        user.setUuid(testUuid);
        user.setAccount("test_" + System.currentTimeMillis());
        user.setName("测试用户");
        user.setPassword(DigestUtils.md5Hex("123456"));
        user.setGender(1);
        sysUserDao.insert(user);
    }

    @Test
    void testSelectList() {
        List<SysUser> list = sysUserDao.selectList(null);
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void testSelectByUuid() {
        SysUser user = sysUserDao.selectByUuid(testUuid);
        assertNotNull(user);
        assertEquals(testUuid, user.getUuid());
    }

    @Test
    void testInsert() {
        SysUser user = new SysUser();
        user.setUuid(UUID.randomUUID().toString().replace("-", ""));
        user.setAccount("new_" + System.currentTimeMillis());
        user.setName("新用户");
        user.setPassword(DigestUtils.md5Hex("123456"));
        user.setGender(1);
        int rows = sysUserDao.insert(user);
        assertEquals(1, rows);
        assertNotNull(user.getId());

        List<SysUser> list = sysUserDao.selectList(null);
        assertEquals(2, list.size());
    }
}
