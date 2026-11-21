package com.banco.ledger.processor.domain.validation;

import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.processor.domain.model.CuentaBancaria;
import com.banco.ledger.processor.domain.model.Transaccion;

import java.util.Objects;

/**
 * Estrategia: ninguna transferencia puede superar un monto maximo.
 * El maximo se configura desde fuera (por defecto 100000), asi una transferencia de
 * $20.000 se COMPLETA y luego es el detector de fraude quien la marca como sospechosa.
 */
public record MontoMaximoPorTransaccionRule(MontoDinero maximo) implements ReglaValidacion {

    static final String MOTIVO = "AMOUNT_LIMIT_EXCEEDED";

    public MontoMaximoPorTransaccionRule {
        Objects.requireNonNull(maximo, "El monto maximo no puede ser nulo");
    }

    @Override
    public ResultadoValidacion evaluar(Transaccion tx, CuentaBancaria origen) {
        Objects.requireNonNull(tx, "La transaccion no puede ser nula");
        return tx.monto().esMayorQue(maximo)
                ? ResultadoValidacion.rechazada(MOTIVO)
                : ResultadoValidacion.aprobada();
    }
}
