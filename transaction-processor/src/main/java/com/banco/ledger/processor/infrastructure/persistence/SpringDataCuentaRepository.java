package com.banco.ledger.processor.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio Spring Data de la tabla "cuentas". Solo lo usa el adaptador JpaCuentaRepositoryAdapter. */
public interface SpringDataCuentaRepository extends JpaRepository<CuentaJpaEntity, String> {
}
