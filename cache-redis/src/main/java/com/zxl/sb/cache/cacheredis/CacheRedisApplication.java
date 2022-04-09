package com.zxl.sb.cache.cacheredis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan(basePackages = "com.zxl.sb.cache.cacheredis.mapper")
public class CacheRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheRedisApplication.class, args);
    }

}
