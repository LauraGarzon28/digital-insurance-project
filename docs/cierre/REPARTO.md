# Reparto del proyecto — G5 Aseguradora digital

**Lección 13.** Quién lidera cada parte y la evidencia en el historial de Git.

---

## Subdominios (ley de Conway aplicada)

| Integrante | Subdominio | Paquete en el código | Agregado raíz |
|---|---|---|---|
| **Laura Garzón** | Cotización y suscripción | `cotizacion` | `Cotizacion` |
| **Diego Sierra** | Pólizas | `poliza` | `Poliza` |
| **Nicolás Ruiz** | Siniestros | `siniestros` | `Siniestro` |

El reparto coincide con el Event Storming de `NOTAS-equipo.md`: cada evento pivote
(`CotizacionAceptada`, `SiniestroReportado`) marcó una frontera de contexto, y cada contexto
tiene un dueño principal.

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
| ADR P-01 a P-05 | Dueño de cada subdominio | Todo el grupo |

---

## Verificación del historial

Para comprobar que cada integrante tiene trabajo propio:

```bash
git shortlog -sne --all
git log --oneline --author="<usuario>"
git log --author="<usuario>" -- siniestros/   # ejemplo por carpeta
```

**Aporte registrado en el historial (commit `c28ba95`):**

| Autor | Commits | Archivos distintos |
|---|---:|---:|
| Laura Garzón | 22 | 75 |
| Nicolás Ruiz | 23 | 57 |
| Diego Sierra | 8 | 48 |

> **Nota:** los criterios D1 y D3 del script de verificación exigen al menos 5 commits por
> integrante y que nadie quede por debajo de la mitad del promedio. Diego tiene 8 commits
> frente a un mínimo calculado de 8.8, así que **D3 depende de un commit más suyo**. Conviene
> que cada integrante haga sus propios commits de aquí al 22 de noviembre.

---

## Alcance evaluado

El curso evalúa el código hasta la **Lección 6**. Las piezas de las lecciones 8 a 13 aparecen
en la documentación como **propuestas**, nunca como implementadas. Los ADR llevan su estado
explícito: **Aceptado** para lo construido, **Propuesta** para lo diseñado y no desplegado.
