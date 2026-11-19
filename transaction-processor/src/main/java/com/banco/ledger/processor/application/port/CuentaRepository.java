package com.banco.ledger.processor.application.port;

import com.banco.ledger.processor.domain.model.CuentaBancaria;

import java.util.Optional;

/**
 * Puerto de persistencia de cuentas (DIP). La implementacion (adaptador JPA) vive en
 * infrastructure/persistence y traduce CuentaBancaria <-> CuentaJpaEntity.
 */
public interface CuentaRepository {

    Optional<CuentaBancaria> buscarPorUsuario(String usuario);

    void guardar(CuentaBancaria cuenta);
}
