package com.banco.ledger.processor.infrastructure.messaging;

import com.banco.ledger.contracts.Topologia;
import com.banco.ledger.contracts.TransferCommandRecord;
import com.banco.ledger.processor.application.ProcesarTransferenciaService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Adaptador de entrada: consume la Work Queue transfer_commands y delega en el caso de uso.
 * No tiene logica de negocio. Si el servicio lanza una excepcion, Spring AMQP reintenta
 * (3 intentos) y luego manda el mensaje a la DLQ.
 */
@Component
public class TransferCommandListener {

    private final ProcesarTransferenciaService procesarTransferencia;

    public TransferCommandListener(ProcesarTransferenciaService procesarTransferencia) {
        this.procesarTransferencia = Objects.requireNonNull(procesarTransferencia);
    }

    @RabbitListener(queues = Topologia.COLA_TRANSFER_COMMANDS)
    public void onMessage(TransferCommandRecord comando) {
        procesarTransferencia.procesar(comando);
    }
}
