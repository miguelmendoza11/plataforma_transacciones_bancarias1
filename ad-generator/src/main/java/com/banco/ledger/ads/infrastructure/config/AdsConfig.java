package com.banco.ledger.ads.infrastructure.config;

import com.banco.ledger.ads.domain.CatalogoAnuncios;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** Registra el catalogo de anuncios (el dominio no conoce Spring). */
@Configuration
public class AdsConfig {

    @Bean
    CatalogoAnuncios catalogoAnuncios() {
        return new CatalogoAnuncios(List.of(
                "¡Obtenga nuestra nueva Tarjeta de Crédito!",
                "Abra su CDT digital y gane intereses desde el primer día",
                "Transfiera sin costo a cualquier usuario del banco",
                "Active las alertas de seguridad en su perfil"));
    }
}
