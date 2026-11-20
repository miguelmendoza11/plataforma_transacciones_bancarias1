package com.banco.ledger.dashboard.infrastructure.websocket;

import com.banco.ledger.dashboard.domain.TransaccionVista;
import com.banco.ledger.dashboard.domain.VistaMaterializada;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Handler de /ws/transactions: guarda las sesiones abiertas y, al conectar, envia el snapshot
 * de la vista (un mensaje por transaccion). Despues el cliente recibe solo los cambios (push).
 */
@Component
public class TransaccionesWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(TransaccionesWebSocketHandler.class);

    // CopyOnWriteArraySet: se recorre mucho (cada cambio) y se modifica poco (conectar/desconectar)
    private final Set<WebSocketSession> sesiones = new CopyOnWriteArraySet<>();
    private final VistaMaterializada vista;
    private final ObjectMapper objectMapper;

    public TransaccionesWebSocketHandler(VistaMaterializada vista, ObjectMapper objectMapper) {
        this.vista = Objects.requireNonNull(vista);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // se bloquea la sesion mientras va el snapshot: los cambios nuevos llegan despues de el
        synchronized (session) {
            sesiones.add(session);
            for (TransaccionVista transaccion : vista.snapshot()) {
                enviar(session, transaccion);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sesiones.remove(session);
    }

    public void enviarATodas(TransaccionVista transaccion) {
        for (WebSocketSession session : sesiones) {
            enviar(session, transaccion);
        }
    }

    // WebSocketSession no es thread-safe: un envio a la vez por sesion
    private void enviar(WebSocketSession session, TransaccionVista transaccion) {
        try {
            String json = objectMapper.writeValueAsString(TransaccionMensaje.desde(transaccion));
            synchronized (session) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        } catch (IOException e) {
            log.warn("No se pudo enviar a la sesion {}: {}", session.getId(), e.getMessage());
            sesiones.remove(session);
        }
    }
}
