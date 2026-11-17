package com.banco.ledger.fraud.domain.rules;

import com.banco.ledger.contracts.TransactionEventRecord;

import java.util.Optional;

/**
 * PATRON STRATEGY - politicas de deteccion de fraude intercambiables.
 *
 * TODO (Miguel):
 *  - AltoValorRule: monto > umbral (10.000, configurable) -> "HIGH_VALUE".
 *  - FrecuenciaAnomalaRule (STATEFUL): mas de N transacciones del mismo from_user en una
 *    ventana de T segundos -> "HIGH_FREQUENCY". Necesita estado en memoria
 *    (ej. Map<String, Deque<Instant>>) -> ojo con concurrencia: ConcurrentHashMap.
 *  - Devolver el motivo si la regla se dispara, Optional.empty() si no.
 */
public interface ReglaFraude {
    Optional<String> evaluar(TransactionEventRecord evento);
}
