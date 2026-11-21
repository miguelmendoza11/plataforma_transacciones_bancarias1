package com.banco.ledger.ads.infrastructure.messaging;

import com.banco.ledger.ads.application.port.AnuncioPublisher;
import com.banco.ledger.contracts.EmailCommandRecord;
import com.banco.ledger.contracts.Topologia;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/** Adaptador de salida (puerto-adaptador): publica el anuncio en la Work Queue email_queue. */
@Component
public class RabbitAnuncioPublisher implements AnuncioPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitAnuncioPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = Objects.requireNonNull(rabbitTemplate);
    }

    @Override
    public void publicar(EmailCommandRecord anuncio) {
        Objects.requireNonNull(anuncio, "anuncio no puede ser nulo");
        rabbitTemplate.convertAndSend(Topologia.COLA_EMAIL, anuncio);
    }
}
