package com.zxl.sb.springdemo.others;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component //AnnotatedGenericBeanDefinition
@Import(value = {Person.class,Car.class})
@PropertySource(value = {"classpath:person.properties"})
public class Car {
//    @Bean(initMethod = "init",destroyMethod = "destory")
    @Bean // 在不指定@Scope情况下，所有Bean都是单实例的Bean,而且是饿汉加载（容器启动实例就创建好了）
    @Scope(value = "prototype") // 指定@Scope为prototype表示是多例的，而且是懒汉模式加载（IOC容器启动的时候，并不会创建对象，而是在第一次使用的时候才会创建）
    public Person person(){
        return new Person();
    }

    @Bean
    @Lazy //懒加载@Lazy(主要针对于单实例的Bean容器启动的时候，不创建对象，在第一次使用的时候才会创建对象)
    public Tank tank(){
        return new Tank();
    }


//    @Bean
//    public TulingAspect tulingAspect() {
//        return new TulingAspect();
//    }
//    //当切 容器中有tulingAspect的组件，那么tulingLog才会被实例化.
//    @Bean
//    @Conditional(value = tulingAspect.class)
//    public TulingLog tulingLog() {
//        return new TulingLog();
//    }

    //通过importSelector类实现组件的导入（导入组件的id为全类名路径）
//    public class TulingImportSelector implements ImportSelector {
//        //可以获取导入类的注解信息
//        @Override
//        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
//            return new String[]{"com.tuling.testimport.compent.Dog"};
//        }
//    }

    //标识为测试环境才会被装配
    @Bean
    @Profile(value = "test")
    public DataSource testDs() {
        return buliderDataSource(new DruidDataSource());
    }
    //标识开发环境才会被激活
    @Bean
    @Profile(value = "dev")
    public DataSource devDs() {
        return buliderDataSource(new DruidDataSource());
    }
    //标识生产环境才会被激活
    @Bean
    @Profile(value = "prod")
    public DataSource prodDs() {
        return buliderDataSource(new DruidDataSource());
    }
    private DataSource buliderDataSource(DruidDataSource dataSource) {
//        dataSource.setUsername(userName);
//        dataSource.setPassword(password);
//        dataSource.setDriverClassName(classDriver);
//        dataSource.setUrl(jdbcUrl);
//        return dataSource;
        return null;
    }


    private String name;
    @Autowired
    private Tank tank;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tank getTank() {
        return tank;
    }

    public void setTank(Tank tank) {
        this.tank = tank;
    }

    public Car(){
        System.out.println("car加载...");
    }
}
