package com.rafaelcabanillas.choirapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the URL the React Native app will connect to via "SockJS" or standard WS
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // Allow React Native
        // .withSockJS(); // Optional: Remove if using native WS client
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefixes for messages destined for the CLIENT (app)
        registry.enableSimpleBroker("/topic");

        // Prefixes for messages destined for the SERVER (Spring)
        registry.setApplicationDestinationPrefixes("/app");
    }
}