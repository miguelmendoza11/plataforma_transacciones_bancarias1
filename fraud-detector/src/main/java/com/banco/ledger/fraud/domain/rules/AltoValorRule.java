package com.banco.ledger.fraud.domain.rules;

import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;

import java.util.Objects;
import java.util.Optional;

/** Strategy: dispara HIGH_VALUE si el monto supera el umbral (por defecto $10.000). */
public record AltoValorRule(MontoDinero umbral) implements ReglaFraude {

    public AltoValorRule {
        Objects.requireNonNull(umbral, "El umbral no puede ser nulo");
    }

    @Override
    public Optional<String> evaluar(TransactionEventRecord evento) {
        Objects.requireNonNull(evento, "El evento no puede ser nulo");
        return evento.amount().esMayorQue(umbral)
                ? Optional.of(MotivosFraude.HIGH_VALUE)
                : Optional.empty();
    }
}
