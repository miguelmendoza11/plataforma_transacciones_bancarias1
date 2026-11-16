/**
 * Adaptadores de persistencia (PostgreSQL / JPA).
 *
 * TODO (Miguel):
 *  - TransaccionJpaEntity, CuentaJpaEntity (@Entity) -> SEPARADAS del dominio.
 *  - SpringDataTransaccionRepo extends JpaRepository<TransaccionJpaEntity, String>.
 *  - JpaTransaccionRepositoryAdapter implements TransaccionRepository (mapea dominio <-> entidad).
 *  - Para CuentaJpaEntity usar @Version (bloqueo optimista) -> evita doble gasto
 *    cuando dos workers procesan en paralelo transferencias de la misma cuenta
 *    (tema de concurrencia de Sistemas Operativos / Distribuidos).
 */
package com.banco.ledger.processor.infrastructure.persistence;
