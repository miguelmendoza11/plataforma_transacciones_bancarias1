package com.banco.ledger.dashboard.infrastructure.websocket;

import com.banco.ledger.dashboard.domain.TransaccionVista;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/** DTO del mensaje WebSocket en snake_case (el formato que espera dashboard.html). */
public record TransaccionMensaje(
        @JsonProperty("tx_id") String txId,
        @JsonProperty("from_user") String fromUser,
        @JsonProperty("to_user") String toUser,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("status") String status,
        @JsonProperty("sospechosa") boolean sospechosa,
        @JsonProperty("motivo_fraude") String motivoFraude) {

    public static TransaccionMensaje desde(TransaccionVista vista) {
        return new TransaccionMensaje(vista.txId().valor(), vista.fromUser(), vista.toUser(),
                vista.amount().valor(), vista.status().name(), vista.sospechosa(), vista.motivoFraude());
    }
}
