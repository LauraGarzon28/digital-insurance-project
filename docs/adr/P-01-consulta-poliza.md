# P-01. Siniestros consulta la póliza en lugar de confiar en el cliente

Estado: **Propuesta** — no implementado en el código

## Contexto

El servicio de dominio `EvaluacionSiniestroService` tiene esta firma:

```java
public void evaluar(Siniestro siniestro, EstadoPolizaReferencia estadoPoliza,
                    boolean esFraude, BigDecimal montoPropuesto)
```

El estado de la póliza **entra como parámetro**. Es decir: el sistema toma como verdad el
`estadoPoliza` que declara el cliente al reportar el siniestro. Si un cliente envía
`VIGENTE` sobre una póliza cancelada, la evaluación lo acepta.

En una aseguradora esto no es un detalle técnico: permite aprobar un reclamo sobre un
contrato que no está en vigor. El contexto de Pólizas ya expone `GET /api/polizas/{id}`
con `valorAsegurado` y `estado` (`VIGENTE`, `VENCIDA`, `CANCELADA`), así que la consulta es
viable hoy.

## Decisión

**Propuesta:** introducir un puerto `ConsultaPolizaPort` en el dominio de Siniestros, con
un adaptador en infraestructura que consulte el contrato REST de Pólizas. La evaluación
deja de recibir `estadoPoliza` como entrada y lo obtiene del puerto.

Con timeout acotado y Circuit Breaker, y una decisión explícita para el caso en que
Pólizas no responda: **rechazar la evaluación y dejarla pendiente**, nunca asumir vigencia.

El patrón ya existe en el repositorio: `ConsultaCotizacionPort` y
`ConsultaCotizacionAdapter` hacen exactamente esto entre Pólizas y Cotización.

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

(+) **Trazabilidad.** Cada evaluación queda vinculada a una consulta concreta a Pólizas,
lo que sirve para auditoría.

(+) **La regla de dominio se vuelve testeable sin ambigüedad**: el test usa un doble del
puerto con estados conocidos.

(−) **Una llamada de red en el camino crítico.** Hoy la evaluación es local y rápida; con
el puerto depende de otro contexto.

(−) **Un modo de fallo nuevo.** Si Pólizas está caído, la evaluación no puede completarse.
Hay que definir la experiencia: dejar el siniestro pendiente y comunicarlo.

(−) **Latencia añadida** al flujo de evaluación, que hay que medir contra el objetivo de
respuesta.

(−) **Costo de mantener una interfaz y una traducción** por cada comunicación entre
contextos.
