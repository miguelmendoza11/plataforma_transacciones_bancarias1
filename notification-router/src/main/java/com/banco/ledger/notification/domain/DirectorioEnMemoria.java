package com.banco.ledger.notification.domain;

import com.banco.ledger.contracts.Validaciones;

import java.util.Locale;

/** Strategy en memoria: "A" -> "user_a@email.com". */
public final class DirectorioEnMemoria implements DirectorioUsuarios {

    @Override
    public String emailDe(String usuario) {
        String limpio = Validaciones.noVacio(usuario, "usuario");
        return "user_" + limpio.toLowerCase(Locale.ROOT) + "@email.com";
    }
}
