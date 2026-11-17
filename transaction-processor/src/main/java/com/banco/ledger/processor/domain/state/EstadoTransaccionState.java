package com.banco.ledger.processor.domain.state;

import com.banco.ledger.contracts.EstadoTransaccion;

/**
 * PATRON STATE - ciclo de vida de la Transaccion.
 *
 * Estados: RECIBIDA -> PROCESANDO -> COMPLETADA | RECHAZADA, y COMPLETADA -> SOSPECHOSA.
 *
 * TODO (Miguel):
 *  - Crear una clase por estado: Recibida, Procesando, Completada, Rechazada, Sospechosa
 *    (pueden ser records/singletons; considera una interfaz sealed con "permits").
 *  - Cada estado devuelve el SIGUIENTE estado o lanza IllegalStateException si la
 *    transicion no esta permitida. CERO if/else o switch sobre el estado.
 *  - Ajustar las firmas si necesitas pasar contexto (ej. motivo de rechazo).
 */
public interface EstadoTransaccionState {

    EstadoTransaccionState iniciarProcesamiento();

    EstadoTransaccionState completar();

    EstadoTransaccionState rechazar();

    EstadoTransaccionState marcarComoSospechosa();

    /** Nombre del estado tal como viaja en los eventos de Kafka. */
    EstadoTransaccion nombre();
}
