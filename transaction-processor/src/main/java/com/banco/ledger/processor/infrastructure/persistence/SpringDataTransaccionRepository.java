package com.banco.ledger.processor.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio Spring Data de la tabla "transacciones". Solo lo usa el adaptador JpaTransaccionRepositoryAdapter. */
public interface SpringDataTransaccionRepository extends JpaRepository<TransaccionJpaEntity, String> {
}
