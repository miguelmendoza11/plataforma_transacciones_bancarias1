package com.banco.ledger.api.infrastructure.messaging;

import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.Topologia;
import com.banco.ledger.contracts.TransferCommandRecord;
import com.banco.ledger.contracts.TxId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RabbitTransferCommandPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitTransferCommandPublisher publisher;

    @Test
    void enviaElComandoALaColaUnaVez() {
        // Arrange
        var comando = new TransferCommandRecord(TxId.de("tx-1"), "A", "B",
                MontoDinero.de("50"), Instant.parse("2026-01-01T10:00:00Z"));

        // Act
        publisher.publicar(comando);

        // Assert
        verify(rabbitTemplate, times(1)).convertAndSend(Topologia.COLA_TRANSFER_COMMANDS, comando);
    }

    @Test
    void rechazaComandoNulo() {
        // Act + Assert
        assertThatThrownBy(() -> publisher.publicar(null))
                .isInstanceOf(NullPointerException.class);
        verifyNoInteractions(rabbitTemplate);
    }
}
