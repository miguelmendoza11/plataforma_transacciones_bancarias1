package com.banco.ledger.dashboard.infrastructure.messaging;

import com.banco.ledger.contracts.FraudAlertRecord;
import com.banco.ledger.contracts.Topologia;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.dashboard.application.ProyectarLedgerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Adaptadores de entrada: leen los dos topics de Kafka y delegan en el caso de uso.
 * Cada listener corre en su propio hilo, por eso la vista es concurrente.
 */
@Component
public class LedgerListeners {

    private final ProyectarLedgerService proyectar;

    public LedgerListeners(ProyectarLedgerService proyectar) {
        this.proyectar = Objects.requireNonNull(proyectar);
    }

    @KafkaListener(topics = Topologia.TOPIC_TRANSACTIONS_LOG)
    public void onTransaccion(TransactionEventRecord evento) {
        proyectar.alRecibirTransaccion(evento);
    }

    // este topic trae otro tipo, se sobrescribe el tipo por defecto del yml
    @KafkaListener(topics = Topologia.TOPIC_FRAUD_ALERTS,
            properties = "spring.json.value.default.type=com.banco.ledger.contracts.FraudAlertRecord")
    public void onAlerta(FraudAlertRecord alerta) {
        proyectar.alRecibirAlerta(alerta);
    }
}
