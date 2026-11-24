# Aseguradora Digital — Proyecto G5

**Ingeniería de Software II · UPTC**

Plataforma para cotizar seguros, emitir pólizas y gestionar el reporte y seguimiento de
siniestros, con reglas propias de suscripción y evaluación de riesgo.

Arquitectura: **microservicios con Spring Boot + Eureka + API Gateway**, cada contexto con
**arquitectura hexagonal** (dominio / aplicación / infraestructura) y **base de datos propia**.

---

## 1. Requisitos

| Herramienta | Versión | Para qué |
|---|---|---|
| **JDK** | 25 | Compilar y ejecutar los servicios |
| **Docker Desktop** | 24+ | PostgreSQL y RabbitMQ |
| **Maven Wrapper** | incluido | Cada servicio trae `mvnw` / `mvnw.cmd` |

Verificar: `java -version` debe reportar `25`.

---

## 2. Arranque paso a paso

Todos los comandos se ejecutan **desde la raíz del repositorio**.

### 2.1 Levantar la infraestructura

```bash
docker compose up -d
```

Levanta seis PostgreSQL y RabbitMQ:

| Contenedor | Puerto host | Base de datos |
|---|---|---|
| `auth-db` | 5433 | `auth_db` |
| `clientes-db` | 5434 | `clientes_db` |
| `cotizaciones-db` | 5435 | `cotizaciones_db` |
| `siniestros-db` | 5436 | `siniestros_db` |
| `auditoria-db` | 5437 | `auditoria_db` |
| `polizas-db` | 5438 | `polizas_db` |
| `insurance-rabbitmq` | 5672 · 15672 (consola) | — |

Esperar a que todos reporten `healthy`: `docker compose ps`.

### 2.2 Generar las llaves RSA del `auth-service`

El `auth-service` firma los JWT con un par RSA. **Las llaves no se versionan** (están en
`.gitignore`), así que cada quien genera las suyas una sola vez:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/generar-llaves.ps1
```

Crea `keys/private.pem` y `keys/public.pem`. Si ya existen, el script no las sobreescribe.

> Sin este paso el `auth-service` no arranca y los demás servicios no pueden validar tokens.

### 2.3 Arrancar los servicios

**El orden importa**: primero Eureka y autenticación; luego los servicios de negocio en el
orden Cotizaciones → Pólizas → Siniestros. Arranca el gateway al final.

| Orden | Servicio | Puerto | Comando |
|---:|---|---:|---|
| 1 | `discovery-server` | 8761 | `cd discovery-server` → `./mvnw spring-boot:run` |
| 2 | `auth-service` | 8081 | `cd auth-service` → `./mvnw spring-boot:run` |
| 3 | `clientes-service` | 8082 | `cd clientes-service` → `./mvnw spring-boot:run` |
| 4 | `cotizaciones-service` | 8083 | `cd cotizaciones-service` → `./mvnw spring-boot:run` |
| 5 | `polizas-service` | 8086 | `cd polizas-service` → `./mvnw spring-boot:run` |
| 6 | `siniestros-service` | 8084 | `cd siniestros-service` → `./mvnw spring-boot:run` |
| 7 | `auditoria-service` | 8085 | `cd auditoria-service` → `./mvnw spring-boot:run` |
| 8 | `gateway` | 8080 | `cd gateway` → `./mvnw spring-boot:run` |

En Windows usar `.\mvnw.cmd` en lugar de `./mvnw`.

Verificar el registro: <http://localhost:8761> debe listar los servicios arriba.
Consola de RabbitMQ: <http://localhost:15672> (`guest` / `guest`).

---

## 3. Flujo completo: cliente, cotización, póliza y siniestro

Este es el flujo que demuestra la integración completa. Usa el **API Gateway** como única
puerta de entrada.

### 3.1 Obtener el token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin12345"}'
```

Respuesta:

```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

Guardar el token:

```bash
TOKEN="pegar-el-accessToken-aqui"
```

> El usuario `admin` lo siembra `AdminInicial` al arrancar `auth-service`.

### 3.2 Crear un cliente y una cotización

```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nombreCompleto":"Laura Ejemplo","tipoDocumento":"CC","numeroDocumento":"123456789","correo":"laura@example.com","telefono":"3001234567"}'
```

Usa el `id` devuelto como `CLIENTE_ID` para solicitar una cotización:

```json
curl -X POST http://localhost:8080/api/cotizaciones \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"clienteId":"<CLIENTE_ID>","descripcionRiesgo":"Vehículo familiar","valorAsegurado":50000000,"puntajeRiesgo":20}'
```

Guarda el `id` de la cotización como `COTIZACION_ID`, evalúala y acéptala:

```bash
curl -X POST "http://localhost:8080/api/cotizaciones/$COTIZACION_ID/evaluacion" \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"versionReglas":"2026.2","tasaBase":0.035,"umbralRevisionManual":70}'

