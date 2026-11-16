package com.banco.ledger.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;

/**
 * Evento inmutable publicado en Kafka (topic transactions_log, key = tx_id).
 * Es la fuente unica de verdad del ledger.
 */
public record TransactionEventRecord(
        @JsonProperty("tx_id") TxId txId,
        @JsonProperty("from_user") String fromUser,
        @JsonProperty("to_user") String toUser,
        @JsonProperty("amount") MontoDinero amount,
        @JsonProperty("status") EstadoTransaccion status,
        @JsonProperty("reason") String reason,
        @JsonProperty("occurred_at") Instant occurredAt) {

    public TransactionEventRecord {
        Objects.requireNonNull(txId, "tx_id es obligatorio");
        fromUser = Validaciones.noVacio(fromUser, "from_user");
        toUser = Validaciones.noVacio(toUser, "to_user");
        Objects.requireNonNull(amount, "amount es obligatorio");
        Objects.requireNonNull(status, "status es obligatorio");
        Objects.requireNonNull(occurredAt, "occurred_at es obligatorio");
    }

    public boolean estaCompletada() {
        return status == EstadoTransaccion.COMPLETED;
    }
}
