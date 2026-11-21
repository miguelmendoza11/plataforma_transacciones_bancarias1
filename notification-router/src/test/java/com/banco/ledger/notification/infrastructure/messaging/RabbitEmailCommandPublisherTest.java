package com.banco.ledger.notification.infrastructure.messaging;

import com.banco.ledger.contracts.EmailCommandRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitEmailCommandPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitEmailCommandPublisher publisher;

    @Test
    void publicaElComandoEnEmailQueue() {
        // Arrange
        var comando = new EmailCommandRecord("user_a@email.com", "Envió $50.00 a B");

        // Act
        publisher.publicar(comando);

        // Assert
        verify(rabbitTemplate, times(1)).convertAndSend("email_queue", comando);
    }
}
