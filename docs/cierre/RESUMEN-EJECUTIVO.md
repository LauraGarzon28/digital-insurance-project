# Aseguradora digital G5 — Resumen ejecutivo

**Para quien decide sin haber leído el código.**

---

## Qué es

Un sistema para que una aseguradora cotice un riesgo, emita una póliza y gestione el reclamo
cuando ocurre un daño. Tres capacidades del negocio, tres partes del software: Cotización,
Pólizas y Siniestros.

## Cómo está construido hoy

Una sola aplicación con tres departamentos internos bien separados. Los tres comparten
edificio y horario: se compilan, se despliegan y se caen juntos.

Cada departamento tiene sus reglas escritas por separado y se comunican por puertas formales,
no por ventanas. Eso es importante: si mañana hay que mudar un departamento a su propio
edificio, las puertas ya existen.

**Las 115 pruebas automáticas pasan**, lo que significa que las reglas del negocio hacen lo
que dicen.

## El problema que hay que resolver

**Al evaluar un reclamo, el sistema le cree al cliente.** Cuando alguien reporta un siniestro,
declara el estado de su póliza, y el sistema lo acepta. Si esa persona dice que su póliza está
vigente cuando en realidad fue cancelada, el sistema no lo detecta.

En una aseguradora esto no es un detalle técnico: es la diferencia entre pagar un reclamo
legítimo y pagar uno que no corresponde. **Es el riesgo más importante del proyecto y hoy está
abierto.**

La buena noticia: la solución ya tiene patrón dentro del mismo sistema. El departamento de
Pólizas consulta al de Cotización por una puerta formal para emitir; el de Siniestros puede
hacer lo mismo para verificar la póliza.

## El segundo problema, menos grave

Si hay que cambiar algo en reclamos, hay que mover toda la aplicación. Y si reclamos se satura
—una ola de siniestros, por ejemplo— se satura todo, incluida la emisión de pólizas. Hoy no se
puede crecer solo en la parte que crece.

## Qué se propone, en orden

1. **Que Siniestros consulte la póliza en vez de creerle al cliente.** Es la corrección más
   importante y no requiere mover nada de sitio.
2. **Que los datos sobrevivan al reinicio.** Hoy se pierden todos: las pólizas viven en una
   base temporal y las cotizaciones y siniestros en memoria.
3. **Extraer solo Siniestros a su propio servicio.** Resuelve el aislamiento y la escala, pero
   es más caro: obliga a coordinar dos sistemas. Se propone como fase posterior y medida.

## Qué se decidió NO hacer, y por qué

- **No se separaron los tres servicios a la vez.** Con tres personas y tres semanas, eso
  produce tres sistemas que parecen independientes pero solo funcionan juntos: toda la
  complejidad de lo distribuido y ninguno de sus beneficios.
- **No se guarda el historial completo de cada cambio** como secuencia de eventos. El estado
  actual es suficiente; el historial completo multiplicaría la complejidad sin necesidad.
- **No se usa infraestructura de malla de servicios.** Es para decenas de servicios con muchos
  equipos; aquí hay tres partes y un equipo pequeño.

## Lo que se pide

Nada que comprometa el calendario. El sistema funciona y está documentado. Lo que se recomienda,
en orden:

1. **Cerrar el riesgo de la póliza** antes de usar el sistema con datos reales.
2. **Dar persistencia real** a los tres departamentos.
3. **Mantener la disciplina de documentar cada decisión** con la alternativa que se descartó.
   Es lo que permite que alguien nuevo entienda el sistema en una hora.

## En una frase

> Tenemos un sistema bien dividido por dentro y fácil de operar, con las reglas del negocio
> probadas, al que le falta una corrección importante —dejar de confiar en el dato que declara
> el cliente al reclamar— y persistencia real; lo demás que se podría hacer está evaluado,
> cuantificado y decidido, no pendiente de improvisar.
