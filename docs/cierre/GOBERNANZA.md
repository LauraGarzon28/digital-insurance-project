# Gobernanza de la arquitectura — G5 Aseguradora digital

**Lección 13.** Guardarraíles verificables, cómo se decide, deuda y radar.

---

## 1. Guardarraíles

Un guardarraíl es una regla que algo hace cumplir. En este proyecto, dos categorías: los que
**existen** y los que están **propuestos**.

### Existentes

| # | Guardarraíl | Cómo se verifica | Estado |
|---|---|---|---|
| G1 | Los tres contextos tienen capas `dominio`, `aplicacion`, `infraestructura` separadas | Estructura de paquetes; 115 pruebas discriminan por contexto | Existe |
| G2 | Las reglas de negocio tienen prueba | `mvnw test`: 115 pruebas en verde | Existe |
| G3 | El monto aprobado no supera el valor asegurado | Prueba de dominio en Siniestros | Existe |

### Propuestos (no implementados)

| # | Guardarraíl | Cómo se verificaría | Por qué importa |
|---|---|---|---|
| F1 | El dominio no depende de la infraestructura | ArchUnit sobre `..dominio..` | Protege el hexágono |
| F2 | Ningún contexto importa la infraestructura de otro | ArchUnit entre los tres paquetes | Protege la frontera del ADR P-03 |
| F3 | La evaluación de siniestro no acepta el estado de póliza del cliente | Prueba de firma / ArchUnit | Convierte el riesgo R1 en algo que el build vigila |

**F3 es el guardarraíl más valioso de los tres**: es el que impide que una regresión reintroduzca
el problema de negocio del ATAM.

---

## 2. Cómo se toman las decisiones

**Modelo: foro consultivo con dueño por subdominio.** Cada integrante decide sobre su
contexto; las decisiones que cruzan contextos se registran en un ADR y se discuten.

| Decisión | Quién | Registro |
|---|---|---|
| Reglas de negocio de un contexto | El dueño del subdominio | Código + prueba |
| Contrato entre contextos | Los dueños de ambos | ADR |
| Cambio de persistencia | El dueño del contexto, con el grupo | ADR |
| Extraer un servicio | Todo el grupo | ADR P-02 + matriz de decisión |

**Reparto de subdominios (ley de Conway aplicada):**

| Integrante | Subdominio | Paquete |
|---|---|---|
| Laura Garzón | Cotización y suscripción | `cotizacion` |
| Diego Sierra | Pólizas | `poliza` |
| Nicolás Ruiz | Siniestros | `siniestros` |

La separación del código refleja la separación de responsabilidades del equipo.

---

## 3. Deuda técnica

| # | Deuda | Riesgo | Disparador de pago |
|---|---|---|---|
| D1 | **Persistencia en memoria**: H2 y mapas. Se pierde todo al reiniciar | No hay operación real posible | Antes de cualquier demostración con datos que deban sobrevivir |
| D2 | **El estado de la póliza se recibe del cliente** | Aprobación indebida de un reclamo | Antes del primer uso con datos reales (ADR P-01) |
| D3 | **Sin auditoría persistente** de decisiones | Incumple el requisito de trazabilidad | Al migrar la persistencia |
| D4 | **Sin pruebas de arquitectura** | Las fronteras se erosionan sin aviso | Cuando se extraiga el primer servicio (F1–F3) |
| D5 | **Sin CI**: las 115 pruebas no corren en cada push | Una regresión puede llegar a `main` | Al configurar el repositorio remoto |

Cada deuda tiene un disparador concreto. Una deuda sin disparador es una deuda que no se paga.

---

## 4. Radar tecnológico

| Anillo | Tecnología | Razón |
|---|---|---|
| **Adoptar** | Java 25, Spring Boot 4.1.1, Maven | Stack obligatorio del curso; en uso |
| **Adoptar** | Arquitectura hexagonal por contexto | En uso en los tres contextos |
| **Adoptar** | ArchUnit | Propuesto: convierte las fronteras en algo verificable |
| **Probar** | PostgreSQL por contexto | Antes de cualquier uso real; hoy H2 y memoria |
| **Probar** | Docker Compose | Para levantar bases y bróker sin instalar nada |
| **Probar** | RabbitMQ o Kafka | Solo si el ADR P-02 se implementa: sin servicios separados no hay mensajería que justificar |
| **Evaluar** | gRPC para la consulta a Pólizas | REST es suficiente hoy; gRPC solo si el volumen lo justifica |
| **Evitar** | Microservicios completos (los tres) | Equipo de tres personas, tres semanas; produciría un monolito distribuido |
| **Evitar** | Event Sourcing | El estado actual es la fuente de verdad; no se necesita el historial completo |
| **Evitar** | Service mesh | Tres contextos en un proceso |
| **Evitar** | Nanoservicios | Un servicio por entidad sería un error de granularidad |

---

## 5. Evaluación y métricas

**Evaluación de diseño:** ATAM-lite en [`ATAM-LITE.md`](ATAM-LITE.md), con cinco escenarios y
seis riesgos con su decisión.

**Evaluación de resultados (DORA):** ninguna de las cuatro métricas se mide todavía.

| Métrica | Estado |
|---|---|
| Frecuencia de despliegue | Medible con `git log` |
| Tiempo de entrega de un cambio | No medido: no hay CI |
| Tasa de fallos de cambio | No medida |
| Tiempo de recuperación | No medida: no hay entorno productivo |

Reconocer que las cuatro están sin medir es parte de la gobernanza honesta: sin CI y sin
entorno, no hay nada que medir.
