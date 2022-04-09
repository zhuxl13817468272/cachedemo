package com.zxl.sb.springdemo.mybatis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// 定义一个@MynScan，用来在启动Spring时执行MyImportBeanDefinitionRegistrar的逻辑，并指定包路径
@Retention(RetentionPolicy.RUNTIME)
@Import(MyImportBeanDefinitionRegistrar.class)
public @interface MyScan {
}
