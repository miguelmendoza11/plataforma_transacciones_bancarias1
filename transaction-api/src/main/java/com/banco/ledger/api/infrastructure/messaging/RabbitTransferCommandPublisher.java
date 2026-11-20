package com.banco.ledger.api.infrastructure.messaging;

import com.banco.ledger.api.application.port.TransferCommandPublisher;
import com.banco.ledger.contracts.Topologia;
import com.banco.ledger.contracts.TransferCommandRecord;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Adaptador de salida (patron Adapter / puerto-adaptador): publica el comando en RabbitMQ.
 * Usa el exchange por defecto con routing key = nombre de la cola (Work Queue).
 */
@Component
public class RabbitTransferCommandPublisher implements TransferCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitTransferCommandPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = Objects.requireNonNull(rabbitTemplate);
    }

    @Override
    public void publicar(TransferCommandRecord comando) {
        Objects.requireNonNull(comando, "comando no puede ser nulo");
        rabbitTemplate.convertAndSend(Topologia.COLA_TRANSFER_COMMANDS, comando);
    }
}
