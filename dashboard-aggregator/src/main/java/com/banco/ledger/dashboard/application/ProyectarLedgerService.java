package com.banco.ledger.dashboard.application;

import com.banco.ledger.contracts.FraudAlertRecord;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.dashboard.application.port.DashboardNotifier;
import com.banco.ledger.dashboard.domain.VistaMaterializada;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Caso de uso: proyectar los eventos del ledger en la vista materializada (lado Query de CQRS)
 * y difundir cada cambio. DIP: difunde por el puerto DashboardNotifier.
 */
@Service
public class ProyectarLedgerService {

    private final VistaMaterializada vista;
    private final DashboardNotifier notifier;

    public ProyectarLedgerService(VistaMaterializada vista, DashboardNotifier notifier) {
        this.vista = Objects.requireNonNull(vista);
        this.notifier = Objects.requireNonNull(notifier);
    }

    public void alRecibirTransaccion(TransactionEventRecord evento) {
        notifier.difundir(vista.registrar(evento));
    }

    // si la alerta quedo pendiente no se difunde: saldra marcada cuando llegue la transaccion
    public void alRecibirAlerta(FraudAlertRecord alerta) {
        vista.marcarSospechosa(alerta).ifPresent(notifier::difundir);
    }
}
