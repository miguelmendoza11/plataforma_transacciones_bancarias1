package com.banco.ledger.api.application;

import com.banco.ledger.api.application.port.TransferCommandPublisher;
import com.banco.ledger.api.web.TransferRequest;
import com.banco.ledger.contracts.TransferCommandRecord;
import com.banco.ledger.contracts.TxId;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Objects;

/**
 * Caso de uso: solicitar una transferencia (lado Command de CQRS).
 * DIP: publica a traves del puerto TransferCommandPublisher, sin conocer RabbitMQ.
 */
@Service
public class SolicitarTransferenciaService {

    private final TransferCommandPublisher publisher;
    private final Clock clock;

    public SolicitarTransferenciaService(TransferCommandPublisher publisher, Clock clock) {
        this.publisher = Objects.requireNonNull(publisher);
        this.clock = Objects.requireNonNull(clock);
    }

    public TxId solicitar(TransferRequest request) {
        Objects.requireNonNull(request, "request no puede ser nulo");
        TxId txId = TxId.nuevo();
        TransferCommandRecord comando = request.toComando(txId, clock.instant());
        publisher.publicar(comando);
        return txId;
    }
}
