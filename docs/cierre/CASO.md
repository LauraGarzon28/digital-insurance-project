# Caso real: Prime Video 2023 — y su transferencia al dominio de seguros

**Lección 13.** Un caso de la industria con fuente primaria, y un análisis honesto de qué
transfiere y qué no a una aseguradora digital.

---

## El caso

En **marzo de 2023**, el blog de ingeniería de Amazon publicó un artículo titulado
*«Scaling up the Prime Video audio/video monitoring service and reducing costs by 90 %»*.
Describe una herramienta de **monitoreo de calidad de audio y video** construida con una
arquitectura distribuida basada en funciones serverless (AWS Step Functions y Lambda), con
muchos componentes pequeños comunicados entre sí.

El problema no fue de corrección sino de **costo y coordinación**: el trabajo se dividía en
muchas funciones y cada transición entre ellas transfería datos, con el consiguiente costo en
tiempo y dinero. El equipo reportó además una superficie grande de componentes que mantener y
depurar.

La decisión fue **consolidar** los componentes en un único proceso que hace el trabajo en
memoria. El resultado reportado fue una **reducción superior al 90 % en el costo de
infraestructura**, manteniendo la escala necesaria.

## Qué NO dice el caso, y por qué importa

- **No dice que los microservicios sean un error.** El resto de la plataforma de Prime Video
  sigue siendo distribuida. El cambio se aplicó a **una herramienta concreta**.
- **No es un argumento contra serverless en general**, sino contra una descomposición que
  obliga a transferir grandes volúmenes de datos entre etapas fuertemente acopladas.
- El contexto incluía una restricción real: monitorear miles de streams concurrentes con
  presupuesto acotado.

## Transferencia a la aseguradora digital

| Aspecto | Prime Video 2023 | Aseguradora G5 | ¿Transfiere? |
|---|---|---|---|
| Naturaleza del trabajo | Etapas encadenadas sobre el mismo flujo de video | Operaciones sobre tres agregados con ciclos de vida distintos | **No** |
| Motivo del cambio | Costo de transferir datos entre funciones | Ninguno: el volumen es de registros, no de medios | **No** |
| Lección aplicable | Distribuir tiene un costo que hay que medir antes de distribuirlo | Es el criterio del ADR P-04 y de la matriz de decisión | **Sí, como principio** |
| Resultado | −90 % de costo de infraestructura | No aplica una métrica equivalente | **No** |

**Lo que sí se toma del caso:** la pregunta de diseño correcta no es *«¿microservicios sí o
no?»* sino *«¿qué problema concreto resuelve separar esta parte, y cuánto cuesta la
separación?»*. Aplicado a este proyecto: extraer **solo** Siniestros (ADR P-02) porque ahí hay
un problema concreto —el control del dato y la asimetría de carga—, y no extraer Cotización ni
Pólizas porque no lo hay.

**Lo que no se puede tomar:** el −90 % no es un resultado propio ni transferible. Es el
resultado de Prime Video en su contexto, y presentarlo como propio sería una falta de
honestidad académica.

## Cómo se usaría en la defensa

Si preguntan *«¿por qué no extrajeron los tres servicios?»*:

> «Porque el caso de Prime Video de 2023 muestra que distribuir tiene un costo que hay que
> medir. En nuestro dominio, el problema concreto está en Siniestros: la evaluación confía en
> datos que envía el cliente y su carga no crece igual que la cotización. Ahí sí justifica
> separar. En Cotización y Pólizas no hay un problema equivalente, y separarlas aportaría
> consistencia eventual y operación sin beneficio.»

## Fuente y nota de verificación

- Amazon Prime Video Tech Blog, *«Scaling up the Prime Video audio/video monitoring service
  and reducing costs by 90 %»*, marzo de 2023.

Se cita el contenido del artículo, no sus cifras exactas ni la identidad de sus autores, que
no se verificaron de primera mano. Antes de usarlo en la socialización, conviene abrir la
publicación original y confirmar los datos.
