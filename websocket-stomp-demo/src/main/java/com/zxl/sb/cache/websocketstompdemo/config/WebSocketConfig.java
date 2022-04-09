package com.zxl.sb.cache.websocketstompdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Created by genghz on 18/4/11.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 启用SockJs,前端访问url: ws://host:port/{endPoint}/{transport},eg:ws://127.0.0.1:8080/ws/websocket
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 设置接收客户端消息 的 路径前缀（不设置可以）
        registry.setApplicationDestinationPrefixes("/app");
        // 设置接收客户端订阅 的 路径前缀（必须不设置，可以为空串）
        registry.enableSimpleBroker("");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 添加拦截器，处理客户端发来的请求
        registration.interceptors(new WebSocketHandleInterceptor());
    }
}
