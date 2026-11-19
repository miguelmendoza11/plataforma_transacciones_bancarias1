package com.banco.ledger.processor.domain.state;

import com.banco.ledger.contracts.EstadoTransaccion;

/** Estado inicial: el comando llego desde RabbitMQ. Solo puede pasar a procesamiento. */
public record Recibida() implements EstadoTransaccionState {

    @Override
    public EstadoTransaccionState iniciarProcesamiento() {
        return new Procesando();
    }

    @Override
    public EstadoTransaccion nombre() {
        return EstadoTransaccion.RECIBIDA;
    }
}
