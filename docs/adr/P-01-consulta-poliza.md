# P-01. Siniestros consulta la póliza en lugar de confiar en el cliente

Estado: **Aceptado — implementado**

## Contexto

El servicio de dominio `EvaluacionSiniestroService` tiene esta firma:

```java
public void evaluar(Siniestro siniestro, EstadoPolizaReferencia estadoPoliza,
                    boolean esFraude, BigDecimal montoPropuesto)
```

Antes de implementar este ADR, el estado de la póliza entraba como parámetro y el sistema
tomaba como verdad el `estadoPoliza` declarado por el cliente. Eso permitía aceptar
`VIGENTE` sobre una póliza cancelada.

En una aseguradora esto no es un detalle técnico: permite aprobar un reclamo sobre un
contrato que no está en vigor. El contexto de Pólizas ya expone `GET /api/polizas/{id}`
con `valorAsegurado` y `estado` (`VIGENTE`, `VENCIDA`, `CANCELADA`), así que la consulta es
viable hoy.

## Decisión

Introducir el puerto `ConsultaPolizaPort` en el dominio de Siniestros, con un adaptador
REST en infraestructura que consulta `polizas-service` por Eureka. Al reportar, Siniestros
obtiene de Pólizas el valor asegurado; al evaluar, obtiene el estado vigente. El contrato
HTTP ya no acepta el valor asegurado ni el estado de póliza como datos de autoridad del
cliente. El JWT de la petición se propaga en la llamada entre servicios.

Si Pólizas no encuentra la póliza, la operación responde `404`. Las llamadas entre
Cotizaciones/Pólizas/Siniestros tienen connect timeout de 2 s y read timeout de 3 s. Si el
servicio no está disponible, el error se propaga; nunca se asume que la póliza está vigente.
Circuit Breaker queda como mejora futura si las métricas de disponibilidad lo justifican.

La emisión de Pólizas aplica el mismo límite: consulta `cotizaciones-service` por Eureka y
solo permite emitir cuando la cotización existe y está aceptada.

## Alternativas descartadas

**Mantener el estado como parámetro del request.** Es el estado actual. Se descarta porque
traslada al cliente la responsabilidad de la verdad y hace imposible cumplir el control
interno: no hay forma de distinguir un dato verificado de uno declarado.

**Consultar la base de Pólizas directamente desde Siniestros.** Se descarta porque acopla
los esquemas de dos contextos y rompería la frontera que el ADR P-03 estableció: un cambio
de tabla en Pólizas rompería a Siniestros sin que su build se entere.

**Replicar el estado de la póliza en Siniestros con un evento.** Es válido a largo plazo,
pero introduce consistencia eventual en una regla que debe ser exacta en el momento de
decidir. Se pospone hasta que el flujo sea asíncrono por otra razón.

## Consecuencias

(+) **Control real sobre el reclamo.** La decisión se basa en la póliza de registro, no en
lo que el reclamante afirma.

(+) **Trazabilidad.** Cada siniestro conserva el valor obtenido de la póliza y cada
evaluación vuelve a consultar su estado actual.

(+) **La regla de dominio se vuelve testeable sin ambigüedad**: el test usa un doble del
puerto con estados conocidos.

(−) **Una llamada de red en el camino crítico.** Hoy la evaluación es local y rápida; con
el puerto depende de otro contexto.

(−) **Un modo de fallo nuevo.** Si Pólizas está caído, reportar o evaluar no puede
completarse; el error se comunica sin producir un resultado de éxito falso.

(−) **Latencia añadida** al flujo de evaluación, que hay que medir contra el objetivo de
respuesta.

(−) **Costo de mantener una interfaz y una traducción** por cada comunicación entre
contextos.
