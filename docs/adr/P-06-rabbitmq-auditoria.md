# P-06. Mensajería asíncrona con RabbitMQ para la auditoría

Estado: **Aceptado** — implementado

## Contexto

El requisito no funcional de trazabilidad pide que toda decisión sobre un siniestro quede
registrada con quién, cuándo y con qué datos. El ATAM lo levantó como riesgo R4: hasta ahora
la auditoría no existía como componente, solo como intención en la documentación.

Había que decidir cómo llega el hecho desde el contexto de Siniestros hasta quien lo conserva.
Y la decisión no era neutral: la evaluación de un siniestro es el camino crítico del negocio,
así que cualquier cosa que se le añada puede volverlo más lento o, peor, más frágil.

## Decisión

**RabbitMQ con un exchange de tipo topic.** `siniestros-service` publica `SiniestroReportado`
y `SiniestroResuelto` en `siniestros.exchange` con la routing key `siniestro.decision`.
`auditoria-service` los consume con `@RabbitListener` desde la cola durable
`auditoria.siniestros.queue` y los proyecta en su propia base de datos.

El productor se conecta a través de un puerto del dominio
(`PublicadorEventosSiniestro`), de modo que el núcleo no conoce ni Rabbit ni AMQP.

El consumidor es **idempotente**: cada mensaje lleva un `mensajeId` y se registra en la tabla
`mensajes_procesados` antes de escribir la decisión, con restricción `unique` sobre
`decisiones_siniestro.mensaje_id` como segunda barrera. Un reenvío del bróker no duplica la
auditoría.

## Alternativas descartadas

**Escribir la auditoría con una llamada REST síncrona desde Siniestros.** Es lo primero que
uno escribe y se descarta por dos razones concretas: mete una dependencia de red en el camino
crítico de la evaluación —si la auditoría está lenta, el ajustador espera— y hace que la
decisión de negocio falle cuando falla un componente que no participa en ella. Además obliga a
decidir qué hacer si la auditoría rechaza la petición, que es exactamente el problema que un
hecho no debería tener.

**Auditar dentro del propio `siniestros-service`.** Se descarta porque confunde dos
responsabilidades con ciclos de vida distintos: la evaluación se escribe una vez y cambia poco;
el histórico de auditoría crece sin parar y tiene sus propios requisitos de retención y
consulta. Meterlos en la misma base impide escalarlos por separado.

**Kafka en lugar de RabbitMQ.** Se descarta para este alcance por la forma del flujo, no por
capacidad: aquí hay un productor, un consumidor y un hecho por decisión. Kafka se justifica
cuando hace falta reprocesar el histórico, retener eventos por días o tener varios consumidores
independientes con offsets propios. Nada de eso existe todavía; si llega la HU-10 con
analítica, la migración es un cambio de adaptador, no del dominio.

## Consecuencias

(+) **La evaluación no se degrada por la auditoría.** Si `auditoria-service` está caído, la
cola retiene los eventos y el ajustador sigue trabajando.

(+) **El consumidor es idempotente.** Un reenvío del bróker, un reinicio a mitad de
procesamiento o un `requeue` no duplican el histórico.

(+) **El dominio sigue limpio.** `EvaluacionSiniestroService` y `Siniestro` no importan nada de
mensajería; la prueba de la regla de negocio no necesita un bróker.

(+) **Cierra el riesgo R4 del ATAM** y da contenido verificable al requisito de trazabilidad.

(−) **Un componente de infraestructura más que operar.** RabbitMQ hay que levantarlo,
monitorearlo y respaldarlo; en un proyecto de curso eso se traduce en un contenedor más en
`docker compose`.

(−) **Consistencia eventual visible.** Justo después de evaluar, consultar la auditoría puede
devolver 404 durante unos milisegundos. Hay que documentarlo porque parece un fallo y no lo es.

(−) **Los mensajes deben sobrevivir a la caída del consumidor**, lo que obliga a declarar la
cola como durable y a no usar auto-ack con pérdida de mensajes. Se configura
`default-requeue-rejected: false` para no entrar en bucle infinito ante un mensaje veneno.

(−) **El contrato del evento ahora es una API.** El record `EventoSiniestro` no puede cambiar
de forma incompatible sin coordinar productor y consumidor; hoy está duplicado en los dos
servicios y nada detecta la divergencia en tiempo de compilación.
