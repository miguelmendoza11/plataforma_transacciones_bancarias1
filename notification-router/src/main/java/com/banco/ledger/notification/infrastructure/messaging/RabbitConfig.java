package com.banco.ledger.notification.infrastructure.messaging;

import com.banco.ledger.contracts.Topologia;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declara email_queue IGUAL que email-worker y ad-generator (durable, sin argumentos);
 * si se declara distinta RabbitMQ responde PRECONDITION_FAILED.
 */
@Configuration
public class RabbitConfig {

    @Bean
    Queue emailQueue() {
        return QueueBuilder.durable(Topologia.COLA_EMAIL).build();
    }

    @Bean
    MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
