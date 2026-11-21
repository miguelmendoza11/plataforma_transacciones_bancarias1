package com.banco.ledger.fraud.domain.rules;

import com.banco.ledger.contracts.TransactionEventRecord;

import java.util.Optional;

/**
 * Patron Strategy: politicas de deteccion de fraude intercambiables.
 * Devuelve el motivo si la regla se dispara, o Optional.empty() si no.
 */
public interface ReglaFraude {
    Optional<String> evaluar(TransactionEventRecord evento);
}
