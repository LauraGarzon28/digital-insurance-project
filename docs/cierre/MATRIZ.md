# Matriz de decisión — G5 Aseguradora digital

**Lección 13.** Tres alternativas reales, seis criterios ponderados, análisis de
sensibilidad.

---

## Criterios y pesos

| # | Criterio | Peso | Justificación |
|---|---|---:|---|
| 1 | **Control del dato del reclamo** | 5 | Aprobar un pago sobre una póliza no vigente es el riesgo de negocio principal |
| 2 | **Auditabilidad** | 5 | Requisito no funcional explícito: toda decisión sobre póliza o siniestro debe quedar registrada |
| 3 | **Facilidad de operar** (equipo de 3) | 4 | Tres personas no pueden sostener tres servicios si cada uno exige operación propia |
| 4 | **Aislamiento de fallos** | 4 | Un incidente en reclamos no debe detener la emisión de pólizas |
| 5 | **Escalado selectivo** | 3 | El crecimiento de reclamos no es simétrico, pero no es la restricción de hoy |
| 6 | **Tiempo de entrega** | 3 | Importa para el corte del 22 de noviembre |

Escala: 1 (malo) a 5 (excelente).

---

## Matriz

| Criterio | Peso | A · Quedarse como está | B · Extraer los tres | C · Extraer solo Siniestros |
|---|---:|---:|---:|---:|
| 1 · Control del dato | 5 | 2 → 10 | 4 → 20 | 5 → 25 |
| 2 · Auditabilidad | 5 | 3 → 15 | 4 → 20 | 4 → 20 |
| 3 · Facilidad de operar | 4 | 5 → 20 | 1 → 4 | 3 → 12 |
| 4 · Aislamiento de fallos | 4 | 1 → 4 | 5 → 20 | 4 → 16 |
| 5 · Escalado selectivo | 3 | 1 → 3 | 5 → 15 | 3 → 9 |
| 6 · Tiempo de entrega | 3 | 5 → 15 | 1 → 3 | 3 → 9 |
| **Total** | | **67** | **82** | **91** |

**Lectura:**

- **A** gana en operación y entrega, pero saca 2 en control del dato: **el problema de
  negocio sigue abierto**. No es una opción mala en abstracto; lo es para este dominio,
  donde el requisito de control es explícito.
- **B** gana aislamiento y escala, pero un equipo de tres personas operando tres servicios
  en tres semanas es la receta del monolito distribuido.
- **C** gana porque extrae **solo lo que resuelve el problema**: Siniestros concentra el
  riesgo de control y la asimetría de carga, y las otras dos capacidades se quedan donde
  funcionan.

---

## Análisis de sensibilidad

Se mueve el peso del criterio más importante (±2).

| Escenario | A | B | C | Ganador |
|---|---:|---:|---:|---|
| Base | 67 | 82 | 91 | **C** |
| Control del dato con peso **3** | 57 | 72 | 81 | **C** |
| Control del dato con peso **7** | 77 | 92 | 101 | **C** |
| Facilidad de operar con peso **6** | 75 | 90 | 103 | **C** |

**Conclusión:** el ganador **no cambia** en ningún ajuste. La decisión es robusta.

**Caso honesto que hay que reconocer:** si el peso de "facilidad de operar" subiera a 6 y el
de "control del dato" bajara a 3, los totales serían A = 65, B = 74, C = 93 — **C sigue
ganando**, pero A quedaría mucho más cerca. Y si el equipo fuera de una sola persona sin
capacidad de operar dos procesos, **A sería la decisión correcta**: el control del dato se
puede resolver con el ADR P-01 **sin extraer ningún servicio**.

Ese es el hallazgo importante de la matriz: **P-01 y P-02 son decisiones independientes.**
El riesgo de negocio se resuelve con un puerto; la extracción del servicio resuelve el
aislamiento y la escala. Se pueden hacer por separado, y por eso la fase 1 es solo Siniestros.

---

## Decisión

> **Elegimos C (extraer solo Siniestros) porque concentra el riesgo de control del dato y la
> asimetría de carga, sin pagar el costo de distribuir Cotización y Pólizas, aceptando el
> costo de una base más que operar, consistencia eventual en los flujos que crucen contextos
> y la necesidad de una Saga donde hoy hay una transacción local.**

El ganador coincide con el ADR P-02. Lo que **no** está implementado se declara como
propuesta, no como hecho: hoy el proyecto está en la opción **A** del código.
