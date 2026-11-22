# P-07. Extraer Siniestros a un servicio propio (fase 1 de P-02)

Estado: **Aceptado** — implementado

## Contexto

El ADR P-04 había decidido un monolito modular: un solo proceso Spring Boot con tres contextos
separados en paquetes. Esa decisión fue correcta para arrancar —tres personas, una cadencia de
despliegue— pero dejaba explícito su propio costo: un incidente en reclamos afectaba también a
la emisión de pólizas, y no había forma de escalar un contexto sin escalar los tres.

El ADR P-02 ya había diseñado la salida: extraer `siniestros-service` como fase 1. Faltaba
ejecutarlo.

## Decisión

**Extraer el contexto de Siniestros a un proyecto Spring Boot independiente**, con su propio
proceso (puerto 8084), su propia base de datos PostgreSQL y su propio despliegue.

La migración sigue una regla estricta: **el dominio y la aplicación se mueven sin cambiar una
línea de lógica**. Los 30 tests del contexto deben pasar sin tocar sus aserciones; si uno falla,
es un problema de imports o de empaquetado, nunca de negocio. Esa es la prueba de que el puerto
estaba bien diseñado.

Los cambios permitidos son los de infraestructura:

- Un adaptador de persistencia JPA que sustituye al mapa en memoria, implementando el mismo
  `RepositorioSiniestros`. El agregado se reconstituye mediante `SiniestroFactory.reconstituir(...)`
  porque su constructor sigue siendo package-private.
- Un adaptador de salida de mensajería (ADR P-06).
- Configuración de seguridad como OAuth2 Resource Server, validando el JWT del `auth-service`.
- Registro en Eureka y ruta en el gateway.

El gateway deja de enrutar `/api/siniestros/**` al monolito y lo hace a `lb://siniestros-service`.

## Alternativas descartadas

**Mantener los tres contextos en un proceso y solo documentar la extracción.** Se descarta
porque es exactamente lo que el ADR P-02 llevaba meses postergando, y porque el bloque B de la
evaluación mide servicios independientes con puertos propios: un monolito con buenos paquetes
no lo satisface.

**Extraer los tres contextos a la vez.** Se descarta por riesgo y por tiempo. Extraer Pólizas y
Cotización obliga a resolver antes la llamada entre ambos (`ConsultaCotizacionPort`), y
Siniestros es el único que ya tenía el hexágono completo y pruebas de contrato HTTP. Hacerlo en
fases permite que cada paso se valide solo.

**Extraer Siniestros compartiendo la base de datos del monolito.** Se descarta porque viola el
Database per Service y reintroduce el acoplamiento que la extracción buscaba eliminar: un cambio
de esquema en un contexto rompería al otro sin que su build se entere.

## Consecuencias

(+) **Aislamiento de fallos.** Si Siniestros consume CPU o se cae, la emisión de pólizas sigue
respondiendo. Era el riesgo R2 del ATAM.

(+) **Escala y despliegue selectivos.** Se puede redesplegar Siniestros sin tocar el resto, que
es la razón principal para extraer un servicio.

(+) **Base de datos propia, con Flyway.** La persistencia deja de ser en memoria: los siniestros
sobreviven a un reinicio, lo que cierra la deuda de pérdida de datos para este contexto.

(+) **Las fronteras ahora se pueden verificar.** Al ser proyectos Maven separados, un import
indebido entre contextos **no compila**. La convención del ADR P-03 pasa a ser una garantía del
build.

(−) **Una base, un despliegue y un puerto más que operar.** Cinco contenedores de PostgreSQL en
lugar de dos.

(−) **Las llamadas entre contextos dejan de ser locales.** Cuando Siniestros necesite el estado
real de la póliza (ADR P-01), será una llamada de red con sus propios modos de fallo. Hoy ese
dato sigue viniendo del request, así que el riesgo R1 continúa abierto: la extracción no lo
resuelve, lo hace más evidente.

(−) **Consistencia eventual entre Siniestros y Pólizas.** Ya no comparten transacción, de modo
que cualquier flujo que toque ambos necesitará saga o compensación. Hoy no existe tal flujo, y
conviene que siga así hasta que haya una razón de negocio.

(−) **Duplicación temporal de código.** `digital-insurance-api` conserva sus propios paquetes
`cliente` y `cotizacion` heredados, que conviven con los servicios extraídos hasta que se
retiren. Es deuda registrada, no un olvido.
