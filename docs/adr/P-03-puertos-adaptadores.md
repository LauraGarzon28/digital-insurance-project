# P-03. Puertos y adaptadores para la comunicación entre contextos

Estado: **Aceptado** — implementado

## Contexto

El contexto de Pólizas necesita los datos de una cotización para emitir la póliza: el
`clienteId`, el `valorAsegurado` y si la cotización fue aceptada. Ese dato pertenece al
contexto de Cotización, que tiene su propio lenguaje y su propio agregado.

La pregunta de diseño es cómo acceder a ese dato sin que Pólizas dependa de la
implementación de Cotización.

## Decisión

Un **puerto en el dominio** de Pólizas y un **adaptador en su infraestructura**:

- `poliza/dominio/puertos/ConsultaCotizacionPort` declara
  `Optional<DatosCotizacionParaEmision> buscarParaEmision(UUID cotizacionId)`.
- `poliza/infraestructura/salida/cotizacion/ConsultaCotizacionAdapter` implementa el
  puerto traduciendo una `Cotizacion` a `DatosCotizacionParaEmision`.

El dominio de Pólizas conoce un tipo propio (`DatosCotizacionParaEmision`) y una interfaz.
No conoce `Cotizacion`, `CotizacionUseCase` ni el paquete de Cotización.

## Alternativas descartadas

**Que Pólizas importe `CotizacionUseCase` directamente.** Es lo más rápido de escribir y se
descarta porque acopla los dos contextos: un cambio en la firma de un caso de uso de
Cotización rompe a Pólizas sin que su build se entere, y los dos contextos dejan de poder
probarse por separado.

**Pasar los datos de la cotización como parámetro desde el controlador.** Se descarta
porque traslada la coordinación a la capa de entrada: cada nuevo punto de entrada tendría
que repetir la consulta y podría olvidarla.

**Copiar los datos de la cotización en el agregado de Póliza en el momento de la emisión.**
Es válido como optimización, pero se descarta como mecanismo principal porque introduce
duplicación de datos que hay que mantener sincronizada.

## Consecuencias

(+) **El dominio de Pólizas se prueba con un doble.** No necesita Cotización ni Spring para
sus pruebas.

(+) **Frontera explícita.** Los dos contextos pueden evolucionar por separado; el contrato
es el puerto.

(+) **Si mañana Cotización se extrae a otro proceso**, el adaptador cambia y el dominio no.

(−) **Una interfaz y una traducción por cada comunicación.** En un flujo trivial, la
indirección puede parecer ceremonia innecesaria.

(−) **El puerto devuelve un DTO del dominio de Pólizas**, lo que obliga a mantener la
traducción cuando cambie cualquiera de los dos modelos.

(−) **No hay verificación automática de la frontera.** Hoy nada impide que alguien importe
`cotizacion.infraestructura` desde Pólizas. Es la deuda que cubriría la fitness function F2.
