package com.zxl.sb.springdemo.others;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

@Configuration
@ComponentScan(basePackages = {"com.zxl.sb.springdemo"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,value = {Controller.class}),
                          @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,value = {Car.class})})
public class MainConfig {
    public static void main(String[] args) {
        // 加载spring上下文
        // reader 读取@Component @Bean @Import等 + register(初始化)各类BeanFactoryPostProcessor和BeanPostProcessor,比如：ConfigurationClassPostProcessor\DefaultEventListenerFactory\AotuwiredAnnotationBeanPostFactory...
        // scanner 读取@ComponentScanner(basePackages="")

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfig.class);
        //invokeBeanFactoryPostProcessors
        //finishBeanFactoryInitialization

        context.refresh();
        Car car = context.getBean("car", Car.class);
        System.out.println(car.getName());
    }



    /**
     * ApplicationEvent   点击 context.refresh();查看源码
     *      所使用的设计模式：观察者模式。ApplicationListener是观察者接口
     *
     *      第一步：初始化广播器。 initApplicationEventMulticaster  如果项目中在配置文件中为容器定义一个事件广播器（配置类实现AppicationEventMultiCaster）Spring就通过反射将其注册成容器的广播器，
     *                                                否则就是用Spring就自动使用SimpleApplicationEventMultiCaster作为事件广播器
     *                  if (beanFactory.containsLocalBean("applicationEventMulticaster")) {
     *                      this.applicationEventMulticaster = (ApplicationEventMulticaster)beanFactory.getBean("applicationEventMulticaster", ApplicationEventMulticaster.class);
     *                  } else {
     *                      this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
     *                      beanFactory.registerSingleton("applicationEventMulticaster", this.applicationEventMulticaster);
     *                  }
     *      第二步：注册监听器。 registerListeners 事件广播器调用addApplicationListenerBean(listenerBeanName),将其添加到事件广播器所提供的的监控器注册表中
     *
     *              Iterator var1 = this.getApplicationListeners().iterator();
     *              while(var1.hasNext()) {
     *                  ApplicationListener<?> listener = (ApplicationListener)var1.next();
     *                  this.getApplicationEventMulticaster().addApplicationListener(listener);
     *              }
     *
     *              String[] listenerBeanNames = this.getBeanNamesForType(ApplicationListener.class, true, false);
     *              String[] var7 = listenerBeanNames;
     *              int var3 = listenerBeanNames.length;
     *              for(int var4 = 0; var4 < var3; ++var4) {
     *                  String listenerBeanName = var7[var4];
     *                  this.getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
     *              }
     *       第三步： 发布事件。finishRefresh --> publishEvent
     *                protected void finishRefresh() {
     *                    this.publishEvent((ApplicationEvent)(new ContextRefreshedEvent(this)));
     *                }
     *                protected void publishEvent(Object event, @Nullable ResolvableType eventType) {
     *                    if (this.parent instanceof AbstractApplicationContext) {
     *                        ((AbstractApplicationContext)this.parent).publishEvent(event, eventType);
     *                    } else {
     *                        this.parent.publishEvent(event);
     *                    }
     *                }
     *       第四步： 监听事件。SimpleApplicationEventMulticaster --> multicastEvent() --> invokeListener() --> doInvokeListener()
     *                从源码中可以看出如果想用异步的，可以自己实现ApplicationEventMulitCaster接口，然后setTaskExecutor(new SimpleAsyncTaskExecutor()),否则就是同步的。
     *                遍历注册的每个监听器，并调用他们的onApplicationEvent方法。
     *              public void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType) {
     *                  ResolvableType type = eventType != null ? eventType : this.resolveDefaultEventType(event);
     *                  Executor executor = this.getTaskExecutor();
     *                  Iterator var5 = this.getApplicationListeners(event, type).iterator();
     *
     *                  while(var5.hasNext()) {
     *                      ApplicationListener<?> listener = (ApplicationListener)var5.next();
     *                      if (executor != null) {
     *                          executor.execute(() -> {
     *                          this.invokeListener(listener, event);
     *                      });
     *                  } else {
     *                      this.invokeListener(listener, event);
     *                  }
     *              }

     *              private void doInvokeListener(ApplicationListener listener, ApplicationEvent event) {
     *                  listener.onApplicationEvent(event);
     *              }
     *
     *
     */

}
