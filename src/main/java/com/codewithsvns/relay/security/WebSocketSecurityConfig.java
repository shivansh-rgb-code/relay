package com.codewithsvns.relay.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig
        implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration) {

        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(
                    Message<?> message,
                    MessageChannel channel) {

                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(
                                message,
                                StompHeaderAccessor.class
                        );

                if (accessor == null) {
                    throw new IllegalArgumentException(
                            "Invalid WebSocket message"
                    );
                }

                StompCommand command = accessor.getCommand();

                if (command == StompCommand.CONNECT
                        || command == StompCommand.SEND
                        || command == StompCommand.SUBSCRIBE) {

                    if (accessor.getUser() == null) {
                        throw new IllegalArgumentException(
                                "Unauthenticated WebSocket connection"
                        );
                    }
                }

                if (command == StompCommand.SEND) {
                    String destination = accessor.getDestination();

                    if (destination == null
                            || !destination.startsWith("/app/")) {
                        throw new IllegalArgumentException(
                                "Sending to this destination is not allowed"
                        );
                    }
                }

                if (command == StompCommand.SUBSCRIBE) {
                    String destination = accessor.getDestination();

                    if (destination == null
                            || !destination.startsWith("/user/queue/")) {
                        throw new IllegalArgumentException(
                                "Only private message queues may be subscribed to"
                        );
                    }
                }

                return message;
            }
        });
    }
}
