package com.banco.ledger.api.web;

import com.banco.ledger.api.application.SolicitarTransferenciaService;
import com.banco.ledger.contracts.TxId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * Adaptador de entrada REST: POST /transfer.
 * Responde 202 Accepted porque la transferencia se procesa despues, de forma asincrona.
 */
@RestController
public class TransferController {

    private final SolicitarTransferenciaService service;

    public TransferController(SolicitarTransferenciaService service) {
        this.service = Objects.requireNonNull(service);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transferir(@RequestBody TransferRequest request) {
        TxId txId = service.solicitar(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("tx_id", txId.valor()));
    }
}
