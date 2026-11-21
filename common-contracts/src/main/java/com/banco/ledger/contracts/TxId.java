package com.banco.ledger.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;
import java.util.UUID;

/** Identificador unico de transaccion. Se usa como key en Kafka. */
public record TxId(String valor) {

    public TxId {
        Objects.requireNonNull(valor, "tx_id no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("tx_id no puede estar vacio");
        }
    }

    public static TxId nuevo() {
        return new TxId(UUID.randomUUID().toString());
    }

    @JsonCreator
    public static TxId de(String valor) {
        return new TxId(valor);
    }

    @JsonValue
    @Override
    public String valor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor;
    }
}
