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

Levanta cinco PostgreSQL y RabbitMQ:

| Contenedor | Puerto host | Base de datos |
|---|---|---|
| `auth-db` | 5433 | `auth_db` |
| `clientes-db` | 5434 | `clientes_db` |
| `cotizaciones-db` | 5435 | `cotizaciones_db` |
| `siniestros-db` | 5436 | `siniestros_db` |
| `auditoria-db` | 5437 | `auditoria_db` |
| `rabbitmq` | 5672 · 15672 (consola) | — |

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

**El orden importa**: primero el descubrimiento, después autenticación, y al final el gateway
(que necesita a los demás registrados en Eureka).

| Orden | Servicio | Puerto | Comando |
|---:|---|---:|---|
| 1 | `discovery-server` | 8761 | `cd discovery-server` → `./mvnw spring-boot:run` |
| 2 | `auth-service` | 8081 | `cd auth-service` → `./mvnw spring-boot:run` |
| 3 | `siniestros-service` | 8084 | `cd siniestros-service` → `./mvnw spring-boot:run` |
| 4 | `auditoria-service` | 8085 | `cd auditoria-service` → `./mvnw spring-boot:run` |
| 5 | `cotizaciones-service` | 8083 | `cd cotizaciones-service` → `./mvnw spring-boot:run` |
| 6 | `clientes-service` | 8082 | `cd clientes-service` → `./mvnw spring-boot:run` |
| 7 | `digital-insurance-api` | 8090 | `cd digital-insurance-api` → `./mvnw spring-boot:run` |
| 8 | `gateway` | 8080 | `cd gateway` → `./mvnw spring-boot:run` |

En Windows usar `.\mvnw.cmd` en lugar de `./mvnw`.

Verificar el registro: <http://localhost:8761> debe listar los servicios arriba.
Consola de RabbitMQ: <http://localhost:15672> (`guest` / `guest`).

---

## 3. Petición que cruza dos servicios

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

### 3.2 Reportar un siniestro

```bash
curl -X POST http://localhost:8080/api/siniestros \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"polizaId":"33333333-3333-3333-3333-333333333333","descripcion":"Choque frontal","valorAseguradoPoliza":50000}'
```

Respuesta `201 Created`:

```json
{
  "id": "8f3c1d2e-...",
  "polizaId": "33333333-3333-3333-3333-333333333333",
  "descripcion": "Choque frontal",
  "estado": "REPORTADO",
  "valorAseguradoPoliza": 50000,
  "montoAprobado": null
}
```

**Cadena que se ejecuta:**

```
gateway (:8080)
   └─▶ siniestros-service (:8084) ──▶ PostgreSQL siniestros_db
              │
              └── SiniestroReportado ──▶ RabbitMQ ──▶ auditoria-service ──▶ auditoria_db
```

Es decir: una petición síncrona del gateway al servicio de Siniestros, y **una propagación
asíncrona** por RabbitMQ hacia la auditoría. Guardar el `id` devuelto como `SINIESTRO_ID`.

### 3.3 Evaluar el siniestro

```bash
curl -X POST http://localhost:8080/api/siniestros/$SINIESTRO_ID/evaluacion \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"estadoPoliza":"ACTIVA","esFraude":false,"montoPropuesto":10000}'
```

Responde `200 OK` con `"estado": "APROBADO"` y `"montoAprobado": 10000.00`. Se publica
`SiniestroResuelto` en RabbitMQ.

### 3.4 Verificar la auditoría (segundo servicio, vía asíncrona)

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
si el contrato cambia.

---

## 5. Pruebas

Cada servicio tiene su suite independiente. Sin Spring ni base de datos en el núcleo: los
casos de uso se prueban con *fakes* escritos a mano.

```bash
cd siniestros-service && ./mvnw test
```

| Servicio | Pruebas |
|---|---:|
| `digital-insurance-api` | 82 |
| `cotizaciones-service` | 33 |
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
├── digital-insurance-api/    Pólizas (+ cliente y cotización heredados)
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
| Diego Sierra | Pólizas | `digital-insurance-api/` (`poliza`) |
| Nicolás Ruiz | Siniestros | `siniestros-service/`, `auditoria-service/` |

---

## 9. Solución de problemas

| Síntoma | Causa y solución |
|---|---|
| `auth-service` no arranca: no encuentra `keys/public.pem` | Ejecutar `scripts/generar-llaves.ps1` (§2.2) |
| Los servicios no se ven en Eureka | Arrancarlos en el orden de §2.3 y esperar 30 s |
| `Connection refused` a `localhost:8761` | `discovery-server` no está arriba: arrancarlo primero |
| `404` en `/api/auditoria/...` | El consumidor aún no procesó el evento: esperar y repetir |
| Gateway y monolito chocan en el puerto | Ya resuelto: el monolito usa 8090 desde §2.3 |
| `403` en cualquier endpoint | Falta el header `Authorization: Bearer <token>` |
