# Aseguradora digital G5 — Resumen ejecutivo

**Para quien decide sin haber leído el código.**

---

## Qué es

Una plataforma para que una aseguradora cotice riesgos, emita pólizas y gestione el reporte, asignación y liquidación de siniestros con trazabilidad auditada. Tres capacidades principales del negocio: **Cotización**, **Pólizas** y **Siniestros**, apoyadas por **Clientes**, **Identidad/Seguridad** y **Auditoría**.

## Cómo está construido hoy

El sistema opera como una arquitectura de **microservicios con Spring Boot 4.2.0** compuesta por **8 servicios independientes**, **6 bases de datos PostgreSQL aisladas** (con migraciones Flyway) y **RabbitMQ** como bróker de mensajería:

- **Puerta de entrada y plataforma:** `gateway` (puerto `8080`, valida tokens JWT), `discovery-server` (Eureka `8761`) y `auth-service` (`8081`, emite JWT RS256).
- **Servicios de negocio (Arquitectura Hexagonal + DDD):** `clientes-service` (`8082`), `cotizaciones-service` (`8083`, con Circuit Breaker hacia Clientes), `polizas-service` (`8086`, con Outbox y base de Saga) y `siniestros-service` (`8084`).
- **Trazabilidad asíncrona:** `auditoria-service` (`8085`), consumidor idempotente en PostgreSQL alimentado por eventos de RabbitMQ.

Cada servicio de negocio separa su núcleo de reglas (`dominio`), casos de uso (`aplicacion`) y adaptadores (`infraestructura`), por lo que las reglas de negocio se prueban sin depender de base de datos ni de red. **Las 114 pruebas automáticas (109 en modo sin `ApplicationTests`) pasan en verde.**

## Riesgos críticos ya resueltos

1. **Control del dato al reclamar (ADR P-01):** `siniestros-service` ya no confía en datos enviados por el cliente; consulta en tiempo real a `polizas-service` para verificar que la póliza esté vigente y obtener el valor asegurado real antes de aprobar una indemnización.
2. **Persistencia durable por contexto:** todos los servicios con estado persisten en su propia base PostgreSQL administrada por Flyway, eliminando las pérdidas de datos al reiniciar.
3. **Auditoría desacoplada (ADR P-06):** cada evento relevante de siniestros se publica en RabbitMQ y se registra de forma idempotente en `auditoria-service` sin frenar ni tumbar la atención del ajustador.

## Qué queda pendiente como mejora

1. **Completar resiliencia y consistencia distribuida:** añadir Outbox en `siniestros-service`, completar el consumidor de cancelación de pólizas para cerrar la Saga distribuida, agregar Dead Letter Queue (DLQ) en `auditoria-service` y extender Circuit Breaker a las llamadas hacia Pólizas y Cotizaciones.
2. **Endurecer seguridad y contratos:** restringir roles por endpoint en `polizas-service` y extraer un contrato compartido para los eventos en lugar de duplicar `EventoSiniestro`.
3. **Gobernanza automática y operación:** añadir pruebas de arquitectura con ArchUnit (F1), pipeline de CI/CD y observabilidad distribuida.

## Qué se decidió NO hacer, y por qué

- **No se usa Event Sourcing:** el estado actual de cada agregado es la fuente de verdad; reconstruir entidades evento a evento añadiría complejidad innecesaria.
- **No se usa Service Mesh ni Data Mesh:** con 8 procesos, un solo entorno y un equipo de 3 personas, el gateway y Eureka cubren el tráfico sin infraestructura sobredimensionada.

## En una frase

> El sistema está separado en 8 microservicios con arquitectura hexagonal, bases PostgreSQL independientes, seguridad JWT RS256, auditoría asíncrona en RabbitMQ y control real del dato en siniestros; las prioridades siguientes son completar la resiliencia/Saga distribuida, las pruebas ArchUnit y la automatización CI/CD.
