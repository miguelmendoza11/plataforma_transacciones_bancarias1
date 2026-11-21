package com.banco.ledger.notification.infrastructure.messaging;

import com.banco.ledger.contracts.EmailCommandRecord;
import com.banco.ledger.contracts.Topologia;
import com.banco.ledger.notification.application.port.EmailCommandPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Adaptador de salida (puerto-adaptador): publica el comando en la Work Queue email_queue
 * usando el exchange por defecto (routing key = nombre de la cola).
 */
@Component
public class RabbitEmailCommandPublisher implements EmailCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitEmailCommandPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = Objects.requireNonNull(rabbitTemplate);
    }

    @Override
    public void publicar(EmailCommandRecord comando) {
        Objects.requireNonNull(comando, "comando no puede ser nulo");
        rabbitTemplate.convertAndSend(Topologia.COLA_EMAIL, comando);
    }
}
