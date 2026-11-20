package com.banco.ledger.fraud.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

/** Tabla "alertas_fraude" para auditoria. Separada de la entidad de dominio AlertaFraude. */
@Entity
@Table(name = "alertas_fraude",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tx_id", "reason"}))
public class AlertaFraudeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tx_id", nullable = false)
    private String txId;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "detected_at", nullable = false)
    private Instant detectedAt;

    protected AlertaFraudeJpaEntity() {
    }

    AlertaFraudeJpaEntity(String txId, String reason, Instant detectedAt) {
        this.txId = txId;
        this.reason = reason;
        this.detectedAt = detectedAt;
    }
}
