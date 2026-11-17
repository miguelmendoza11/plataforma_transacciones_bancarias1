package com.banco.ledger.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Comando de la cola email_queue (lo producen notification-router y ad-generator). */
public record EmailCommandRecord(
        @JsonProperty("to") String to,
        @JsonProperty("body") String body) {

    public EmailCommandRecord {
        to = Validaciones.noVacio(to, "to");
        body = Validaciones.noVacio(body, "body");
    }
}
