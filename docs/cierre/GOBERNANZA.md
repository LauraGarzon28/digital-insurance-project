# Gobernanza de la arquitectura — G5 Aseguradora digital

**Lección 13 (actualizada al estado actual del código).** Guardarraíles verificables, cómo se decide, deuda y radar.

---

## 1. Guardarraíles

Un guardarraíl es una regla verificable automáticamente o por estructura. En este proyecto hay guardarraíles **existentes** y **propuestos**.

### Existentes

| # | Guardarraíl | Cómo se verifica | Estado |
|---|---|---|---|
| **G1 / F2** | Los contextos están aislados y no pueden importar la infraestructura de otro | Módulos Maven separados (`clientes-service`, `cotizaciones-service`, `polizas-service`, `siniestros-service`): un import cruzado no compila | **Cumplida por construcción** |
| **G2** | Las reglas de negocio y casos de uso tienen prueba automatizada | `mvnw test`: **114 pruebas** en total (**109** sin `*ApplicationTests`) en verde entre los 8 servicios | **Existe** |
| **G3** | El monto aprobado no supera el valor asegurado | Pruebas de dominio en `siniestros-service` (`SiniestroTest`) | **Existe** |
| **F3** | La evaluación de un siniestro no acepta el estado ni el valor asegurado del cliente | Pruebas de aplicación y controlador en `siniestros-service` (`SiniestroApplicationServiceTest`, `SiniestroControllerTest`) | **Implementada** |
| **F4** | El consumidor de auditoría es idempotente | `AuditoriaServiceTest.elMismoMensajeDosVecesSoloSeAuditaUnaVez` + constraint unique en BD | **Implementada** |
| **F5** | El gateway enruta Pólizas por Eureka | `RutasGatewayTest.polizasSeEnrutaAlServicioDescubiertoPorEureka` | **Implementada** |

### Propuestos (pendientes)

| # | Guardarraíl | Cómo se verificaría | Por qué importa |
|---|---|---|---|
| **F1** | El dominio no depende de la infraestructura ni de Spring/JPA | ArchUnit sobre `..dominio..` en cada servicio | Protege la pureza del hexágono ante cambios futuros |

---

## 2. Cómo se toman las decisiones

**Modelo: foro consultivo con dueño por subdominio.** Cada integrante decide sobre su contexto; las decisiones que cruzan contextos se registran en un ADR y se discuten.

| Decisión | Quién | Registro |
|---|---|---|
| Reglas de negocio de un contexto | El dueño del subdominio | Código + prueba |
| Contrato entre contextos | Los dueños de ambos | ADR |
| Cambio de persistencia | El dueño del contexto, con el grupo | ADR + migración Flyway |
| Extraer un servicio | Todo el grupo | ADR + matriz de decisión |

**Reparto de subdominios (ley de Conway aplicada):**

| Integrante | Subdominio | Servicios en el repositorio |
|---|---|---|
| Laura Garzón | Cotización, clientes, identidad y borde | `cotizaciones-service`, `clientes-service`, `auth-service`, `gateway`, `discovery-server` |
| Diego Sierra | Pólizas | `polizas-service` |
| Nicolás Ruiz | Siniestros y auditoría | `siniestros-service`, `auditoria-service` |

La separación de carpetas y despliegues refleja directamente la separación de responsabilidades del equipo.

---

## 3. Deuda técnica

### Deudas cerradas respecto al corte inicial

- ~~**D1 · Persistencia en memoria (H2 y mapas):**~~ **Cerrada.** Los 6 servicios con estado usan PostgreSQL propio y Flyway (`auth_db`, `clientes_db`, `cotizaciones_db`, `polizas_db`, `siniestros_db`, `auditoria_db`).
- ~~**D2 · Estado de póliza recibido del cliente:**~~ **Cerrada.** Implementado ADR P-01 (`ConsultaPolizaPort` / `PolizasRestAdapter`).
- ~~**D3 · Sin auditoría persistente:**~~ **Cerrada.** Implementado ADR P-06 (`auditoria-service` + RabbitMQ + `auditoria_db`).

### Deuda técnica vigente

| # | Deuda | Riesgo | Disparador de pago |
|---|---|---|---|
| **D4** | **Sin pruebas ArchUnit (F1)** | Las dependencias internas dentro de un servicio podrían erosionar el hexágono | Antes de incorporar nuevos desarrolladores o refactors grandes |
| **D5** | **Sin CI automatizado** | Las 114 pruebas no corren automáticamente en cada push | Al configurar GitHub Actions sobre el repositorio |
| **D6** | **Resiliencia desigual y Saga/Outbox incompletos** | `siniestros-service` publica a RabbitMQ sin Outbox y no responde aún a `polizas.cancelacion-solicitada`; `auditoria-service` carece de DLQ; falta Circuit Breaker en `siniestros → polizas` y `polizas → cotizaciones` | Antes de habilitar cancelación distribuida o paso a producción |
| **D7** | **Contrato `EventoSiniestro` duplicado** | Un cambio incompatible entre productor y consumidor solo falla en tiempo de ejecución | Al versionar eventos o crear un módulo compartido de contratos |

---

## 4. Radar tecnológico

| Anillo | Tecnología | Razón |
|---|---|---|
| **Adoptar** | Java 25, Spring Boot 4.2.0, Maven | Stack base del proyecto en los 8 servicios |
| **Adoptar** | Arquitectura hexagonal por contexto | En uso en `clientes`, `cotizaciones`, `polizas` y `siniestros` |
| **Adoptar** | PostgreSQL + Flyway por servicio | En uso en los 6 servicios con persistencia (`docker-compose.yml`) |
| **Adoptar** | Docker Compose | Levanta las 6 bases de datos y RabbitMQ |
| **Adoptar** | RabbitMQ (topic exchange) | En uso para auditoría de siniestros y eventos de pólizas |
| **Adoptar** | Spring Cloud Gateway + Eureka + JWT RS256 | Enrutamiento, descubrimiento y seguridad activos |
| **Adoptar** | ArchUnit | Propuesto para automatizar F1 dentro de cada módulo |
| **Evaluar** | gRPC entre servicios internos | `polizas-service` expone `PolizaGrpcService` y contratos `.proto`, pero el consumo activo usa REST (`RestClient`) |
| **Evaluar** | Observabilidad distribuida (OpenTelemetry) | Hoy cada servicio emite logs locales sin `traceId` distribuido |
| **Evitar** | Event Sourcing | El estado actual es la fuente de verdad; no hace falta reconstruir entidades evento a evento |
| **Evitar** | Service mesh | 8 procesos, 1 entorno, equipo de 3 personas: no justifica el costo operativo |
| **Evitar** | Nanoservicios | Un servicio por entidad sería un error de granularidad |

---

## 5. Evaluación y métricas

**Evaluación de diseño:** ATAM-lite en [`ATAM-LITE.md`](ATAM-LITE.md).

**Evaluación de resultados (DORA):** solo la frecuencia de despliegue/cambios es observable hoy vía `git log`; tiempo de entrega, tasa de fallos de cambio y tiempo de recuperación no se miden todavía por ausencia de CI y entorno productivo.
