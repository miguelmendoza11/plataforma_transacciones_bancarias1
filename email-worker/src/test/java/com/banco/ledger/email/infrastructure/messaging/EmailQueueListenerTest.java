package com.banco.ledger.email.infrastructure.messaging;

import com.banco.ledger.contracts.EmailCommandRecord;
import com.banco.ledger.email.application.DespacharEmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailQueueListenerTest {

    @Mock
    private DespacharEmailService service;

    @InjectMocks
    private EmailQueueListener listener;

    @Test
    void delegaElComandoAlServicioUnaVez() {
        // Arrange
        var comando = new EmailCommandRecord("user_a@email.com", "Envió $50.00 a B");

        // Act
        listener.onMessage(comando);

        // Assert
        verify(service, times(1)).despachar(comando);
    }
}
