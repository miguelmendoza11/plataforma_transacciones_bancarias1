package com.banco.ledger.api.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** El formulario y demo.sh leen estos JSON: el formato no debe cambiar al usar records. */
class RespuestasJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void transferResponseSeSerializaConTxId() throws Exception {
        // Arrange
        var respuesta = new TransferResponse("tx-1");

        // Act
        String json = objectMapper.writeValueAsString(respuesta);

        // Assert
        assertThat(json).isEqualTo("{\"tx_id\":\"tx-1\"}");
    }

    @Test
    void errorResponseSeSerializaConError() throws Exception {
        // Arrange
        var respuesta = new ErrorResponse("Divisa no admitida: USD");

        // Act
        String json = objectMapper.writeValueAsString(respuesta);

        // Assert
        assertThat(json).isEqualTo("{\"error\":\"Divisa no admitida: USD\"}");
    }
}
