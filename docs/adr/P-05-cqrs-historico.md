# P-05. CQRS para el histórico de siniestros

Estado: **Propuesta** — no implementado

## Contexto

La HU-10 pide: *«Como suscriptor, quiero consultar el histórico de siniestros de un cliente
o póliza, para mejorar futuras evaluaciones de riesgo.»*

Esa consulta cruza dos contextos: los siniestros están en Siniestros y la identificación del
cliente y de la póliza en Pólizas. Con la arquitectura actual obligaría a recorrer los dos y
unir el resultado en memoria, y no permite agregaciones como «siniestros por póliza en el
último año» sin traer todos los datos.

Además el histórico es de **solo lectura**: no hay invariantes que proteger en ese lado.

## Decisión

**Propuesta:** un modelo de lectura desnormalizado —fichas de siniestros por póliza y por
cliente— alimentado por eventos de dominio, con un proyector idempotente y reconstruible.
Los endpoints serían de solo lectura, sin POST ni PUT.

Nivel 2 de CQRS (almacenes separados), **no** Event Sourcing: el estado actual de las
entidades sigue siendo la fuente de verdad.

## Alternativas descartadas

**Consultar los dos contextos en cada petición y unir en memoria.** Es lo que ocurriría sin
CQRS. Se descarta porque la pantalla depende de que ambos contextos respondan y porque las
agregaciones requieren traer todos los siniestros del cliente.

**Una vista materializada en la base de Pólizas.** Se descarta porque crea dependencia
directa entre esquemas y contradice el ADR P-03: Pólizas dejaría de ser dueña exclusiva de
su base.

**Event Sourcing sobre los siniestros.** Se descarta por complejidad desproporcionada: se
necesita consultar el histórico, no reconstruir el estado de cada siniestro evento a evento.
El historial completo tiene valor de auditoría, pero la auditoría se resuelve con registro
de eventos de decisión, no con Event Sourcing.

## Consecuencias

(+) **Una sola lectura devuelve el histórico** sin llamadas entre contextos.

(+) **Las agregaciones se resuelven con un índice**, no recorriendo todos los siniestros.

(+) **La consulta sobrevive a la caída de los contextos de escritura.**

(+) **El modelo es descartable**: si se corrompe, se reconstruye reproduciendo eventos.

(−) **Un modelo más que mantener y sincronizar.**

(−) **Consistencia eventual visible**: un siniestro recién reportado puede tardar unos
segundos en aparecer en el histórico. Hay que decidir qué muestra la interfaz mientras tanto.

(−) **Un proyector que debe ser idempotente y tolerante al desorden**, o los totales se
corrompen con eventos duplicados.

(−) **La reconstrucción depende de la retención de los eventos.** Si se borran, no se puede
reproducir.

(−) **Requiere mantener un proyector y su modelo de lectura sincronizados.** Aunque la
mensajería asíncrona con RabbitMQ ya está implementada para auditoría (ADR P-06), este ADR
queda en estado Propuesta porque el modelo de lectura desnormalizado para HU-10 aún no se ha
construido.
