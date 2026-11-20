package com.banco.ledger.dashboard.domain;

import com.banco.ledger.contracts.FraudAlertRecord;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TxId;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Vista materializada en memoria (lado Query de CQRS). Es STATEFUL y la usan dos hilos
 * (listener de transacciones y listener de alertas), por eso todo cambio va por compute(),
 * que es atomico por tx_id.
 */
public final class VistaMaterializada {

    private final Map<TxId, TransaccionVista> vistas = new ConcurrentHashMap<>();
    // alertas que llegaron antes que su transaccion (no hay orden entre topics)
    private final Map<TxId, String> alertasPendientes = new ConcurrentHashMap<>();

    public TransaccionVista registrar(TransactionEventRecord evento) {
        Objects.requireNonNull(evento, "evento no puede ser nulo");
        return vistas.compute(evento.txId(), (txId, actual) -> {
            TransaccionVista nueva = TransaccionVista.desde(evento);
            String pendiente = alertasPendientes.remove(txId);
            if (pendiente != null) {
                return nueva.marcarSospechosa(pendiente);
            }
            // reentrega de Kafka: no perder la marca que ya tenia
            if (actual != null && actual.sospechosa()) {
                return nueva.marcarSospechosa(actual.motivoFraude());
            }
            return nueva;
        });
    }

    public Optional<TransaccionVista> marcarSospechosa(FraudAlertRecord alerta) {
        Objects.requireNonNull(alerta, "alerta no puede ser nula");
        // la pendiente se guarda dentro del compute para no cruzarse con registrar()
        TransaccionVista marcada = vistas.compute(alerta.txId(), (txId, actual) -> {
            if (actual == null) {
                alertasPendientes.put(txId, alerta.reason());
                return null;
            }
            return actual.marcarSospechosa(alerta.reason());
        });
        return Optional.ofNullable(marcada);
    }

    public List<TransaccionVista> snapshot() {
        return List.copyOf(vistas.values());
    }
}
