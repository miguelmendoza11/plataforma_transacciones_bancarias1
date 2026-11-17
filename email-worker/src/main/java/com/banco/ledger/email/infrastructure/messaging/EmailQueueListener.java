package com.banco.ledger.email.infrastructure.messaging;

import com.banco.ledger.contracts.EmailCommandRecord;
import com.banco.ledger.contracts.Topologia;
import com.banco.ledger.email.application.DespacharEmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada: consume la Work Queue email_queue. Varias replicas de este
 * servicio compiten por los mensajes (cada mensaje lo procesa UNA sola replica).
 */
@Component
public class EmailQueueListener {

    private final DespacharEmailService despacharEmail;

    public EmailQueueListener(DespacharEmailService despacharEmail) {
        this.despacharEmail = despacharEmail;
    }

    @RabbitListener(queues = Topologia.COLA_EMAIL)
    public void onMessage(EmailCommandRecord comando) {
        despacharEmail.despachar(comando);
    }
}
