package com.banco.ledger.processor.infrastructure.messaging;

import com.banco.ledger.contracts.Topologia;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia RabbitMQ del processor.
 *
 * transfer_commands es una Work Queue: si hay varias instancias del processor, compiten por
 * los mensajes y cada comando lo procesa UNA sola. Si un comando falla 3 veces (retry en
 * application.yml), RabbitMQ lo reenvia a transfer_commands.dlq (Dead Letter Queue) en vez de
 * descartarlo, para poder revisarlo despues.
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
