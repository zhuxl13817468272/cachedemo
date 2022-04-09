package com.zxl.sb.cache.cacheredis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxl.sb.cache.cacheredis.entity.UserDO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "users")
public interface UserMapper extends BaseMapper<UserDO> {

    @Cacheable(key = "#a0")
    UserDO selectById(Integer id);

    @CachePut(key = "#user.id")
    default UserDO insert0(UserDO user) {
        // 插入记录
        this.insert(user);
        // 返回用户
        return user;
    }

    @CacheEvict(key = "#a0")
    int deleteById(Integer id);

}