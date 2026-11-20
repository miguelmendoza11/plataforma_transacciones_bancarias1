package com.banco.ledger.api.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ManejadorErroresTest {

    private final ManejadorErrores manejador = new ManejadorErrores();

    @Test
    void argumentoInvalidoDevuelve400ConElMensaje() {
        // Arrange
        var ex = new IllegalArgumentException("No se puede transferir a la misma cuenta");

        // Act
        var respuesta = manejador.argumentoInvalido(ex);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody()).isEqualTo(Map.of("error", "No se puede transferir a la misma cuenta"));
    }

    @Test
    void campoNuloDevuelve400ConElMensaje() {
        // Arrange
        var ex = new NullPointerException("amount es obligatorio");

        // Act
        var respuesta = manejador.campoNulo(ex);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody()).isEqualTo(Map.of("error", "amount es obligatorio"));
    }

    @Test
    void cuerpoIlegibleDevuelve400ConLaCausaReal() {
        // Arrange
        var causa = new NullPointerException("from_user no puede ser nulo");
        var ex = new HttpMessageNotReadableException("JSON invalido", causa, mock(HttpInputMessage.class));

        // Act
        var respuesta = manejador.cuerpoIlegible(ex);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody()).isEqualTo(Map.of("error", "from_user no puede ser nulo"));
    }

    @Test
    void usaMensajePorDefectoSiLaExcepcionNoTraeMensaje() {
        // Act
        var respuesta = manejador.campoNulo(new NullPointerException());

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody()).isEqualTo(Map.of("error", "Solicitud invalida"));
    }
}
