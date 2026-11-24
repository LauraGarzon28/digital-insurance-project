# Reparto del proyecto — G5 Aseguradora digital

**Lección 13 (actualizada al estado actual del repositorio).** Quién lidera cada parte y la evidencia en el historial de Git.

---

## Subdominios (ley de Conway aplicada)

| Integrante | Subdominio | Carpetas en el repositorio | Agregado raíz |
|---|---|---|---|
| **Laura Garzón** | Cotización, suscripción, clientes y plataforma | `cotizaciones-service/`, `clientes-service/`, `auth-service/`, `gateway/`, `discovery-server/` | `Cotizacion`, `Cliente` |
| **Diego Sierra** | Pólizas | `polizas-service/` | `Poliza` |
| **Nicolás Ruiz** | Siniestros y auditoría | `siniestros-service/`, `auditoria-service/` | `Siniestro` |

El reparto coincide con el Event Storming de `NOTAS-equipo.md`: cada evento pivote (`CotizacionAceptada`, `SiniestroReportado`) marcó una frontera de contexto, y cada contexto tiene un dueño principal.

Cada subdominio cuenta con su propia carpeta de servicio independiente en el repositorio (el monolito legado `digital-insurance-api/` ya fue retirado).

---

## Reparto de la documentación de cierre

| Documento | Lidera | Discutido con |
|---|---|---|
| `ARQUITECTURA.md` | Nicolás Ruiz | Todo el grupo |
| `ATAM-LITE.md` | Nicolás Ruiz | Todo el grupo |
| `MATRIZ.md` | Laura Garzón | Todo el grupo |
| `CASO.md` | Diego Sierra | Todo el grupo |
| `GOBERNANZA.md` | Diego Sierra | Todo el grupo |
| `RESUMEN-EJECUTIVO.md` | Laura Garzón | Todo el grupo |
| ADR P-01 a P-07 | Dueño de cada subdominio | Todo el grupo |

---

## Verificación del historial

Para comprobar que cada integrante tiene trabajo propio **en su carpeta**:

```bash
# Resumen general
git shortlog -sne --all

# Evidencia por integrante y su carpeta (rúbrica M6)
git log --oneline --author="Laura"   -- cotizaciones-service/ clientes-service/ auth-service/ gateway/ discovery-server/
git log --oneline --author="Diego"   -- polizas-service/
git log --oneline --author="Nicolas" -- siniestros-service/ auditoria-service/
```

**Aporte registrado en `git log` (corte actual):**

| Autor | Commits | Archivos distintos |
|---|---:|---:|
| Laura Garzón | 191 | 621 |
| Nicolás Ruiz | 18 | 146 |
| Diego Sierra | 9 | 102 |

> **Nota sobre D3:** todos los integrantes superan el mínimo de 5 commits propios (criterio D1 en verde), aunque el criterio de equilibrio de commits (D3) permanece pendiente debido a la diferencia de commits totales frente al promedio del grupo.

---

## Alcance evaluado

El curso evalúa el código hasta la **Lección 6**. Los ADR llevan su estado explícito: **Aceptado** para lo construido y **Propuesta** para lo diseñado y aún no implementado.

Situación actual:

- **Aceptado e implementado:** P-01 (consulta síncrona a Pólizas), P-03 (puertos y adaptadores), P-06 (auditoría asíncrona con RabbitMQ), P-07 (extracción de `siniestros-service`, ampliada luego a los demás servicios).
- **Superado por la evolución del sistema:** P-02 y P-04 (el monolito modular y la extracción de un solo servicio fueron la etapa inicial antes de separar todos los servicios actuales).
- **Propuesta, no implementado:** P-05 (CQRS del histórico para HU-10).
