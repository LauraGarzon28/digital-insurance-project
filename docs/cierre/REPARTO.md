# Reparto del proyecto — G5 Aseguradora digital

**Lección 13.** Quién lidera cada parte y la evidencia en el historial de Git.

---

## Subdominios (ley de Conway aplicada)

| Integrante | Subdominio | Carpeta en el repositorio | Agregado raíz |
|---|---|---|---|
| **Laura Garzón** | Cotización y suscripción | `cotizaciones-service/`, `clientes-service/`, `auth-service/`, `gateway/`, `discovery-server/` | `Cotizacion` |
| **Diego Sierra** | Pólizas | `digital-insurance-api/` (paquete `poliza`) | `Poliza` |
| **Nicolás Ruiz** | Siniestros | `siniestros-service/`, `auditoria-service/` | `Siniestro` |

El reparto coincide con el Event Storming de `NOTAS-equipo.md`: cada evento pivote
(`CotizacionAceptada`, `SiniestroReportado`) marcó una frontera de contexto, y cada contexto
tiene un dueño principal.

Desde el corte del proyecto, **cada subdominio es una carpeta de servicio propia**, lo que
hace que la evidencia en Git sea directa: el historial de una carpeta es el historial de su
dueño.

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

# Evidencia por integrante y su carpeta (lo que pide la rúbrica M6)
git log --oneline --author="Laura"   -- cotizaciones-service/ clientes-service/ auth-service/ gateway/ discovery-server/
git log --oneline --author="Diego"   -- digital-insurance-api/
git log --oneline --author="Nicolas" -- siniestros-service/ auditoria-service/
```

**Aporte registrado en el historial (antes del corte del proyecto):**

| Autor | Commits | Archivos distintos |
|---|---:|---:|
| Laura Garzón | 33 | 198 |
| Nicolás Ruiz | 25 | 70 |
| Diego Sierra | 9 | 102 |

> **Nota sobre D3:** el script exige que nadie quede por debajo de la mitad del promedio de
> commits. Con el reparto actual, Diego necesita **al menos 3 commits propios más** antes de
> obtener el hash del corte. Los tiene disponibles al mergear `polizas-service` y ajustar sus
> puertos.

---

## Alcance evaluado

El curso evalúa el código hasta la **Lección 6**. Las piezas de las lecciones 8 a 13 aparecen
en la documentación como **propuestas**, nunca como implementadas. Los ADR llevan su estado
explícito: **Aceptado** para lo construido, **Propuesta** para lo diseñado y no desplegado.

Situación al corte:

- **Aceptado e implementado:** P-02, P-03, P-04, P-06, P-07.
- **Propuesta, no implementado:** P-01 (consulta de póliza), P-05 (CQRS del histórico).
