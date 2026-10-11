package com.codewithsvns.relay.security;

import com.codewithsvns.relay.security.JwtService;
import io.jsonwebtoken.JwtException;
import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    public WebSocketSecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(
                                message, StompHeaderAccessor.class);

                if (accessor == null) {
                    return message;
                }

                StompCommand command = accessor.getCommand();

                if (command == StompCommand.CONNECT) {
                    String authorization =
                            accessor.getFirstNativeHeader("Authorization");

                    if (authorization == null
                            || !authorization.startsWith("Bearer ")) {
                        throw new IllegalArgumentException(
                                "Missing or invalid Authorization header");
                    }

                    String token = authorization.substring(7);

                    try {
                        String email = jwtService.extractEmail(token);

                        if (email == null || !jwtService.isTokenValid(token, email)) {
                            throw new IllegalArgumentException(
                                    "Invalid or expired token");
                        }

                        accessor.setUser(
                                new UsernamePasswordAuthenticationToken(
                                        email, null, List.of()));

                    } catch (JwtException | IllegalArgumentException exception) {
                        throw new IllegalArgumentException(
                                "Invalid or expired token", exception);
                    }
                }

                if (command == StompCommand.CONNECT
                        || command == StompCommand.SEND
                        || command == StompCommand.SUBSCRIBE) {

                    if (accessor.getUser() == null) {
                        throw new IllegalArgumentException(
                                "Unauthenticated WebSocket connection");
                    }
                }

                if (command == StompCommand.SEND) {
                    String destination = accessor.getDestination();

                    if (destination == null || !destination.startsWith("/app/")) {
                        throw new IllegalArgumentException(
                                "Sending to this destination is not allowed");
                    }
                }

                if (command == StompCommand.SUBSCRIBE) {
                    String destination = accessor.getDestination();

                    if (destination == null
                            || !destination.startsWith("/user/queue/")) {
                        throw new IllegalArgumentException(
                                "Subscribing to this destination is not allowed");
                    }
                }

                return message;
            }
        });
    }
}