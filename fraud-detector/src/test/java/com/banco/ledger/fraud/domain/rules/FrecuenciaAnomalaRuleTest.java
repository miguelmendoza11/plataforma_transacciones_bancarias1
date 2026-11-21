package com.banco.ledger.fraud.domain.rules;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TxId;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class FrecuenciaAnomalaRuleTest {

    private static final Instant T0 = Instant.parse("2026-11-18T11:30:00Z");

    private static TransactionEventRecord evento(String usuario, Instant momento) {
        return new TransactionEventRecord(TxId.nuevo(), usuario, "Z", MontoDinero.de("50"),
                EstadoTransaccion.COMPLETED, null, momento);
    }

    @Test
    void hastaElMaximoNoDispara() {
        // Arrange
        var regla = new FrecuenciaAnomalaRule(3, Duration.ofSeconds(60));

        // Act
        Optional<String> primera = regla.evaluar(evento("A", T0));
        Optional<String> segunda = regla.evaluar(evento("A", T0.plusSeconds(10)));
        Optional<String> tercera = regla.evaluar(evento("A", T0.plusSeconds(20)));

        // Assert
        assertThat(primera).isEmpty();
        assertThat(segunda).isEmpty();
        assertThat(tercera).isEmpty();
    }

    @Test
    void pasarElMaximoDentroDeLaVentanaDisparaHighFrequency() {
        // Arrange
        var regla = new FrecuenciaAnomalaRule(3, Duration.ofSeconds(60));
        regla.evaluar(evento("A", T0));
        regla.evaluar(evento("A", T0.plusSeconds(10)));
        regla.evaluar(evento("A", T0.plusSeconds(20)));

        // Act
        Optional<String> cuarta = regla.evaluar(evento("A", T0.plusSeconds(30)));

        // Assert
        assertThat(cuarta).contains(MotivosFraude.HIGH_FREQUENCY);
    }

    @Test
    void lasTransaccionesViejasSalenDeLaVentana() {
        // Arrange
        var regla = new FrecuenciaAnomalaRule(3, Duration.ofSeconds(60));
        regla.evaluar(evento("A", T0));
        regla.evaluar(evento("A", T0.plusSeconds(10)));
        regla.evaluar(evento("A", T0.plusSeconds(20)));

        // Act: 75 s despues, las dos primeras ya expiraron
        Optional<String> cuarta = regla.evaluar(evento("A", T0.plusSeconds(75)));

        // Assert
        assertThat(cuarta).isEmpty();
    }

    @Test
    void cadaUsuarioTieneSuPropioConteo() {
        // Arrange
        var regla = new FrecuenciaAnomalaRule(3, Duration.ofSeconds(60));
        regla.evaluar(evento("A", T0));
        regla.evaluar(evento("A", T0.plusSeconds(1)));
        regla.evaluar(evento("A", T0.plusSeconds(2)));

        // Act
        Optional<String> deB = regla.evaluar(evento("B", T0.plusSeconds(3)));
        Optional<String> cuartaDeA = regla.evaluar(evento("A", T0.plusSeconds(4)));

        // Assert
        assertThat(deB).isEmpty();
        assertThat(cuartaDeA).contains(MotivosFraude.HIGH_FREQUENCY);
    }

    @Test
    void enConcurrenciaNoSePierdenConteos() throws Exception {
        // Arrange: 1000 eventos del mismo usuario en 8 hilos, con maximo 999
        var regla = new FrecuenciaAnomalaRule(999, Duration.ofSeconds(60));
        ExecutorService hilos = Executors.newFixedThreadPool(8);
        List<Callable<Optional<String>>> tareas = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            tareas.add(() -> regla.evaluar(evento("A", T0)));
        }

        // Act
        List<Future<Optional<String>>> resultados = hilos.invokeAll(tareas);
        hilos.shutdown();

        // Assert: si ningun conteo se pierde, solo el evento numero 1000 supera el maximo
        long alertas = 0;
        for (Future<Optional<String>> resultado : resultados) {
            if (resultado.get().isPresent()) {
                alertas++;
            }
        }
        assertThat(alertas).isEqualTo(1);
    }

    @Test
    void parametrosInvalidosFallanRapido() {
        // Arrange
        Duration ventana = Duration.ofSeconds(60);

        // Act
        Throwable maxCero = catchThrowable(() -> new FrecuenciaAnomalaRule(0, ventana));
        Throwable ventanaCero = catchThrowable(() -> new FrecuenciaAnomalaRule(5, Duration.ZERO));
        Throwable ventanaNula = catchThrowable(() -> new FrecuenciaAnomalaRule(5, null));

        // Assert
        assertThat(maxCero).isInstanceOf(IllegalArgumentException.class);
        assertThat(ventanaCero).isInstanceOf(IllegalArgumentException.class);
        assertThat(ventanaNula).isInstanceOf(NullPointerException.class);
    }
}
