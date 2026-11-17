package com.banco.ledger.api.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * DTO de entrada del POST /transfer: { "from_user": "A", "to_user": "B", "amount": 50 }.
 *
 * TODO (Juanse): constructor compacto Fail-Fast (usar Validaciones.noVacio y MontoDinero),
 * o convertir directamente a TransferCommandRecord en el servicio.
 */
public record TransferRequest(
        @JsonProperty("from_user") String fromUser,
        @JsonProperty("to_user") String toUser,
        @JsonProperty("amount") BigDecimal amount) {
}
