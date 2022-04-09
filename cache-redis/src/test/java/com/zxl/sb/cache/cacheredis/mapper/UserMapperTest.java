package com.zxl.sb.cache.cacheredis.mapper;

import com.zxl.sb.cache.cacheredis.CacheRedisApplication;
import com.zxl.sb.cache.cacheredis.entity.UserDO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CacheRedisApplication.class)
public class UserMapperTest {
    private static final String CACHE_NAME_USER = "users";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CacheManager cacheManager;

    // UserMapperTest.java

    @Test
    public void testCacheManager() {
        System.out.println(cacheManager);
    }

    @Test
    public void selectById() {
        Integer id = 1;

        UserDO user = userMapper.selectById(1);
        System.out.println("user: "+ user);
        Assert.assertNotNull("缓存为空", cacheManager.getCache(CACHE_NAME_USER).get(user.getId(), UserDO.class));

        user = userMapper.selectById(id);
        System.out.println("user: "+ user);
    }

    @Test
    public void insert0() {
    }

    @Test
    public void deleteById() {
        UserDO user = new UserDO();
        user.setUsername(UUID.randomUUID().toString()); // 随机账号，因为唯一索引
        user.setPassword("nicai");
        user.setCreateTime(new Date());
        user.setDeleted(0);

        UserDO userDO = userMapper.insert0(user);

        Assert.assertNotNull("缓存不为空",cacheManager.getCache(CACHE_NAME_USER).get(userDO.getId(),UserDO.class));

//        userMapper.deleteById(userDO.getId());



    }
}