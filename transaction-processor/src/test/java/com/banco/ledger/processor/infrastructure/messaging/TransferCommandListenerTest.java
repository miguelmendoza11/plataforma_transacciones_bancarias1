package com.banco.ledger.processor.infrastructure.messaging;

import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransferCommandRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.processor.application.ProcesarTransferenciaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransferCommandListenerTest {

    @Mock
    private ProcesarTransferenciaService service;

    @InjectMocks
    private TransferCommandListener listener;

    @Test
    void delegaElComandoAlServicioUnaVez() {
        // Arrange
        var cmd = new TransferCommandRecord(TxId.de("tx-1"), "A", "B", MontoDinero.de("50"), Instant.now());

        // Act
        listener.onMessage(cmd);

        // Assert
        verify(service, times(1)).procesar(cmd);
    }
}
