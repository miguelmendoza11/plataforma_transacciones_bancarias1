/**
 * TODO (Andres):
 *  - EnrutarNotificacionService: si el evento esta COMPLETED, construir
 *    EmailCommandRecord("user_a@email.com", "Envio $50 a B") y publicarlo. Si no, ignorar.
 *  - Estrategia para resolver el email del usuario (ej. interfaz DirectorioUsuarios con una
 *    implementacion en memoria: "A" -> "user_a@email.com").
 *  - Prueba Mockito: COMPLETED -> verify(publisher).publicar(...); REJECTED -> verifyNoInteractions.
 */
package com.banco.ledger.notification.application;
