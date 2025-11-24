package com.rafaelcabanillas.choirapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // endpoint "/ws" is correct
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable Heartbeats (Ping/Pong every 10 seconds)
        // This prevents Railway/Nginx from killing the connection during silence.
        ThreadPoolTaskScheduler te = new ThreadPoolTaskScheduler();
        te.setPoolSize(1);
        te.setThreadNamePrefix("wss-heartbeat-thread-");
        te.initialize();

        // Configure Broker
        // Added "/queue" for future private messaging support
        registry.enableSimpleBroker("/topic", "/queue")
                .setTaskScheduler(te) // Attach the heartbeat scheduler
                .setHeartbeatValue(new long[]{10000, 10000}); // Server send/receive every 10s

        // Prefixes for messages FROM client TO server
        registry.setApplicationDestinationPrefixes("/app");
    }
}