package com.banco.ledger.fraud.infrastructure.messaging;

import com.banco.ledger.contracts.Topologia;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** Crea el topic de alertas en Kafka si no existe. */
@Configuration
public class KafkaConfig {

    @Bean
    NewTopic fraudAlertsTopic() {
        return TopicBuilder.name(Topologia.TOPIC_FRAUD_ALERTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
