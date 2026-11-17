/**
 * Adaptadores de mensajeria.
 *
 * TODO (Miguel):
 *  - RabbitConfig: Queue durable "transfer_commands" + Jackson2JsonMessageConverter
 *    (copiar el patron de email-worker).
 *  - TransferCommandListener: @RabbitListener(queues = Topologia.COLA_TRANSFER_COMMANDS)
 *    que delega en ProcesarTransferenciaService.
 *  - KafkaLedgerPublisher implements LedgerEventPublisher:
 *    kafkaTemplate.send(Topologia.TOPIC_TRANSACTIONS_LOG, evento.txId().valor(), evento)
 *    (tx_id como KEY -> orden garantizado por transaccion dentro de la particion).
 *  - KafkaConfig: NewTopic transactions_log + JsonSerializer en application.yml.
 */
package com.banco.ledger.processor.infrastructure.messaging;