curl -X POST "http://localhost:8080/api/cotizaciones/$COTIZACION_ID/aceptacion" \
  -H "Authorization: Bearer $TOKEN"
```

### 3.3 Emitir la póliza

Pólizas consulta la cotización aceptada por Eureka antes de emitir. Guarda el `id` devuelto
como `POLIZA_ID`.

```bash
curl -X POST http://localhost:8080/api/polizas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"cotizacionId":"<COTIZACION_ID>","fechaInicio":"2026-11-24","fechaFin":"2027-11-24"}'
```

### 3.4 Reportar un siniestro

Siniestros obtiene de Pólizas el valor asegurado; el cliente ya no puede suministrar ese dato.

```bash
curl -X POST http://localhost:8080/api/siniestros \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"polizaId":"<POLIZA_ID>","descripcion":"Choque frontal"}'
```

Guarda el `id` devuelto como `SINIESTRO_ID`. La cadena síncrona es
`gateway → siniestros-service → polizas-service`; Siniestros publica el evento de reporte
por RabbitMQ para que Auditoría lo persista.

### 3.5 Evaluar el siniestro

```bash
curl -X POST http://localhost:8080/api/siniestros/$SINIESTRO_ID/evaluacion \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"esFraude":false,"montoPropuesto":10000}'
```

Siniestros vuelve a consultar el estado real de la póliza antes de evaluar. Responde `200 OK`
con `"estado": "APROBADO"` y `"montoAprobado": 10000.00`, y publica `SiniestroResuelto` en
RabbitMQ.

### 3.6 Verificar la auditoría (vía asíncrona)

```bash
curl http://localhost:8080/api/auditoria/siniestros/$SINIESTRO_ID \
  -H "Authorization: Bearer $TOKEN"
