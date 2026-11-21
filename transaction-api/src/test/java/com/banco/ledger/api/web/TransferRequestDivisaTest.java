package com.banco.ledger.api.web;

import com.banco.ledger.contracts.Divisa;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class TransferRequestDivisaTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void sinDivisaSeAsumeCop() {
        // Arrange / Act
        var request = new TransferRequest("A", "B", new BigDecimal("50"));

        // Assert
        assertThat(request.currency()).isEqualTo(Divisa.COP);
    }

    @Test
    void jsonSinCurrencyQuedaEnCop() throws Exception {
        // Arrange
        String json = "{\"from_user\":\"A\",\"to_user\":\"B\",\"amount\":50}";

        // Act
        TransferRequest request = objectMapper.readValue(json, TransferRequest.class);

        // Assert
        assertThat(request.currency()).isEqualTo(Divisa.COP);
    }

    @Test
    void jsonConCopEnMinusculaSeAcepta() throws Exception {
        // Arrange
        String json = "{\"from_user\":\"A\",\"to_user\":\"B\",\"amount\":50,\"currency\":\"cop\"}";

        // Act
        TransferRequest request = objectMapper.readValue(json, TransferRequest.class);

        // Assert
        assertThat(request.currency()).isEqualTo(Divisa.COP);
    }

    @Test
    void jsonConDivisaNoAdmitidaSeRechaza() {
        // Arrange
        String json = "{\"from_user\":\"A\",\"to_user\":\"B\",\"amount\":50,\"currency\":\"USD\"}";

        // Act
        Throwable error = catchThrowable(() -> objectMapper.readValue(json, TransferRequest.class));

        // Assert: la causa es la validacion Fail-Fast de Divisa (el ManejadorErrores la vuelve 400)
        assertThat(error).hasRootCauseInstanceOf(IllegalArgumentException.class)
                .hasRootCauseMessage("Divisa no admitida: USD");
    }
}
