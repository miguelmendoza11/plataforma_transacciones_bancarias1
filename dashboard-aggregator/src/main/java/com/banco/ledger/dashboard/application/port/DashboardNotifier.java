package com.banco.ledger.dashboard.application.port;

import com.banco.ledger.dashboard.domain.TransaccionVista;

/** Puerto de salida hacia los clientes web. La logica no conoce WebSocket. */
public interface DashboardNotifier {
    void difundir(TransaccionVista vista);
}
