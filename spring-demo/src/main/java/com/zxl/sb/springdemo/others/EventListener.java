//package com.zxl.sb.springdemo;
//
//import org.springframework.beans.factory.annotation.Autowire;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationListener;
//import org.springframework.stereotype.Component;
//
//public class EventListener {
//    @Autowire
//    private ApplicationContext applicationContext;
//
//    // 发布事件
//    applicationContext.publishEvent(new HelloEvent(this,"lgb"));
//
//    // 处理事件
//    @Component
//    public class HelloEventListener implements ApplicationListener<OrderEvent> {
//         @Override
//         public void onApplicationEvent(OrderEvent event) {
//             if(event.getName().equals("减库存")){
//                 System.out.println("减库存.......");
//             }
//         }
//    }
//
//    @Component
//    public class OrderEventListener {
//
//        @EventListener(OrderEvent.class)
//        public void onApplicationEvent(OrderEvent event) {
//             if(event.getName().equals("减库存")){
//                 System.out.println("减库存.......");
//             }
//        }
//     }
//
//}
