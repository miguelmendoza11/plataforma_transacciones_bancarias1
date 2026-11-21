package com.banco.ledger.dashboard.infrastructure.websocket;

import com.banco.ledger.dashboard.application.port.DashboardNotifier;
import com.banco.ledger.dashboard.domain.TransaccionVista;
import org.springframework.stereotype.Component;

import java.util.Objects;

/** Adaptador de salida: implementa el puerto DashboardNotifier con push por WebSocket. */
@Component
public class WebSocketDashboardNotifier implements DashboardNotifier {

    private final TransaccionesWebSocketHandler handler;

    public WebSocketDashboardNotifier(TransaccionesWebSocketHandler handler) {
        this.handler = Objects.requireNonNull(handler);
    }

    @Override
    public void difundir(TransaccionVista vista) {
        Objects.requireNonNull(vista, "vista no puede ser nula");
        handler.enviarATodas(vista);
    }
}
