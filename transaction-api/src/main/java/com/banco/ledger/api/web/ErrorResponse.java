package com.banco.ledger.api.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/** DTO de salida para errores de la API: { "error": "..." }. */
public record ErrorResponse(@JsonProperty("error") String error) {

    public ErrorResponse {
        Objects.requireNonNull(error, "error es obligatorio");
    }
}
