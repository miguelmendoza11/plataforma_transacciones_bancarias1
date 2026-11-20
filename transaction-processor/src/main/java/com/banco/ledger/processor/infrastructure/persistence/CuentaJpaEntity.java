package com.banco.ledger.processor.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;

/**
 * Tabla "cuentas". Entidad JPA SEPARADA del dominio (CuentaBancaria): el dominio no sabe que
 * existe JPA y esta clase no tiene reglas de negocio.
 *
 * {@code @Version} activa el BLOQUEO OPTIMISTA: si dos workers cargan la misma cuenta y ambos
 * intentan guardarla, el segundo UPDATE no encuentra la version esperada y Hibernate lanza
 * una excepcion en vez de pisar el saldo (evita el "doble gasto").
 */
@Entity
@Table(name = "cuentas")
public class CuentaJpaEntity {

    @Id
    @Column(name = "usuario", nullable = false)
    private String usuario;

    @Column(name = "saldo", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    /** Requerido por JPA. */
    protected CuentaJpaEntity() {
    }

    CuentaJpaEntity(String usuario, BigDecimal saldo) {
        this.usuario = usuario;
        this.saldo = saldo;
    }

    void actualizarSaldo(BigDecimal nuevoSaldo) {
        this.saldo = nuevoSaldo;
    }

    String usuario() {
        return usuario;
    }

    BigDecimal saldo() {
        return saldo;
    }

    long version() {
        return version;
    }
}
