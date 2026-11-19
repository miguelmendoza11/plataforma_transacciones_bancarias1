package com.banco.ledger.processor.domain.state;

import com.banco.ledger.contracts.EstadoTransaccion;

/** Estado final: no paso alguna regla de validacion. No admite transiciones. */
public record Rechazada() implements EstadoTransaccionState {

    @Override
    public EstadoTransaccion nombre() {
        return EstadoTransaccion.REJECTED;
    }
}
