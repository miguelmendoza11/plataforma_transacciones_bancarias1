package com.banco.ledger.api.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/** DTO de salida del POST /transfer: { "tx_id": "..." }. */
public record TransferResponse(@JsonProperty("tx_id") String txId) {

    public TransferResponse {
        Objects.requireNonNull(txId, "tx_id es obligatorio");
    }
}
