package com.banco.ledger.api.infrastructure.messaging;

import com.banco.ledger.contracts.Topologia;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia RabbitMQ de la API (productor de la Work Queue transfer_commands).
 * La cola se declara con los MISMOS argumentos que en transaction-processor (DLQ);
 * si no, RabbitMQ responde PRECONDITION_FAILED.
 */
@Configuration
public class RabbitConfig {

    @Bean
    Queue transferCommandsQueue() {
        return QueueBuilder.durable(Topologia.COLA_TRANSFER_COMMANDS)
                .deadLetterExchange("")
                .deadLetterRoutingKey(Topologia.COLA_TRANSFER_COMMANDS_DLQ)
                .build();
    }

    @Bean
    Queue transferCommandsDeadLetterQueue() {
        return QueueBuilder.durable(Topologia.COLA_TRANSFER_COMMANDS_DLQ).build();
    }

    @Bean
    MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
