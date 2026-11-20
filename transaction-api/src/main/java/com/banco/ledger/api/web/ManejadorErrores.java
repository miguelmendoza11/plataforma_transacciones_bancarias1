package com.banco.ledger.api.web;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

/** Traduce las excepciones Fail-Fast a 400 Bad Request con { "error": "..." }. */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> argumentoInvalido(IllegalArgumentException ex) {
        return error(ex.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, String>> campoNulo(NullPointerException ex) {
        return error(ex.getMessage());
    }

    // JSON mal formado o que fallo en el constructor del record al deserializar
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> cuerpoIlegible(HttpMessageNotReadableException ex) {
        return error(ex.getMostSpecificCause().getMessage());
    }

    private ResponseEntity<Map<String, String>> error(String mensaje) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", Objects.requireNonNullElse(mensaje, "Solicitud invalida")));
    }
}
