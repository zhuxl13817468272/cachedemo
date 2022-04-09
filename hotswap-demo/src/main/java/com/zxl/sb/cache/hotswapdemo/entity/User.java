package com.zxl.sb.cache.hotswapdemo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
    private Integer id;
    private String name;
    private String password;
}
