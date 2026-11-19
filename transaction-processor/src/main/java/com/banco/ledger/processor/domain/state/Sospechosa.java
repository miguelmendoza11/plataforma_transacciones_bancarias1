package com.banco.ledger.processor.domain.state;

import com.banco.ledger.contracts.EstadoTransaccion;

/** Estado final: completada pero marcada por el detector de fraude. No admite transiciones. */
public record Sospechosa() implements EstadoTransaccionState {

    @Override
    public EstadoTransaccion nombre() {
        return EstadoTransaccion.SOSPECHOSA;
    }
}
