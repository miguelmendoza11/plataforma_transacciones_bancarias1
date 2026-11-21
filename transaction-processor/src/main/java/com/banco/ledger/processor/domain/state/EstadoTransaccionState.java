package com.banco.ledger.processor.domain.state;

import com.banco.ledger.contracts.EstadoTransaccion;

/**
 * PATRON STATE - ciclo de vida de la Transaccion.
 *
 * <pre>
 * RECIBIDA -> PROCESANDO -> COMPLETADA -> SOSPECHOSA
 *                       \-> RECHAZADA
 * </pre>
 *
 * Cada estado es un objeto que sabe cual es su SIGUIENTE estado. Por defecto toda transicion
 * lanza {@link TransicionInvalidaException}; cada estado sobrescribe SOLO las transiciones que
 * le estan permitidas. Asi no hay ni un if/else ni un switch sobre el estado: el polimorfismo
 * decide (Open/Closed: un estado nuevo es una clase nueva, no un "case" mas).
 *
 * Es {@code sealed}: el compilador garantiza que no existen otros estados que los listados en
 * {@code permits}. Los estados son records sin componentes: inmutables y sin identidad propia
 * (dos {@code new Completada()} son iguales).
 */
public sealed interface EstadoTransaccionState
        permits Recibida, Procesando, Completada, Rechazada, Sospechosa {

    /** Estado con el que nace toda transaccion. */
    static EstadoTransaccionState inicial() {
        return new Recibida();
    }

    /**
     * Rehidrata el estado a partir de su nombre persistido (ej. desde PostgreSQL).
     *
     * @throws IllegalArgumentException si el nombre es nulo
     */
    static EstadoTransaccionState desde(EstadoTransaccion nombre) {
        if (nombre == null) {
            throw new IllegalArgumentException("El nombre del estado no puede ser nulo");
        }
        return CatalogoEstados.buscar(nombre);
    }

    default EstadoTransaccionState iniciarProcesamiento() {
        throw new TransicionInvalidaException("iniciar el procesamiento de", nombre());
    }

    default EstadoTransaccionState completar() {
        throw new TransicionInvalidaException("completar", nombre());
    }

    default EstadoTransaccionState rechazar() {
        throw new TransicionInvalidaException("rechazar", nombre());
    }

    default EstadoTransaccionState marcarComoSospechosa() {
        throw new TransicionInvalidaException("marcar como sospechosa", nombre());
    }

    /** Nombre del estado tal como viaja en los eventos de Kafka. */
    EstadoTransaccion nombre();
}