```

Responde la lista de hechos consumidos por `auditoria-service`:

```json
[
  { "tipo": "SiniestroReportado", "estado": "REPORTADO", "montoAprobado": null },
  { "tipo": "SiniestroResuelto",  "estado": "APROBADO",  "montoAprobado": 10000.00 }
]
```

Si responde `404`, el consumidor aún no procesó el mensaje: esperar un segundo y repetir.

---

## 4. Colecciones Bruno

Abrir la carpeta `bruno/` con [Bruno](https://www.usebruno.com/). Cada carpeta es un taller:

| Carpeta | Qué contiene |
|---|---|
| `bruno/taller-03-ddd/` | Recorrido de los pasos del taller de DDD (8 requests) |
| `bruno/taller-04-hexagonal/` | Recorrido completo del hexágono (7 requests) |
| `bruno/taller-06-mensajeria/` | **Comunicación asíncrona**: 2 flujos con sus asserts |

### `taller-06-mensajeria` — los dos flujos

1. **Token** → 2. **Flujo 1: reportar** → 3. **aprobar** → 4. **verificar auditoría**
5. **Flujo 2: reportar con fraude** → 6. **rechazar por fraude** → 7. **verificar auditoría**
→ 8. **reintentar evaluación** (debe dar 400 y no duplicar eventos)

Cada request trae sus propios `tests`, así que la colección se puede correr completa y falla
si el contrato cambia. Para `taller-03-ddd` y `taller-04-hexagonal`, configura el entorno
`local` de `bruno/environments/local.bru` con un `token` válido y un `polizaId` vigente emitido
por el flujo de §3. Para el rechazo por vencimiento de `taller-03-ddd`, configura además
`polizaVencidaId` con una póliza vencida. `taller-06-mensajeria` conserva su propio entorno
local.

---

## 5. Pruebas

Cada servicio tiene su suite independiente. Sin Spring ni base de datos en el núcleo: los
casos de uso se prueban con *fakes* escritos a mano.

```bash
cd siniestros-service && ./mvnw test
```

| Servicio | Pruebas |
|---|---:|
| `cotizaciones-service` | 33 |
| `polizas-service` | — |
| `siniestros-service` | 30 |
| `auth-service` | 1 |
| `clientes-service` | 4 |
| `auditoria-service` | 4 |
| `discovery-server` | 3 |
| `gateway` | 3 |

Correr todas de una vez (PowerShell):

```powershell
Get-ChildItem -Directory | Where-Object { Test-Path "$_\pom.xml" } | ForEach-Object {
    Write-Host "== $($_.Name) =="
    Push-Location $_.FullName; .\mvnw.cmd -q -B -ntp test; Pop-Location
}
```

---

## 6. Arquitectura

```
digital-insurance-project/
├── discovery-server/         Eureka
├── gateway/                  API Gateway (punto único de entrada)
├── auth-service/             Identidad y JWT
├── clientes-service/         Clientes
├── cotizaciones-service/     Cotización y suscripción
├── polizas-service/          Pólizas (PostgreSQL; consulta Cotizaciones por Eureka)
├── siniestros-service/       Siniestros
├── auditoria-service/        Consumidor de hechos (auditoría)
├── bruno/                    Colecciones de requests
├── docs/                     Arquitectura, ADR y cierre del proyecto
└── docker-compose.yml        PostgreSQL + RabbitMQ
```

Cada servicio de dominio sigue **arquitectura hexagonal**:

```
<contexto>/
├── dominio/          Entidades, value objects, agregados, servicios de dominio y puertos
├── aplicacion/       Casos de uso (orquestan el dominio)
└── infraestructura/  Adaptadores de entrada (web, mensajería) y de salida (persistencia)
```

Las dependencias apuntan **hacia adentro**: el dominio no conoce Spring, ni JPA, ni RabbitMQ.

Detalle completo, diagramas C4 y decisiones en [`docs/ARQUITECTURA.md`](docs/ARQUITECTURA.md).
`digital-insurance-api/` ya no forma parte del arranque ni del enrutamiento; tras verificar
el flujo de esta guía, queda como código legado que puede retirarse.

---

## 7. Documentación

| Documento | Contenido |
|---|---|
| [`docs/ARQUITECTURA.md`](docs/ARQUITECTURA.md) | Contexto, estilos, diagrama de contenedores, decisiones, mesh y deuda |
| [`docs/adr/`](docs/adr/) | Siete ADR: cada uno con alternativa descartada y consecuencias negativas |
| [`docs/cierre/ATAM-LITE.md`](docs/cierre/ATAM-LITE.md) | Árbol de utilidad, riesgos y compromisos |
| [`docs/cierre/MATRIZ.md`](docs/cierre/MATRIZ.md) | Matriz de decisión ponderada y sensibilidad |
| [`docs/cierre/GOBERNANZA.md`](docs/cierre/GOBERNANZA.md) | Guardarraíles, deuda técnica y radar |
| [`docs/cierre/CASO.md`](docs/cierre/CASO.md) | Caso real y su aplicabilidad |
| [`docs/cierre/RESUMEN-EJECUTIVO.md`](docs/cierre/RESUMEN-EJECUTIVO.md) | Una página para decidir sin jerga |
| [`docs/cierre/REPARTO.md`](docs/cierre/REPARTO.md) | Quién lidera cada parte y evidencia en Git |
| [`NOTAS-equipo.md`](NOTAS-equipo.md) | Event Storming y asignación de subdominios |

---

## 8. Reparto del equipo

| Integrante | Subdominio | Carpeta |
|---|---|---|
| Laura Garzón | Cotización y suscripción | `cotizaciones-service/`, `clientes-service/`, `auth-service/`, `gateway/`, `discovery-server/` |
| Diego Sierra | Pólizas | `polizas-service/` |
| Nicolás Ruiz | Siniestros | `siniestros-service/`, `auditoria-service/` |

---

## 9. Solución de problemas

| Síntoma | Causa y solución |
|---|---|
| `auth-service` no arranca: no encuentra `keys/public.pem` | Ejecutar `scripts/generar-llaves.ps1` (§2.2) |
| Los servicios no se ven en Eureka | Arrancarlos en el orden de §2.3 y esperar 30 s |
| `Connection refused` a `localhost:8761` | `discovery-server` no está arriba: arrancarlo primero |
| `404` en `/api/auditoria/...` | El consumidor aún no procesó el evento: esperar y repetir |
| Gateway da 503 en `/api/polizas` | Verificar que `polizas-service` aparezca en Eureka y que escuche en 8086 |
| `403` en cualquier endpoint | Falta el header `Authorization: Bearer <token>` |
| `Conflict. The container name "/rabbitmq" is already in use` | Hay otro RabbitMQ de un proyecto anterior. Este compose usa `insurance-rabbitmq` para no chocar; si aun así aparece, `docker rm <nombre>` |
| RabbitMQ arranca pero el consumidor no recibe nada | Verificar que la cola `auditoria.siniestros.queue` exista en <http://localhost:15672> → Queues |
