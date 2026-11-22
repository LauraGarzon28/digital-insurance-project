# ATAM-lite — Aseguradora digital G5

**Lección 7 aplicada al proyecto.** Árbol de utilidad, análisis del escenario de mayor
riesgo y hallazgos con su decisión.

---

## 1. Árbol de utilidad

| Atributo | Escenario | Imp. | Dif. |
|---|---|:---:|:---:|
| **Control del dato** | La evaluación de un siniestro consulta el estado de la póliza al contexto Pólizas. Medida: 0 siniestros aprobados sobre pólizas canceladas en una batería de 100 casos. | A | A |
| **Auditabilidad** | Toda decisión sobre una póliza o un siniestro deja registro con quién, cuándo y con qué datos. Medida: 100 % de las decisiones trazables. | A | M |
| **Disponibilidad de la cotización** | Con el contexto de Siniestros detenido, `POST /api/cotizaciones` responde 201 en p95 < 500 ms. | A | A |
| **Modificabilidad de reglas de riesgo** | Cambiar el umbral de revisión manual no modifica el código de Pólizas ni de Siniestros. Medida: un solo paquete tocado. | A | M |
| **Tiempo de incorporación** | Una persona nueva entiende un contexto leyendo su `dominio/` sin abrir la infraestructura. | M | B |

**Lectura:** dos escenarios con importancia alta y dificultad alta —control del dato y
disponibilidad de cotización— son los que ATAM analiza primero. El primero está **sin
resolver**; el segundo tampoco, porque hoy los tres contextos viven en un proceso.

---

## 2. Análisis del escenario de mayor riesgo

**Escenario:** evaluar un siniestro cuando el estado de la póliza es dudoso.

| Elemento | Análisis |
|---|---|
| **Enfoque actual** | `EvaluacionSiniestroService.evaluar(siniestro, estadoPoliza, esFraude, monto)` recibe el estado como **parámetro** |
| **Cómo responde** | Acepta el `estadoPoliza` del cliente y lo usa como verdad. Si envía `VIGENTE` sobre una póliza cancelada, la evaluación lo acepta |
| **Punto de sensibilidad** | La validación del estado de la póliza: es el único control que impide pagar un reclamo sobre un contrato no vigente |
| **Punto de compromiso** | Consultar la póliza en cada evaluación añade dependencia de red a un flujo local: se gana control, se pierde disponibilidad |
| **No-riesgo** | El monto aprobado no puede superar el valor asegurado: la regla está en el dominio y tiene prueba |

### Riesgos y su decisión

| # | Riesgo | Decisión |
|---|---|---|
| **R1** | El sistema confía en el dato del cliente; se podría aprobar un siniestro sobre una póliza cancelada | **Mitigar — prioridad 1.** ADR P-01: puerto `ConsultaPolizaPort` |
| **R2** | Todo se despliega junto; un incidente en reclamos afecta la emisión | **Parcialmente mitigado.** Siniestros ya es un servicio independiente (ADR P-07). Pólizas y Cotización siguen en el monolito |
| **R3** | Se pierden datos al reiniciar (H2 y mapas en memoria) | **Mitigado en Siniestros** con PostgreSQL propio (ADR P-07). **Aceptado** para Pólizas, que sigue en H2 |
| **R4** | No hay auditoría persistente de decisiones | **Mitigado.** `auditoria-service` con RabbitMQ + PostgreSQL (ADR P-06) |
| **R5** | El crecimiento no es simétrico: una ola de reclamos escala todo | **Aceptar hoy**; argumento del ADR P-02 |
| **R6** | No hay pruebas de arquitectura: las fronteras entre contextos pueden erosionarse | **Mitigar.** Fitness functions F1–F3 |

### Puntos de compromiso

| Compromiso | Gana | Pierde |
|---|---|---|
| Monolito modular (P-04) | Operación simple, transacciones locales, 115 pruebas en un comando | Despliegue y escala independientes |
| Consultar la póliza por red (P-01) | Control real del dato | Una dependencia y un modo de fallo nuevos |
| Extraer Siniestros (P-02) | Aislamiento de fallos, escala selectiva | Consistencia eventual y una base más que operar |

---

## 3. Lo que esta evaluación deja al descubierto

**Los dos atributos de importancia alta y dificultad alta están sin resolver en el código.**
El de control del dato es el más grave porque es de negocio, no de rendimiento: no es que el
sistema sea lento, es que puede aprobar un pago indebido.

Y hay una asimetría llamativa: la documentación (L12 y L13) describe esta situación con
precisión, pero el código todavía no la corrige. Eso es exactamente lo que ATAM debe hacer
visible, y es la razón por la que el ADR P-01 lleva estado **Propuesta** y no Aceptado.
