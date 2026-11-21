package com.banco.ledger.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;

/** Alerta publicada en Kafka (topic fraud_alerts). Ej: { "tx_id": "...", "reason": "HIGH_VALUE" }. */
public record FraudAlertRecord(
        @JsonProperty("tx_id") TxId txId,
        @JsonProperty("reason") String reason,
        @JsonProperty("detected_at") Instant detectedAt) {

    public FraudAlertRecord {
        Objects.requireNonNull(txId, "tx_id es obligatorio");
        reason = Validaciones.noVacio(reason, "reason");
        Objects.requireNonNull(detectedAt, "detected_at es obligatorio");
    }
}
