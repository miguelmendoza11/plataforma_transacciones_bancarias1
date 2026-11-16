/**
 * TODO (Juanse):
 *  - RabbitConfig: Queue durable transfer_commands + Jackson2JsonMessageConverter (ver email-worker).
 *  - RabbitTransferCommandPublisher implements TransferCommandPublisher
 *    (rabbitTemplate.convertAndSend(Topologia.COLA_TRANSFER_COMMANDS, comando)).
 */
package com.banco.ledger.api.infrastructure.messaging;
