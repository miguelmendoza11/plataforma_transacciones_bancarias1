package com.banco.ledger.notification.domain;

/** PATRON STRATEGY: como se resuelve el email de un usuario (en memoria, BD, LDAP...). */
public interface DirectorioUsuarios {
    String emailDe(String usuario);
}
