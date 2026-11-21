package com.banco.ledger.processor.domain.validation;

import com.banco.ledger.processor.domain.model.CuentaBancaria;
import com.banco.ledger.processor.domain.model.Transaccion;

import java.util.Objects;

/** Estrategia: la cuenta origen debe tener saldo suficiente para el monto. */
public record FondosSuficientesRule() implements ReglaValidacion {

    static final String MOTIVO = "INSUFFICIENT_FUNDS";

    @Override
    public ResultadoValidacion evaluar(Transaccion tx, CuentaBancaria origen) {
        Objects.requireNonNull(tx, "La transaccion no puede ser nula");
        Objects.requireNonNull(origen, "La cuenta origen no puede ser nula");
        return origen.tieneFondosPara(tx.monto())
                ? ResultadoValidacion.aprobada()
                : ResultadoValidacion.rechazada(MOTIVO);
    }
}
