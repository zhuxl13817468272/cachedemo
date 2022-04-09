package com.zxl.sb.springdemo.mybatis;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

// 定义一个MyFactoryBean，用来将Mybatis的代理对象生成一个bean对象
@Component
public class MyFactoryBean implements FactoryBean {

    private Class mapperInterface;
    public MyFactoryBean(Class mapperInterface) {
         this.mapperInterface = mapperInterface;
    }

    @Override
    public Object getObject() throws Exception {
        Object proxyInstance = Proxy.newProxyInstance(MyFactoryBean.class.getClassLoader(), new Class[]{mapperInterface}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                if(Object.class.equals(method.getDeclaringClass())){
                    return method.invoke(this,args);
                }else {
                    // 执行代理逻辑
                    return null;
                }
            }
        });
        return proxyInstance;
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }
}
