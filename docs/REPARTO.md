# Reparto de trabajo

Criterio: cada persona es dueña de servicios completos (dominio + adaptadores + pruebas + Dockerfile),
y cada una toca las tres materias. **Miguel** se queda con la parte más pesada: el cerebro contable y el
detector de fraude stateful, que concentran casi todo lo de POO avanzada, persistencia y consistencia distribuida.

Peso aproximado: Miguel ~45 % · Juanse ~30 % · Andrés ~25 %.

---

## 🔴 Miguel — `transaction-processor` + `fraud-detector` + modelo de datos

**transaction-processor** (el núcleo)
- [x] `Transaccion` y `CuentaBancaria` como entidades ricas (`debitar()`, `acreditar()`, `ejecutarTransferencia()`, `rechazarPorFondosInsuficientes()`, `marcarComoSospechosa()`).
- [x] **Patrón State**: `Recibida → Procesando → Completada | Rechazada`, `Completada → Sospechosa`, sin `if/switch` (interfaz `sealed`).
- [x] **Patrón Strategy**: `ReglaValidacion` (fondos suficientes, monto máximo; la cuenta inexistente se rechaza con `ACCOUNT_NOT_FOUND`) inyectada como `List<ReglaValidacion>`.
- [x] Puertos `TransaccionRepository`, `CuentaRepository`, `LedgerEventPublisher` y sus adaptadores (JPA + Kafka).
- [x] Entidades JPA separadas del dominio + mappers; cuentas semilla con saldo inicial.
- [x] `@Version` en cuentas (bloqueo optimista) e **idempotencia** por `tx_id` ante reentregas de RabbitMQ.
- [x] Publicar en `transactions_log` con `tx_id` como key.
- [x] Pruebas: todas las transiciones del State (válidas e `IllegalStateException`), invariantes de saldo, y Mockito con `verify(repo, times(1))` + `verify(publisher, times(1))`.

**fraud-detector** (stateful)
- [x] `ReglaFraude` (Strategy): `AltoValorRule` (> $10.000) y `FrecuenciaAnomalaRule` (ventana deslizante en memoria, `ConcurrentHashMap`).
- [x] Persistir alertas (`AlertaFraude` / `RegistroAuditoria`) en PostgreSQL y publicar en `fraud_alerts`.
- [x] Pruebas de cada regla (la ventana usa la hora del evento; el servicio usa un `Clock` inyectado) y del servicio con Mockito.

**Integración final**
- [x] Validación Fail-Fast de divisas: `Divisa` (solo COP) en `common-contracts` y `currency` opcional en `TransferRequest` (una divisa no admitida responde 400).
- [x] `spring-boot-starter-json` en `transaction-processor` para que arranque en Docker (mismo problema del `ObjectMapper` que tenían los servicios de Andrés).

**Para la sustentación:** CQRS/Event Sourcing, por qué Kafka es la fuente de verdad, garantías *at-least-once* + idempotencia, condiciones de carrera y bloqueo optimista.

---

## 🟡 Juanse — `transaction-api` + `dashboard-aggregator` + `dashboard.html`

- [x] `POST /transfer` → `TransferCommandRecord` con validación Fail-Fast → 202 con `tx_id`; errores → 400 (`@RestControllerAdvice`). Entrada y respuestas como records (`TransferRequest`, `TransferResponse`, `ErrorResponse`).
- [x] Adaptador `RabbitTransferCommandPublisher` (cola `transfer_commands`, declarada con la misma Dead Letter Queue que el processor).
- [x] Vista materializada en memoria (lado *Query* de CQRS) con dos `@KafkaListener` (`transactions_log`, `fraud_alerts`).
- [x] Caso borde: alerta que llega antes que la transacción (no hay orden entre topics); se guarda como pendiente dentro de `compute()`.
- [x] WebSocket en `:8081` (`/ws/transactions`): snapshot al conectar + cambios incrementales.
- [x] Terminar `dashboard.html` (ya tiene base): filas en rojo para sospechosas con su motivo y estado `SOSPECHOSA`; formulario para enviar transferencias (CORS en la API).
- [x] Pruebas: `TransferRequest` inválidos, servicios con Mockito, vista materializada pura (incluida una prueba concurrente) y adaptadores (Rabbit, listeners, WebSocket).

**Para la sustentación:** comunicación síncrona vs. asíncrona, *consumer groups*, push vía WebSocket, consistencia eventual.

---

## 🟢 Andrés — `notification-router` + `ad-generator` + `email-worker` + infraestructura Docker

- [x] Router: solo eventos `COMPLETED` → `EmailCommandRecord` → `email_queue` (puente Kafka → RabbitMQ). Directorio de usuarios como Strategy (`DirectorioUsuarios`).
- [x] `ad-generator`: `@Scheduled` + catálogo de anuncios (round-robin con `AtomicInteger`) → `email_queue`.
- [x] Mantener `email-worker` (ya implementado como referencia) y añadir su prueba de listener. Se agregó `spring-boot-starter-json` a los 3 servicios: sin él no existía el bean `ObjectMapper` y no arrancaban.
- [x] Dueño del `docker-compose.yml` y los Dockerfiles: build de las 7 imágenes verificado, límites `cpus`/`memory`, red `backend` interna vs `frontend`, healthchecks, y `restart: on-failure` en los servicios Spring.
- [x] Evidencias de SO: `docker stats` con los límites aplicados, `docker compose logs email-worker` mostrando el balanceo entre las 3 réplicas, contenedor corriendo como `appuser` (`docker compose exec email-worker whoami`), prueba de OOM / `ExitOnOutOfMemoryError` (con `-Xmx8m`, sale con código 3). Guía en [`EVIDENCIAS-SO.md`](EVIDENCIAS-SO.md).
- [x] Script `scripts/demo.sh` que lance los dos flujos del enunciado ($50 y $20.000). Verificado de punta a punta en Docker: la de $20.000 sale como `SOSPECHOSA` en el dashboard y los correos se reparten entre las 3 réplicas.

**Para la sustentación:** cgroups y límites de recursos, JVM en contenedores (`UseContainerSupport`, `MaxRAMPercentage`), usuario no privilegiado, Work Queues y *fair dispatch* (`prefetch: 1`), escalado horizontal.

---

## Orden de integración sugerido

1. **Semana 1** — cada uno con su dominio + pruebas unitarias (sin brokers). Los contratos ya están en `common-contracts`; si alguien necesita cambiarlos, se avisa al grupo.
2. **Semana 2** — adaptadores. Flujo mínimo: api → processor → Kafka → dashboard.
3. **Semana 3** — fraude, notificaciones, publicidad; `docker compose up` de punta a punta.
4. **Semana 4** — evidencias, demo y ensayo de sustentación.

## Flujo de Git

- `main` no se toca durante el desarrollo; `Develop` es la rama de integración (sin commits directos en ninguna de las dos).
- Cada tarea es una rama `feature/<nombre>` que sale de `Develop` actualizado, con commits pequeños en inglés (`feat:`, `fix:`, `test:`, `docs:`…).
- Vuelve a `Develop` por Pull Request, aprobado por otro integrante y mergeado con **"Create a merge commit"** (nunca squash ni rebase).
- `mvn test` debe pasar antes de cada commit y de cada merge.
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
