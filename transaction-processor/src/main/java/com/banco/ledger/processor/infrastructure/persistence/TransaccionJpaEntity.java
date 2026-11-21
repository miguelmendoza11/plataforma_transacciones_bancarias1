package com.banco.ledger.processor.infrastructure.persistence;

import com.banco.ledger.contracts.EstadoTransaccion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Tabla "transacciones": historico del ledger para auditoria. Entidad JPA SEPARADA del
 * dominio (Transaccion); el estado se guarda solo por su nombre (STRING) y el dominio lo
 * rehidrata con EstadoTransaccionState.desde(...).
 */
@Entity
@Table(name = "transacciones")
public class TransaccionJpaEntity {

    @Id
    @Column(name = "tx_id", nullable = false)
    private String txId;

    @Column(name = "from_user", nullable = false)
    private String fromUser;

    @Column(name = "to_user", nullable = false)
    private String toUser;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EstadoTransaccion status;

    @Column(name = "reason")
    private String reason;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    /** Requerido por JPA. */
    protected TransaccionJpaEntity() {
    }

    TransaccionJpaEntity(String txId, String fromUser, String toUser, BigDecimal amount,
                         EstadoTransaccion status, String reason, Instant requestedAt, Instant processedAt) {
        this.txId = txId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.status = status;
        this.reason = reason;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
    }

    String txId() {
        return txId;
    }

    String fromUser() {
        return fromUser;
    }

    String toUser() {
        return toUser;
    }

    BigDecimal amount() {
        return amount;
    }

    EstadoTransaccion status() {
        return status;
    }

    String reason() {
        return reason;
    }

    Instant requestedAt() {
        return requestedAt;
    }

    Instant processedAt() {
        return processedAt;
    }
}
