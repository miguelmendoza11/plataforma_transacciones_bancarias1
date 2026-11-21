package com.banco.ledger.processor.domain.state;

import com.banco.ledger.contracts.EstadoTransaccion;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Tabla de busqueda nombre -> objeto estado, usada para REHIDRATAR una Transaccion leida de la
 * base de datos (alli solo se guarda el nombre, ej. "COMPLETED").
 *
 * Usa un EnumMap en lugar de un switch: la decision se toma con una busqueda en una tabla,
 * no con condicionales. Como los estados son records inmutables sin componentes, se puede
 * compartir una sola instancia de cada uno.
 *
 * Package-private: el resto del sistema entra por {@link EstadoTransaccionState#desde}.
 */
final class CatalogoEstados {

    private static final Map<EstadoTransaccion, EstadoTransaccionState> POR_NOMBRE = crearCatalogo();

    private CatalogoEstados() {
        throw new AssertionError("No instanciable");
    }

    private static Map<EstadoTransaccion, EstadoTransaccionState> crearCatalogo() {
        Map<EstadoTransaccion, EstadoTransaccionState> catalogo = new EnumMap<>(EstadoTransaccion.class);
        catalogo.put(EstadoTransaccion.RECIBIDA, new Recibida());
        catalogo.put(EstadoTransaccion.PROCESANDO, new Procesando());
        catalogo.put(EstadoTransaccion.COMPLETED, new Completada());
        catalogo.put(EstadoTransaccion.REJECTED, new Rechazada());
        catalogo.put(EstadoTransaccion.SOSPECHOSA, new Sospechosa());
        return Collections.unmodifiableMap(catalogo);
    }

    static EstadoTransaccionState buscar(EstadoTransaccion nombre) {
        return Optional.ofNullable(POR_NOMBRE.get(nombre))
                .orElseThrow(() -> new IllegalArgumentException("Estado sin implementacion: " + nombre));
    }
}
