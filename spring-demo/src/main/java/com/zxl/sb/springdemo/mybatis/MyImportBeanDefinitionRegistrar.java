package com.zxl.sb.springdemo.mybatis;

import com.zxl.sb.springdemo.mybatis.Mapper.UserMapper;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

// 定义一个MyImportBeanDefinitionRegistrar，用来生成不同Mapper对象的MyFactoryBean
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    /**
     *  可以通过AnnotationMetadata获取到对应的@MyScan注解，如果@MyScan有指定待扫描的包路径，那么就会扫描到。
     *  获取到所设置的包路径，然后扫描该路径下的所有Mapper，生成BeanDefinition，放入Spring容器中。
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
         BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
         AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
         beanDefinition.setBeanClass(MyFactoryBean.class);
         beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(UserMapper.class);
         // 添加beanDefinition
         registry.registerBeanDefinition("my"+ UserMapper.class.getSimpleName(), beanDefinition);
    }

}
