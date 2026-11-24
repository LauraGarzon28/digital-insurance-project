# ATAM-lite — Aseguradora digital G5

**Lección 7 aplicada al proyecto (actualizada al estado actual del corte).** Árbol de utilidad, análisis del escenario de mayor riesgo y hallazgos con su decisión.

---

## 1. Árbol de utilidad

| Atributo | Escenario | Imp. | Dif. |
|---|---|:---:|:---:|
| **Control del dato** | La evaluación de un siniestro consulta el estado de la póliza al contexto Pólizas (`polizas-service`). Medida: 0 siniestros aprobados sobre pólizas canceladas en una batería de 100 casos. | A | A |
| **Auditabilidad** | Toda decisión sobre un siniestro deja registro con quién, cuándo y con qué datos en `auditoria-service`. Medida: 100 % de las decisiones trazables. | A | M |
| **Disponibilidad de la cotización** | Con el contexto de Siniestros detenido, `POST /api/cotizaciones` responde 201 en p95 < 500 ms. | A | A |
| **Modificabilidad de reglas de riesgo** | Cambiar el umbral de revisión manual no modifica el código de Pólizas ni de Siniestros. Medida: un solo paquete/servicio tocado. | A | M |
| **Tiempo de incorporación** | Una persona nueva entiende un contexto leyendo su `dominio/` sin abrir la infraestructura. | M | B |

**Lectura:** los dos escenarios con importancia alta y dificultad alta —control del dato y disponibilidad de cotización— ya fueron **atendidos en el código**: `siniestros-service` consulta el estado real de la póliza en `polizas-service` (ADR P-01) y los contextos corren en procesos independientes (`clientes-service`, `cotizaciones-service`, `polizas-service`, `siniestros-service`).

---

## 2. Análisis del escenario de mayor riesgo

**Escenario:** evaluar un siniestro cuando el estado de la póliza es dudoso o `polizas-service` presenta fallos.

| Elemento | Análisis |
|---|---|
| **Enfoque inicial (superado)** | Antes de P-01, `EvaluacionSiniestroService.evaluar(...)` aceptaba el `estadoPoliza` declarado en el request del cliente |
| **Enfoque actual (implementado)** | `SiniestroApplicationService` consulta `ConsultaPolizaPort` (`PolizasRestAdapter` → `GET /api/polizas/{id}` vía Eureka propagando el JWT) al reportar y al evaluar |
| **Punto de sensibilidad** | La consulta síncrona a `polizas-service`: es el control que impide aprobar un reclamo sobre una póliza cancelada o vencida |
| **Punto de compromiso** | Consultar la póliza por red en cada reporte y evaluación añade dependencia de disponibilidad y latencia al camino crítico (se gana control del dato, se pierde autonomía si Pólizas cae) |
| **No-riesgo** | El monto aprobado no puede superar el valor asegurado obtenido de Pólizas: la regla está en `EvaluacionSiniestroService` / `Siniestro` y tiene pruebas |

### Riesgos y su decisión

| # | Riesgo | Decisión |
|---|---|---|
| **R1** | Confiar en el dato del cliente y aprobar un siniestro sobre una póliza cancelada | **Mitigado (implementado — ADR P-01).** Puerto `ConsultaPolizaPort` + `PolizasRestAdapter`; el cliente no envía estado ni valor asegurado |
| **R2** | Todo se despliega junto; un incidente en reclamos afecta la emisión o cotización | **Mitigado.** `clientes`, `cotizaciones`, `polizas` y `siniestros` corren como servicios Spring Boot independientes; además `cotizaciones → clientes` tiene Circuit Breaker |
| **R3** | Se pierden datos al reiniciar (H2 y mapas en memoria) | **Mitigado.** Los 6 servicios con estado persisten en bases PostgreSQL independientes con migraciones Flyway |
| **R4** | No hay auditoría persistente de decisiones | **Mitigado.** `auditoria-service` consume eventos de RabbitMQ (`siniestros.exchange`) de forma idempotente en `auditoria_db` |
| **R5** | Dependencia síncrona sin Circuit Breaker en `siniestros → polizas` y `polizas → cotizaciones` | **Abierto (aceptado en este corte).** Existen timeouts HTTP (`2 s` / `3 s`); añadir Resilience4j si las métricas lo justifican |
| **R6** | No hay pruebas automáticas de arquitectura interna (F1) ni Saga completa de cancelación | **Mitigar.** F2 ya se cumple por construcción al ser módulos Maven separados; falta ArchUnit (F1) y completar el lado Siniestros de la Saga de cancelación |

### Puntos de compromiso

| Compromiso | Gana | Pierde |
|---|---|---|
| Separación en 8 servicios (P-02/P-07 ampliado) | Aislamiento de fallos, despliegue y escala selectivos, F2 por compilación | Mayor costo operativo (8 procesos, 6 PostgreSQL, RabbitMQ) y consistencia eventual |
| Consultar la póliza por red (P-01) | Control real del dato | Dependencia de red en el camino crítico y nuevo modo de fallo si Pólizas cae |
| Auditoría asíncrona por RabbitMQ (P-06) | La evaluación no se bloquea ni falla si Auditoría cae | Consistencia eventual visible (unos milisegundos hasta aparecer en `/api/auditoria`) |

---

## 3. Estado actual frente a la evaluación inicial

**Los riesgos principales levantados al inicio del ATAM (R1, R2, R3 y R4) ya fueron cerrados en la implementación actual.** El riesgo arquitectónico residual se concentra ahora en la **resiliencia de las llamadas síncronas nuevas** (R5: falta de Circuit Breaker en Siniestros y Pólizas, falta de Outbox en Siniestros y falta de DLQ en Auditoría) y en la **gobernanza automática del hexágono** (R6: añadir ArchUnit para F1).
