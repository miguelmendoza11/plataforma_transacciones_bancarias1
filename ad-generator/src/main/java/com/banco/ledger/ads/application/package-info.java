/**
 * TODO (Andres):
 *  - CatalogoAnuncios (lista inmutable de mensajes; rotacion round-robin o aleatoria).
 *  - GenerarAnuncioService: toma el siguiente anuncio y lo publica como
 *    EmailCommandRecord("all_users@banco.com", "¡Obtenga nuestra nueva Tarjeta de Credito!").
 *  - @EnableScheduling + @Scheduled(fixedRateString = "${ads.intervalo-ms}") en un adaptador
 *    de infraestructura que llame al servicio (asi el servicio se prueba sin reloj).
 */
package com.banco.ledger.ads.application;
