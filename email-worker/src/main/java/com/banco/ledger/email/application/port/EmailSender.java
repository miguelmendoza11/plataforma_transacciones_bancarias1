package com.banco.ledger.email.application.port;

import com.banco.ledger.contracts.EmailCommandRecord;

/** Puerto de salida: como se "envia" un correo. La logica no sabe si es consola, SMTP, etc. */
public interface EmailSender {
    void enviar(EmailCommandRecord comando);
}
