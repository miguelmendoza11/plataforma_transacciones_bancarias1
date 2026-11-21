package com.banco.ledger.processor.infrastructure.messaging;

import com.banco.ledger.contracts.Topologia;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** Crea el topic del ledger en Kafka si no existe. */
@Configuration
public class KafkaConfig {

    @Bean
    NewTopic transactionsLogTopic() {
        return TopicBuilder.name(Topologia.TOPIC_TRANSACTIONS_LOG)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
