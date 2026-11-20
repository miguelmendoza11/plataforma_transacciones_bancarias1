package com.banco.ledger.processor.infrastructure.persistence;

import com.banco.ledger.processor.domain.model.CuentaBancaria;
import com.banco.ledger.processor.domain.model.Saldo;

/**
 * Traduce CuentaBancaria (dominio) <-> CuentaJpaEntity (tabla). Mantiene el dominio libre de JPA.
 *
 * Para ACTUALIZAR se modifica la entidad ya cargada (no se crea una nueva): asi Hibernate
 * conserva la version leida y el bloqueo optimista (@Version) puede detectar conflictos.
 */
final class CuentaMapper {

    private CuentaMapper() {
        throw new AssertionError("No instanciable");
    }

    static CuentaBancaria toDomain(CuentaJpaEntity entidad) {
        return CuentaBancaria.reconstituir(entidad.usuario(), Saldo.de(entidad.saldo()));
    }

    static CuentaJpaEntity toEntity(CuentaBancaria cuenta) {
        return new CuentaJpaEntity(cuenta.usuario(), cuenta.saldo().valor());
    }

    static void actualizar(CuentaJpaEntity entidad, CuentaBancaria cuenta) {
        entidad.actualizarSaldo(cuenta.saldo().valor());
    }
}
