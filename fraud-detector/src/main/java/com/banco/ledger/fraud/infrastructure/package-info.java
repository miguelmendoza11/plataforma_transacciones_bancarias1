/**
 * TODO (Miguel):
 *  - messaging: TransactionsLogListener (@KafkaListener topic transactions_log, group fraud-detector),
 *    KafkaFraudAlertPublisher (key = tx_id), NewTopic fraud_alerts.
 *  - persistence: AlertaFraudeJpaEntity + adaptador que implementa AlertaFraudeRepository.
 */
package com.banco.ledger.fraud.infrastructure;
