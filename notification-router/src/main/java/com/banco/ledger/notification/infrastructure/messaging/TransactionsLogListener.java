package com.banco.ledger.notification.infrastructure.messaging;

import com.banco.ledger.contracts.Topologia;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.notification.application.EnrutarNotificacionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/** Adaptador de entrada del puente Kafka -> RabbitMQ: lee transactions_log y delega en el caso de uso. */
@Component
public class TransactionsLogListener {

    private final EnrutarNotificacionService enrutarNotificacion;

    public TransactionsLogListener(EnrutarNotificacionService enrutarNotificacion) {
        this.enrutarNotificacion = Objects.requireNonNull(enrutarNotificacion);
    }

    @KafkaListener(topics = Topologia.TOPIC_TRANSACTIONS_LOG)
    public void onMessage(TransactionEventRecord evento) {
        enrutarNotificacion.enrutar(evento);
    }
}
