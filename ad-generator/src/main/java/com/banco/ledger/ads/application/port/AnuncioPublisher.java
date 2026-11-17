package com.banco.ledger.ads.application.port;

import com.banco.ledger.contracts.EmailCommandRecord;

/** Puerto de salida hacia email_queue. */
public interface AnuncioPublisher {
    void publicar(EmailCommandRecord anuncio);
}
