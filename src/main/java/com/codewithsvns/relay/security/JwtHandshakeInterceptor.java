package com.codewithsvns.relay.security;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    public JwtHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }

        String authHeader = servletRequest.getServletRequest()
                .getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatusCode(
                    org.springframework.http.HttpStatus.UNAUTHORIZED
            );
            return false;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractEmail(token);

            if (!jwtService.isTokenValid(token, email)) {
                response.setStatusCode(
                        org.springframework.http.HttpStatus.UNAUTHORIZED
                );
                return false;
            }

            attributes.put("authenticatedEmail", email);
            return true;

        } catch (JwtException | IllegalArgumentException exception) {
            response.setStatusCode(
                    org.springframework.http.HttpStatus.UNAUTHORIZED
            );
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // No additional action is required after the handshake.
    }
}
