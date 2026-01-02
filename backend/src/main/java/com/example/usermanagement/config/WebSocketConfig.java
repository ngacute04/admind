package com.example.usermanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Kích hoạt tính năng Message Broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 1. Tạo "phòng chờ" cho tin nhắn gửi đến người dùng cụ thể (/user) 
        // và các thông báo chung (/topic)
        config.enableSimpleBroker("/topic", "/user");
        
        // 2. Tiền tố cho các request gửi từ Client lên Server (ví dụ: /app/chat)
        config.setApplicationDestinationPrefixes("/app");
        
        // 3. Cấu hình để gửi tin nhắn riêng cho từng User
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký cổng kết nối (Endpoint) mà React sẽ gọi tới
        registry.addEndpoint("/ws-redzone")
                .setAllowedOrigins("http://localhost:3000") // URL của React
                .withSockJS(); // Hỗ trợ nếu trình duyệt không có WebSocket thuần
    }
}