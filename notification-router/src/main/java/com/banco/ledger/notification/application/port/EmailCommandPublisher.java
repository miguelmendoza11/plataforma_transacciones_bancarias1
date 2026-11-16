package com.banco.ledger.notification.application.port;

import com.banco.ledger.contracts.EmailCommandRecord;

/** Puerto de salida hacia email_queue (RabbitMQ). */
public interface EmailCommandPublisher {
    void publicar(EmailCommandRecord comando);
}
