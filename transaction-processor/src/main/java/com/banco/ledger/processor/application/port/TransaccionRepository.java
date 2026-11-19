package com.banco.ledger.processor.application.port;

import com.banco.ledger.contracts.TxId;
import com.banco.ledger.processor.domain.model.Transaccion;

/**
 * Puerto de persistencia de transacciones (DIP). La implementacion (adaptador JPA) vive en
 * infrastructure/persistence y traduce Transaccion <-> TransaccionJpaEntity.
 * El caso de uso solo conoce esta interfaz, no PostgreSQL.
 */
public interface TransaccionRepository {

    void guardar(Transaccion tx);

    /** Para la idempotencia: RabbitMQ puede reentregar el mismo comando. */
    boolean existe(TxId id);
}
