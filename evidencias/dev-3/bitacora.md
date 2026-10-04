# Bitácora de Implementación Individual - DDD
**Integrante:** Nicolás Ruiz (Dev 3)
**Subdominio:** Siniestros

| Paso | Tiempo estimado | Tiempo real | Fecha |
|---|---|---|---|
| 1 · Lenguaje Ubicuo (Entidad Siniestro) | ~20 min | 25 min | 23/09/2026 |
| 2 · Value Object (MontoAprobado) | ~45 min | 35 min | 23/09/2026 |
| 3 · Servicio de Dominio (EvaluacionSiniestro) | ~35 min | 30 min | 23/09/2026 |
| 4 · Límite del Agregado (Siniestro -> polizaId) | ~15 min | 15 min | 23/09/2026 |
| 5 · Factory (SiniestroFactory) | ~35 min | 25 min | 23/09/2026 |
| 6 · Commit y push | ~5 min | 5 min | 23/09/2026 |

**Trazabilidad:**
Se han generado commits independientes con el mensaje `feat(siniestros): ...` para respaldar el paso a paso detallado en esta bitácora, y se ejecutaron las pruebas unitarias que respaldan las reglas de negocio de `MontoAprobado` y la lógica de rechazo de la póliza en el servicio de evaluación.
