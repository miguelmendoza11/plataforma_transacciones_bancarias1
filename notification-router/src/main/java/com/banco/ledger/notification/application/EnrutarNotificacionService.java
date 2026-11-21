package com.banco.ledger.notification.application;

import com.banco.ledger.contracts.EmailCommandRecord;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.notification.application.port.EmailCommandPublisher;
import com.banco.ledger.notification.domain.DirectorioUsuarios;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Caso de uso: por cada transaccion COMPLETED arma el comando de email y lo publica.
 * DIP: depende de la Strategy DirectorioUsuarios y del puerto EmailCommandPublisher.
 */
@Service
public class EnrutarNotificacionService {

    private final DirectorioUsuarios directorio;
    private final EmailCommandPublisher publisher;

    public EnrutarNotificacionService(DirectorioUsuarios directorio, EmailCommandPublisher publisher) {
        this.directorio = Objects.requireNonNull(directorio);
        this.publisher = Objects.requireNonNull(publisher);
    }

    public void enrutar(TransactionEventRecord evento) {
        Objects.requireNonNull(evento, "evento no puede ser nulo");
        if (!evento.estaCompletada()) {
            return; // los REJECTED no generan correo
        }
        String to = directorio.emailDe(evento.fromUser());
        String body = "Envió " + evento.amount() + " a " + evento.toUser();
        publisher.publicar(new EmailCommandRecord(to, body));
    }
}
