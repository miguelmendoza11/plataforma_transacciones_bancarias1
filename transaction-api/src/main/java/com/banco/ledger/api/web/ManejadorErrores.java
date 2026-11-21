package com.banco.ledger.api.web;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/** Traduce las excepciones Fail-Fast a 400 Bad Request con { "error": "..." }. */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> argumentoInvalido(IllegalArgumentException ex) {
        return error(ex.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> campoNulo(NullPointerException ex) {
        return error(ex.getMessage());
    }

    // JSON mal formado o que fallo en el constructor del record al deserializar
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> cuerpoIlegible(HttpMessageNotReadableException ex) {
        return error(ex.getMostSpecificCause().getMessage());
    }

    private ResponseEntity<ErrorResponse> error(String mensaje) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(Objects.requireNonNullElse(mensaje, "Solicitud invalida")));
    }
}
