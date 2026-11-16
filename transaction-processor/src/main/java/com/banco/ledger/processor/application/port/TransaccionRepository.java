package com.banco.ledger.processor.application.port;

/**
 * Puerto de persistencia (DIP). La implementacion (adaptador JPA) vive en
 * infrastructure/persistence y traduce Transaccion <-> TransaccionJpaEntity.
 *
 * TODO (Miguel): void guardar(Transaccion tx); Optional<Transaccion> buscarPorId(TxId id);
 */
public interface TransaccionRepository {
}
