package com.zxl.sb.cache.quartzdemo.mapper;

import com.zxl.sb.cache.quartzdemo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    int saveUser(User user);
}
