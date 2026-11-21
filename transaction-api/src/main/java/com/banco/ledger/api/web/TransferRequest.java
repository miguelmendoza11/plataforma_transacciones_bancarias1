package com.banco.ledger.api.web;

import com.banco.ledger.contracts.Divisa;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransferCommandRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.contracts.Validaciones;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO de entrada del POST /transfer: { "from_user": "A", "to_user": "B", "amount": 50, "currency": "COP" }.
 * Fail-Fast: valida en el constructor compacto y se convierte al comando de los contratos.
 * "currency" es opcional (si no viene se asume COP); una divisa no admitida se rechaza.
 */
public record TransferRequest(
        @JsonProperty("from_user") String fromUser,
        @JsonProperty("to_user") String toUser,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("currency") Divisa currency) {

    @JsonCreator
    public TransferRequest {
        fromUser = Validaciones.noVacio(fromUser, "from_user");
        toUser = Validaciones.noVacio(toUser, "to_user");
        Objects.requireNonNull(amount, "amount es obligatorio");
        currency = Objects.requireNonNullElse(currency, Divisa.porDefecto());
    }

    public TransferRequest(String fromUser, String toUser, BigDecimal amount) {
        this(fromUser, toUser, amount, null);
    }

    // monto <= 0 y misma cuenta los rechazan MontoDinero y TransferCommandRecord
    public TransferCommandRecord toComando(TxId txId, Instant solicitadaEn) {
        return new TransferCommandRecord(txId, fromUser, toUser, MontoDinero.de(amount), solicitadaEn);
    }
}
