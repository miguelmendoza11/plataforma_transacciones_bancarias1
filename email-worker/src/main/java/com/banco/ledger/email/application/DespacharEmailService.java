package com.banco.ledger.email.application;

import com.banco.ledger.contracts.EmailCommandRecord;
import com.banco.ledger.email.application.port.EmailSender;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Caso de uso: despachar un comando de email.
 * DIP: depende de la abstraccion EmailSender, inyectada por constructor.
 */
@Service
public class DespacharEmailService {

    private final EmailSender emailSender;

    public DespacharEmailService(EmailSender emailSender) {
        this.emailSender = Objects.requireNonNull(emailSender);
    }

    public void despachar(EmailCommandRecord comando) {
        Objects.requireNonNull(comando, "comando no puede ser nulo");
        emailSender.enviar(comando);
    }
}
