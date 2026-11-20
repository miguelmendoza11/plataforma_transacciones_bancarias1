package com.banco.ledger.processor.infrastructure.persistence;

import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.processor.domain.model.Transaccion;

import java.time.Instant;

/** Traduce Transaccion (dominio) <-> TransaccionJpaEntity (tabla). Mantiene el dominio libre de JPA. */
final class TransaccionMapper {

    private TransaccionMapper() {
        throw new AssertionError("No instanciable");
    }

    static TransaccionJpaEntity toEntity(Transaccion tx, Instant procesadaEn) {
        return new TransaccionJpaEntity(
                tx.txId().valor(),
                tx.origen(),
                tx.destino(),
                tx.monto().valor(),
                tx.estado(),
                tx.motivoRechazo().orElse(null),
                tx.solicitadaEn(),
                procesadaEn);
    }

    static Transaccion toDomain(TransaccionJpaEntity entidad) {
        return Transaccion.reconstituir(
                TxId.de(entidad.txId()),
                entidad.fromUser(),
                entidad.toUser(),
                MontoDinero.de(entidad.amount()),
                entidad.requestedAt(),
                entidad.status(),
                entidad.reason());
    }
}
