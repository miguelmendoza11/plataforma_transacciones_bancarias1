package com.banco.ledger.email.application;

import com.banco.ledger.contracts.EmailCommandRecord;
import com.banco.ledger.email.application.port.EmailSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/** Plantilla de prueba de servicio con Mockito (sin Spring, sin RabbitMQ). */
@ExtendWith(MockitoExtension.class)
class DespacharEmailServiceTest {

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private DespacharEmailService service;

    @Test
    void despachaExactamenteUnaVez() {
        // Arrange
        var cmd = new EmailCommandRecord("user_a@email.com", "Envio $50 a B");
        // Act
        service.despachar(cmd);
        // Assert
        verify(emailSender, times(1)).enviar(cmd);
    }

    @Test
    void rechazaComandoNulo() {
        assertThatThrownBy(() -> service.despachar(null)).isInstanceOf(NullPointerException.class);
        verifyNoInteractions(emailSender);
    }
}
