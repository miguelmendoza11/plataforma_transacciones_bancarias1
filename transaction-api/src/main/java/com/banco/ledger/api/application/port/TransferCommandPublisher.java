package com.banco.ledger.api.application.port;

import com.banco.ledger.contracts.TransferCommandRecord;

/** Puerto de salida (DIP): el caso de uso no conoce RabbitTemplate. */
public interface TransferCommandPublisher {
    void publicar(TransferCommandRecord comando);
}
