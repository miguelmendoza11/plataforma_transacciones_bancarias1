package com.banco.ledger.email.infrastructure.console;

import com.banco.ledger.contracts.EmailCommandRecord;
import com.banco.ledger.email.application.port.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Adaptador que simula el envio imprimiendo en consola. */
@Component
public class ConsoleEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailSender.class);

    @Override
    public void enviar(EmailCommandRecord comando) {
        // El hostname permite ver en los logs que replica de email-worker atendio el mensaje
        log.info("ENVIANDO EMAIL a [{}]: [{}] (worker={})",
                comando.to(), comando.body(), System.getenv().getOrDefault("HOSTNAME", "local"));
    }
}
