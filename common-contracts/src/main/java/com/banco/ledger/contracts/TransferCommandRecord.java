package com.banco.ledger.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;

/**
 * Comando que viaja por RabbitMQ (cola transfer_commands):
 * "Usuario A quiere enviar $50 a Usuario B".
 */
public record TransferCommandRecord(
        @JsonProperty("tx_id") TxId txId,
        @JsonProperty("from_user") String fromUser,
        @JsonProperty("to_user") String toUser,
        @JsonProperty("amount") MontoDinero amount,
        @JsonProperty("requested_at") Instant requestedAt) {

    public TransferCommandRecord {
        Objects.requireNonNull(txId, "tx_id es obligatorio");
        fromUser = Validaciones.noVacio(fromUser, "from_user");
        toUser = Validaciones.noVacio(toUser, "to_user");
        Objects.requireNonNull(amount, "amount es obligatorio");
        Objects.requireNonNull(requestedAt, "requested_at es obligatorio");
        if (fromUser.equalsIgnoreCase(toUser)) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
        }
    }
}
