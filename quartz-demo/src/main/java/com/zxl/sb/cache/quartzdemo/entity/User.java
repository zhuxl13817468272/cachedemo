package com.zxl.sb.cache.quartzdemo.entity;

import lombok.Data;

@Data
public class User {

    private Integer id;
    private String name;
    private Integer age;

    public User(){}
    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
