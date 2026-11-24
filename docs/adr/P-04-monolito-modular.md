# P-04. Monolito modular antes que microservicios

Estado: **Superado** — decisión inicial reemplazada por P-07 y la extracción posterior a microservicios con PostgreSQL

## Contexto

El Event Storming de la Lección 3 produjo tres bounded contexts con eventos pivote reales:
Cotización, Pólizas y Siniestros. Cada uno tiene lenguaje propio, reglas propias y un
agregado raíz distinto.

Al arrancar el proyecto había que decidir cómo desplegarlos. El equipo es de tres personas,
hay una sola cadencia de despliegue y los flujos entre contextos son hoy transacciones
locales.

## Decisión

**Un solo proceso Spring Boot** con tres módulos internos bien separados, cada uno en su
propio paquete con arquitectura hexagonal (`dominio`, `aplicacion`, `infraestructura`) y
puertos explícitos.

El nombre correcto para esto es **monolito modular**: los límites existen y se respetan en
el código, pero se compilan y se despliegan juntos.

## Alternativas descartadas

**Distribuir los tres contextos desde el inicio.** Se descarta porque el costo de
coordinación de tres personas con tres servicios supera el beneficio: cada frontera nueva
exige contratos versionados, pruebas de integración y operación. Además las fronteras del
dominio todavía podían ajustarse sin costo, y en un sistema distribuido mover una frontera
cuesta mucho más.

**Un servicio por entidad o Value Object.** Se descarta por granularidad: sería un error de diseño.
`Riesgo`, `Prima`, `ValorAsegurado` y `VigenciaPoliza` no tienen ciclos de vida independientes.

**Un monolito sin módulos internos.** Se descarta porque pierde la ventaja principal: con
paquetes por contexto y puertos explícitos, una extracción posterior es mecánica, no una
reescritura.

## Consecuencias

(+) **Despliegue y operación simples.** Un comando para levantar, uno para desplegar.

(+) **Transacciones locales.** Emitir una póliza y leer la cotización ocurre en un mismo
proceso; no hace falta Saga ni compensación.

(+) **115 pruebas en un solo comando**, sin infraestructura de contenedores.

(+) **Las fronteras están preparadas para extraerse**: el ADR P-02 es viable precisamente
porque los contextos ya están separados internamente.

(−) **Un cambio en cualquier contexto obliga a redesplegar todo el sistema.** No se puede
desplegar Siniestros sin tocar Cotización.

(−) **No se puede escalar solo reclamos.** Una ola de siniestros escala todo o nada.

(−) **Un incidente en un contexto afecta a los otros.** Si Siniestros consume CPU, la
cotización también se degrada.

(−) **La separación es una convención, no una garantía.** Nada impide hoy que un contexto
importe las clases internas de otro. Es la deuda que cubrirían las fitness functions F1 y F2.

(−) **La persistencia no es real**: H2 y mapas en memoria. Se pierde todo al reiniciar.
Es una limitación del alcance del curso, no una consecuencia del monolito modular.
