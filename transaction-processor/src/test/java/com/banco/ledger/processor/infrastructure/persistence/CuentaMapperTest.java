package com.banco.ledger.processor.infrastructure.persistence;

import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.processor.domain.model.CuentaBancaria;
import com.banco.ledger.processor.domain.model.Saldo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/** Pruebas puras del mapper de cuentas: sin base de datos ni Spring. */
class CuentaMapperTest {

    @Test
    @DisplayName("toDomain() reconstruye la cuenta con su usuario y saldo")
    void toDomainReconstruyeLaCuenta() {
        // Arrange
        CuentaJpaEntity entidad = new CuentaJpaEntity("A", new BigDecimal("1000.00"));

        // Act
        CuentaBancaria cuenta = CuentaMapper.toDomain(entidad);

        // Assert
        assertThat(cuenta.usuario()).isEqualTo("A");
        assertThat(cuenta.saldo()).isEqualTo(Saldo.de("1000.00"));
    }

    @Test
    @DisplayName("toEntity() copia usuario y saldo a la entidad")
    void toEntityCopiaLosDatos() {
        // Arrange
        CuentaBancaria cuenta = CuentaBancaria.abrir("B", Saldo.de("50.25"));

        // Act
        CuentaJpaEntity entidad = CuentaMapper.toEntity(cuenta);

        // Assert
        assertThat(entidad.usuario()).isEqualTo("B");
        assertThat(entidad.saldo()).isEqualByComparingTo("50.25");
    }

    @Test
    @DisplayName("actualizar() modifica la MISMA entidad y conserva su version (bloqueo optimista)")
    void actualizarModificaLaMismaEntidad() {
        // Arrange
        CuentaJpaEntity entidad = new CuentaJpaEntity("A", new BigDecimal("1000.00"));
        long versionLeida = entidad.version();
        CuentaBancaria cuenta = CuentaMapper.toDomain(entidad);
        cuenta.debitar(MontoDinero.de("300.00"));

        // Act
        CuentaMapper.actualizar(entidad, cuenta);

        // Assert
        assertThat(entidad.saldo()).isEqualByComparingTo("700.00");
        assertThat(entidad.version()).isEqualTo(versionLeida);
    }
}
