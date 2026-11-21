package com.banco.ledger.fraud.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAlertaFraudeRepository extends JpaRepository<AlertaFraudeJpaEntity, Long> {

    boolean existsByTxIdAndReason(String txId, String reason);
}
