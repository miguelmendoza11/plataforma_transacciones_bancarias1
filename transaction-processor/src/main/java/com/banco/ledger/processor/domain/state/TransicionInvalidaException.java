package com.banco.ledger.processor.domain.state;

import com.banco.ledger.contracts.EstadoTransaccion;

import java.util.Objects;

/**
 * Se lanza cuando se pide al patron State una transicion que el estado actual no permite
 * (ej. completar una transaccion RECHAZADA).
 *
 * Extiende IllegalStateException porque la entrada es valida, pero la operacion no esta
 * permitida en el estado actual del objeto (Effective Java, Item 72: usar excepciones estandar).
 */
public class TransicionInvalidaException extends IllegalStateException {

    private final String accion;
    private final EstadoTransaccion estadoActual;

    public TransicionInvalidaException(String accion, EstadoTransaccion estadoActual) {
        super("No se puede " + Objects.requireNonNull(accion, "accion no puede ser nula")
                + " una transaccion en estado "
                + Objects.requireNonNull(estadoActual, "estadoActual no puede ser nulo"));
        this.accion = accion;
        this.estadoActual = estadoActual;
    }

    public String accion() {
        return accion;
    }

    public EstadoTransaccion estadoActual() {
        return estadoActual;
    }
}
