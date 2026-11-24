# Checklist para el corte del proyecto — G5 Aseguradora Digital

**Corte: domingo 22 de noviembre, 23:59.**
Generado el 2026-11-22 a partir de la verificación real sobre un clon limpio.

---

## Estado actual, medido

| Rama | Commit | Puntaje |
|---|---|---|
| `feature/dev3-siniestros-service` (Nicolás) | `ea82c63` | **67 / 70** |
| `auth-service` (Laura, PR #14) | `6f819e7` | 36 / 70 |
| `main` | `eb6d40c` | ~19 / 70 |

El 67/70 está medido con `VerificarProyecto.java --integrantes 3 --pruebas` sobre un
**clon limpio**, no sobre la máquina local.

```
Commit:  ea82c6359fc2f470e496167fa790cfe66b73fe28
Huella:  02bac949a10d
Total:   67 / 70
```

| Bloque | Puntos | Estado |
|---|---|---|
| A · Construcción | 20 / 20 | Completo |
| B · Arquitectura (L3–L6) | 25 / 25 | Completo |
| C · Documentación | 15 / 15 | Completo |
| D · Equipo | 7 / 10 | **Falta D3** |

---

## Lo que falta y quién lo hace

### Diego — 3 puntos (D3)

El script exige que nadie quede por debajo de la mitad del promedio de commits:

```
D3 | Aporte equilibrado | 0 / 3 | minimo exigido 11.2 commits por integrante
```

Hoy: Laura 33, Nicolás 26, **Diego 8**.

**Diego necesita al menos 3 commits propios más** antes de sacar el hash del corte. No sirve
que otro commitee por él: el script lee el autor de cada commit.

Cómo conseguirlos de forma legítima, en su subdominio:

1. Mergear `feature/poliza-service` a `main` (ese commit cuenta).
2. Ajustar los puertos de `polizas-service`: hoy usa 8082 y choca con `clientes-service`.
   Debe pasar a **8084**… pero ojo, 8084 ya es de `siniestros-service`.
   → **`polizas-service` a 8086** y su base de datos a **5438**.
3. Un commit de documentación actualizando su bitácora en `evidencias/dev-2/`.
4. Registrar `polizas-service` en Eureka y cambiar la ruta del gateway de
   `http://localhost:8090` a `lb://polizas-service`.

Cada uno de esos pasos es un commit real con contenido real.

### Laura — PR #14 y 3 puntos de A3

El PR ya está **aprobado** con observaciones. Quedaron dos comentarios en el PR; ninguno
bloquea el merge, pero conviene atenderlos:

1. **A3 (8 puntos)** — `gateway`, `discovery-server` y `clientes-service` solo tienen
   `contextLoads()`, que el script excluye. Ya hay una prueba real escrita para cada uno en
   la rama de Nicolás (`RutasGatewayTest`, `DiscoveryServerTest`,
   `ClienteApplicationServiceTest`); si esta rama se mergea, el problema queda resuelto.
2. **`keys/`** — `auth-service` no arranca en un clon limpio sin el par RSA.
   Se resuelve con `scripts/generar-llaves.ps1`, ya incluido en esta rama.

### Todos — orden del merge

`feature/dev3-siniestros-service` **sale de `auth-service`**, así que:

- Si el PR #14 se mergea primero: el PR #15 se rebasa sin conflictos en `docker-compose.yml`.
- Si el PR #15 se mergea primero: absorbe lo de #14 y #14 puede cerrarse.

Verifiqué que `feature/poliza-service` también mergea limpio sobre esta rama
(`Automatic merge went well`).

---

## Ritual de corte (repetible)

Desde un clon limpio, en el commit que se va a entregar:

```powershell
git clone https://github.com/LauraGarzon28/digital-insurance-project.git evaluado
cd evaluado
git checkout <hash-del-commit>

java "C:\Users\sergi\U\2026-2\Software 2\Ingenieria-Software-II\01-Pagina-web-Software-II\VerificarProyecto.java" `
     . --integrantes 3 --pruebas
```

La bandera `--pruebas` es obligatoria: sin ella A3 sale en 0 y se pierden 8 puntos.

**Antes de sacar el hash:**

1. `git status` debe decir *working tree clean*.
2. `git push` y confirmar *up to date*.
3. `git shortlog -sne --all` debe mostrar tres personas, sin duplicados (el `.mailmap` ya
   unifica las identidades de Laura y Nicolás).

---

## Texto de entrega en Moodle

```
PROYECTO (grupo G5) — Aseguradora digital
Repositorio: https://github.com/LauraGarzon28/digital-insurance-project
Commit del corte: <hash completo>
Integrantes: 3
Informe de verificación con huella: 02bac949a10d
```

> La huella cambia si se hace cualquier commit nuevo. Si se mergea el PR de Diego, hay que
> volver a correr el verificador y actualizar la huella.

---

## Estado del código tras las actualizaciones finales

Registrado en `docs/ARQUITECTURA.md` §7:

| Elemento | Estado actual |
|---|---|
| Consulta de póliza desde Siniestros (riesgo R1) | **Cerrado — ADR P-01 implementado** (`PolizasRestAdapter`) |
| Persistencia de Pólizas y demás contextos | **Cerrado** — 6 bases PostgreSQL propias con Flyway |
| Monolito legado (`digital-insurance-api/`) | **Cerrado** — retirado del repositorio |
| Contrato de `EventoSiniestro` duplicado en productor y consumidor | Deuda registrada |
| Pruebas de arquitectura (ArchUnit) | F1 propuesta; F2 por separación Maven y F3–F5 implementadas |
| Saga de cancelación / Outbox en Siniestros / DLQ en Auditoría | Pendiente como mejora posterior |

Lo no implementado sigue marcado explícitamente como **propuesta** o **deuda** en la
documentación (criterio M5: no presentar como hecho lo que no está construido).
