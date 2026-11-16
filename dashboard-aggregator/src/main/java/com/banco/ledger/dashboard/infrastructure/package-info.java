/**
 * TODO (Juanse):
 *  - Dos @KafkaListener: transactions_log y fraud_alerts (group dashboard-aggregator).
 *  - WebSocket en puerto 8081 (endpoint /ws/transactions), con TextWebSocketHandler o STOMP.
 *    Al conectar un cliente: enviar snapshot completo; luego cambios incrementales.
 *  - WebSocketDashboardNotifier implements DashboardNotifier.
 */
package com.banco.ledger.dashboard.infrastructure;
