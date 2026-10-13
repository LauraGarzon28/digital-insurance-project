**PROYECTO G5**

**Ingeniería de Software II**

# Aseguradora digital: cotización, pólizas y siniestros

## 1. Narrativa

Plataforma para cotizar seguros, emitir pólizas y gestionar el reporte y seguimiento de siniestros, con reglas propias de suscripción y evaluación de riesgo.

### Actores

- Cliente
- Asesor comercial
- Suscriptor (evalúa riesgo)
- Ajustador de siniestros

## 2. Historias de usuario

**HU-01.** Como cliente, quiero cotizar una póliza indicando mis datos y el riesgo a asegurar, para conocer el costo antes de comprar.

**HU-02.** Como suscriptor, quiero que el sistema evalúe el riesgo y determine la prima según reglas de suscripción, para decidir si el riesgo es asegurable.

  - Criterio de aceptación: Si el riesgo supera el umbral configurado, la cotización se marca para revisión manual del suscriptor.

  - Criterio de aceptación: La prima calculada queda asociada a la versión de las reglas de suscripción vigente en ese momento.

**HU-03.** Como cliente, quiero emitir una póliza a partir de una cotización aceptada, para quedar formalmente asegurado.

**HU-04.** Como cliente, quiero consultar mis pólizas activas, para saber qué tengo cubierto.

**HU-05.** Como cliente, quiero renovar o cancelar una póliza, para ajustar mi cobertura en el tiempo.

**HU-06.** Como cliente, quiero reportar un siniestro asociado a una póliza, para iniciar el proceso de reclamación.

  - Criterio de aceptación: El siniestro se rechaza si la póliza está vencida o cancelada.

  - Criterio de aceptación: El siniestro queda en estado 'en evaluación' hasta que un ajustador lo resuelve.

**HU-07.** Como asesor comercial, quiero asignar un siniestro reportado a un ajustador, para que se gestione.

**HU-08.** Como ajustador, quiero registrar mi evaluación y decisión sobre un siniestro (aprobado, rechazado, monto), para cerrar el caso.

**HU-09.** Como cliente, quiero consultar el estado de mi siniestro, para saber en qué va mi reclamación.

**HU-10.** Como suscriptor, quiero consultar el histórico de siniestros de un cliente o póliza, para mejorar futuras evaluaciones de riesgo.

## 3. Reglas de negocio

- Una póliza no se emite si la evaluación de riesgo la rechaza.
- Un siniestro no puede reportarse sobre una póliza vencida o cancelada.
- El monto aprobado de un siniestro no puede superar el valor asegurado de la póliza.
- Una póliza cancelada no puede renovarse; debe cotizarse de nuevo.

## 4. Requisitos no funcionales

- Las reglas de suscripción y evaluación de riesgo son complejas y deben poder evolucionar sin afectar el resto del sistema.
- Las consultas de cotización (alto volumen) deben separarse de las operaciones de emisión y siniestros (bajo volumen, alta importancia transaccional).
- Toda decisión sobre una póliza o siniestro debe quedar auditada.
- Confidencialidad de los datos personales y financieros de los clientes.

## 5. Entidades clave del dominio

Cliente · Cotización · Póliza · Riesgo · Siniestro · Ajustador