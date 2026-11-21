package com.banco.ledger.processor.domain.state;

import com.banco.ledger.contracts.EstadoTransaccion;

/** El dinero ya se movio. Solo puede marcarse despues como sospechosa (fraude). */
public record Completada() implements EstadoTransaccionState {

    @Override
    public EstadoTransaccionState marcarComoSospechosa() {
        return new Sospechosa();
    }

    @Override
    public EstadoTransaccion nombre() {
        return EstadoTransaccion.COMPLETED;
    }
}
