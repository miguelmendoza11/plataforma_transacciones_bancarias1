package com.banco.ledger.api.web;

import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransferCommandRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.contracts.Validaciones;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO de entrada del POST /transfer: { "from_user": "A", "to_user": "B", "amount": 50 }.
 * Fail-Fast: valida en el constructor compacto y se convierte al comando de los contratos.
 */
public record TransferRequest(
        @JsonProperty("from_user") String fromUser,
        @JsonProperty("to_user") String toUser,
        @JsonProperty("amount") BigDecimal amount) {

    public TransferRequest {
        fromUser = Validaciones.noVacio(fromUser, "from_user");
        toUser = Validaciones.noVacio(toUser, "to_user");
        Objects.requireNonNull(amount, "amount es obligatorio");
    }

    // monto <= 0 y misma cuenta los rechazan MontoDinero y TransferCommandRecord
    public TransferCommandRecord toComando(TxId txId, Instant solicitadaEn) {
        return new TransferCommandRecord(txId, fromUser, toUser, MontoDinero.de(amount), solicitadaEn);
    }
}
