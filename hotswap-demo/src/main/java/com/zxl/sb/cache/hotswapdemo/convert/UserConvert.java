package com.zxl.sb.cache.hotswapdemo.convert;

import com.zxl.sb.cache.hotswapdemo.entity.User;
import com.zxl.sb.cache.hotswapdemo.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper // 声明它是一个 MapStruct Mapper 映射器
public interface UserConvert {
    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class); // 获得 MapStruct 帮我们自动生成的 UserConvert 实现类的对象。

    @Mappings({
            @Mapping(source = "name",target = "username")
    })
    UserVo convert(User user);
}
