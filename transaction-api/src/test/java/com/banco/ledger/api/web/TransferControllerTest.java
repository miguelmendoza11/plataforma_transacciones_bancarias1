package com.banco.ledger.api.web;

import com.banco.ledger.api.application.SolicitarTransferenciaService;
import com.banco.ledger.contracts.TxId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @Mock
    private SolicitarTransferenciaService service;

    @InjectMocks
    private TransferController controller;

    @Test
    void respondeAcceptedConElTxId() {
        // Arrange
        var request = new TransferRequest("A", "B", new BigDecimal("50"));
        when(service.solicitar(request)).thenReturn(TxId.de("tx-1"));

        // Act
        var respuesta = controller.transferir(request);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(respuesta.getBody()).isEqualTo(Map.of("tx_id", "tx-1"));
        verify(service, times(1)).solicitar(request);
    }
}
