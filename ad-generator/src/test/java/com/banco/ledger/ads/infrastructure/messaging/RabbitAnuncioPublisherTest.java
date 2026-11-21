package com.banco.ledger.ads.infrastructure.messaging;

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
class RabbitAnuncioPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitAnuncioPublisher publisher;

    @Test
    void publicaElAnuncioEnEmailQueue() {
        // Arrange
        var anuncio = new EmailCommandRecord("all_users@banco.com", "¡Obtenga nuestra nueva Tarjeta de Crédito!");

        // Act
        publisher.publicar(anuncio);

        // Assert
        verify(rabbitTemplate, times(1)).convertAndSend("email_queue", anuncio);
    }
}
