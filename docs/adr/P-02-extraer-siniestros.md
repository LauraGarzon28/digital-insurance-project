# P-02. Extraer solo `siniestros-service` como fase 1

Estado: **Aceptado** — fase 1 implementada en el ADR P-07

## Contexto

Los tres contextos viven en un único proceso Spring Boot. Esa decisión tiene tres
consecuencias operativas medibles:

1. Un cambio en Siniestros obliga a redesplegar la emisión de pólizas.
2. Un incidente en reclamos puede tumbar la cotización.
3. El crecimiento no es simétrico: un terremoto o una ola de reclamos multiplica la carga
   de Siniestros sin que la cotización crezca igual, y hoy se escala todo o nada.

La evaluación del curso llega hasta la Lección 6. Extraer un contexto es el mínimo
verificable de "microservicios": proceso, build, despliegue y datos propios.

## Decisión

**Propuesta:** extraer **únicamente** `siniestros-service`, con su propia base de datos,
conservando el contrato REST actual (`/api/siniestros`) y los 22 tests del subdominio sin
cambiar la lógica de negocio.

El criterio de aceptación es verificable: el servicio arranca solo, en su puerto, con su
base; los 22 tests siguen verdes; la colección Bruno pasa contra el servicio extraído.

## Alternativas descartadas

**Extraer los tres contextos a la vez.** Se descarta porque produce un monolito
distribuido: tres servicios que comparten datos, se llaman en cadena y solo pueden
desplegarse juntos. Multiplica la coordinación e impide aprender de una fase antes de abrir
la siguiente.

**No extraer nada y quedarse en el monolito modular.** Es una opción legítima y está
evaluada en la matriz de decisión. Se descarta para Siniestros —no para los otros dos— por
el problema concreto del control del dato (ADR P-01) y por la asimetría de carga.

**Extraer un servicio por entidad.** Se descarta por granularidad: `Paciente`, `Cama`,
`Orden`, `Resultado` no tienen ciclos de vida independientes. Sería un error de diseño, no
una mejora.

## Consecuencias

(+) **Despliegue independiente de reclamos.** Un cambio en siniestros no obliga a
redesplegar la emisión de pólizas.

(+) **Aislamiento de fallos.** Un incidente en reclamos no tumba la cotización.

(+) **Escala selectiva** ante una ola de reclamos.

(+) **Propiedad del dato.** Siniestros es dueño de sus siniestros; Pólizas del estado y
del valor asegurado.

(−) **Consistencia eventual.** El reclamo puede registrarse antes de que el resto del
sistema se entere; hay que definir qué se muestra mientras tanto.

(−) **Más infraestructura**: una base más, un despliegue más, monitoreo, registros y el
costo operativo de atender alertas.

(−) **Nuevos modos de fallo**: la red falla, el otro servicio tarda, el mensaje se duplica.

(−) **Más contratos**: cada integración necesita contrato explícito y versión; un cambio
puede romper al otro servicio.

(−) **Transacciones distribuidas.** Los flujos que toquen Pólizas y Siniestros ya no
pueden resolverse con un `commit` local; exigen Saga y compensación.
