# Reparto de trabajo

Criterio: cada persona es dueña de servicios completos (dominio + adaptadores + pruebas + Dockerfile),
y cada una toca las tres materias. **Miguel** se queda con la parte más pesada: el cerebro contable y el
detector de fraude stateful, que concentran casi todo lo de POO avanzada, persistencia y consistencia distribuida.

Peso aproximado: Miguel ~45 % · Juanse ~30 % · Andrés ~25 %.

---

## 🔴 Miguel — `transaction-processor` + `fraud-detector` + modelo de datos

**transaction-processor** (el núcleo)
- [ ] `Transaccion` y `CuentaBancaria` como entidades ricas (`debitar()`, `acreditar()`, `ejecutarTransferencia()`, `rechazarPorFondosInsuficientes()`, `marcarComoSospechosa()`).
- [ ] **Patrón State**: `Recibida → Procesando → Completada | Rechazada`, `Completada → Sospechosa`, sin `if/switch` (interfaz `sealed`).
- [ ] **Patrón Strategy**: `ReglaValidacion` (fondos suficientes, monto máximo, cuenta activa) inyectada como `List<ReglaValidacion>`.
- [ ] Puertos `TransaccionRepository`, `CuentaRepository`, `LedgerEventPublisher` y sus adaptadores (JPA + Kafka).
- [ ] Entidades JPA separadas del dominio + mappers; cuentas semilla con saldo inicial.
- [ ] `@Version` en cuentas (bloqueo optimista) e **idempotencia** por `tx_id` ante reentregas de RabbitMQ.
- [ ] Publicar en `transactions_log` con `tx_id` como key.
- [ ] Pruebas: todas las transiciones del State (válidas e `IllegalStateException`), invariantes de saldo, y Mockito con `verify(repo, times(1))` + `verify(publisher, times(1))`.

**fraud-detector** (stateful)
- [ ] `ReglaFraude` (Strategy): `AltoValorRule` (> $10.000) y `FrecuenciaAnomalaRule` (ventana deslizante en memoria, `ConcurrentHashMap`).
- [ ] Persistir alertas (`AlertaFraude` / `RegistroAuditoria`) en PostgreSQL y publicar en `fraud_alerts`.
- [ ] Pruebas de cada regla (incluida la ventana de tiempo con un `Clock` inyectado) y del servicio con Mockito.

**Para la sustentación:** CQRS/Event Sourcing, por qué Kafka es la fuente de verdad, garantías *at-least-once* + idempotencia, condiciones de carrera y bloqueo optimista.

---

## 🟡 Juanse — `transaction-api` + `dashboard-aggregator` + `dashboard.html`

- [ ] `POST /transfer` → `TransferCommandRecord` con validación Fail-Fast → 202 con `tx_id`; errores → 400 (`@RestControllerAdvice`).
- [ ] Adaptador `RabbitTransferCommandPublisher` (cola `transfer_commands`).
- [ ] Vista materializada en memoria (lado *Query* de CQRS) con dos `@KafkaListener` (`transactions_log`, `fraud_alerts`).
- [ ] Caso borde: alerta que llega antes que la transacción (no hay orden entre topics).
- [ ] WebSocket en `:8081` (`/ws/transactions`): snapshot al conectar + cambios incrementales.
- [ ] Terminar `dashboard.html` (ya tiene base): filas en rojo para sospechosas; opcional, formulario para enviar transferencias.
- [ ] Pruebas: `TransferRequest` inválidos, servicio con Mockito, vista materializada pura.

**Para la sustentación:** comunicación síncrona vs. asíncrona, *consumer groups*, push vía WebSocket, consistencia eventual.

---

## 🟢 Andrés — `notification-router` + `ad-generator` + `email-worker` + infraestructura Docker

- [ ] Router: solo eventos `COMPLETED` → `EmailCommandRecord` → `email_queue` (puente Kafka → RabbitMQ).
- [ ] `ad-generator`: `@Scheduled` + catálogo de anuncios → `email_queue`.
- [ ] Mantener `email-worker` (ya implementado como referencia) y añadir su prueba de listener.
- [ ] Dueño del `docker-compose.yml` y los Dockerfiles: verificar build de las 7 imágenes, límites `cpus`/`memory`, red `backend` interna vs `frontend`, healthchecks.
- [ ] Evidencias de SO: `docker stats` con los límites aplicados, `docker compose logs email-worker` mostrando el balanceo entre las 3 réplicas, contenedor corriendo como `appuser` (`docker compose exec email-worker whoami`), prueba de OOM / `ExitOnOutOfMemoryError`.
- [ ] Script `scripts/demo.sh` que lance los dos flujos del enunciado ($50 y $20.000).

**Para la sustentación:** cgroups y límites de recursos, JVM en contenedores (`UseContainerSupport`, `MaxRAMPercentage`), usuario no privilegiado, Work Queues y *fair dispatch* (`prefetch: 1`), escalado horizontal.

---

## Orden de integración sugerido

1. **Semana 1** — cada uno con su dominio + pruebas unitarias (sin brokers). Los contratos ya están en `common-contracts`; si alguien necesita cambiarlos, se avisa al grupo.
2. **Semana 2** — adaptadores. Flujo mínimo: api → processor → Kafka → dashboard.
3. **Semana 3** — fraude, notificaciones, publicidad; `docker compose up` de punta a punta.
4. **Semana 4** — evidencias, demo y ensayo de sustentación.

## Flujo de Git

- Una rama por persona/servicio (`feature/processor`, `feature/dashboard`, …) → Pull Request a `main`.
- `mvn test` debe pasar antes de cada merge.
- Cambios en `common-contracts` o `docker-compose.yml` siempre por PR revisado por otro integrante.

## Effective Java (3.ª ed.) — ítems que aplican directamente

| Ítem | Dónde |
|---|---|
| 1 · Factorías estáticas | `MontoDinero.de()`, `TxId.nuevo()`, `Transaccion.recibir()` |
| 4 · Clases no instanciables | `Topologia`, `Validaciones` |
| 15 · Minimizar accesibilidad | Entidades sin setters, campos `private final` |
| 17 · Minimizar mutabilidad | Todos los records y Value Objects |
| 18 · Composición sobre herencia | Estados y reglas como objetos inyectados |
| 49 · Validar parámetros | Constructores compactos Fail-Fast |
| 64 · Referirse a objetos por su interfaz | Puertos (`LedgerEventPublisher`, `TransaccionRepository`…) |
| 69–70 · Excepciones solo para casos excepcionales | `IllegalArgumentException` / `IllegalStateException` en el dominio |
| 78 · Sincronizar acceso a datos mutables compartidos | Estado en memoria de `fraud-detector` y `dashboard-aggregator` |
