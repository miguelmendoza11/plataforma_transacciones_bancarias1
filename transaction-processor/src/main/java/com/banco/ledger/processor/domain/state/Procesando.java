package com.banco.ledger.processor.domain.state;

import com.banco.ledger.contracts.EstadoTransaccion;

/** Se estan evaluando las reglas de validacion. Termina completada o rechazada. */
public record Procesando() implements EstadoTransaccionState {

    @Override
    public EstadoTransaccionState completar() {
        return new Completada();
    }

    @Override
    public EstadoTransaccionState rechazar() {
        return new Rechazada();
    }

    @Override
    public EstadoTransaccion nombre() {
        return EstadoTransaccion.PROCESANDO;
    }
}
