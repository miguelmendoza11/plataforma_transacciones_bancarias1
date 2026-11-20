package com.banco.ledger.processor.infrastructure.persistence;

import com.banco.ledger.contracts.TxId;
import com.banco.ledger.processor.application.port.TransaccionRepository;
import com.banco.ledger.processor.domain.model.Transaccion;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Objects;

/**
 * Adaptador (arquitectura hexagonal): implementa el puerto TransaccionRepository con Spring Data JPA.
 * Registra cada transaccion procesada en la tabla "transacciones" (historico para auditoria).
 */
@Component
public class JpaTransaccionRepositoryAdapter implements TransaccionRepository {

    private final SpringDataTransaccionRepository repository;
    private final Clock clock;

    public JpaTransaccionRepositoryAdapter(SpringDataTransaccionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public void guardar(Transaccion tx) {
        repository.save(TransaccionMapper.toEntity(tx, clock.instant()));
    }

    @Override
    public boolean existe(TxId id) {
        return repository.existsById(id.valor());
    }
}
