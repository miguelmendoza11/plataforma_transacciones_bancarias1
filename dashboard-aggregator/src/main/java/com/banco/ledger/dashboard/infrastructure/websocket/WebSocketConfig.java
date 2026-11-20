package com.banco.ledger.dashboard.infrastructure.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Objects;

/** Registra el endpoint WebSocket /ws/transactions en el mismo puerto del servidor (8081). */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final TransaccionesWebSocketHandler handler;

    public WebSocketConfig(TransaccionesWebSocketHandler handler) {
        this.handler = Objects.requireNonNull(handler);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/transactions").setAllowedOrigins("*");
    }
}
