# Proyecto G5 - Aseguradora Digital

## 1. Eventos de Dominio 
* `CotizacionSolicitada` (HU-01)
* `RiesgoEvaluado` (HU-02)
* `PrimaCalculada` (HU-02)
* `CotizacionMarcadaParaRevision` (HU-02)
* `CotizacionAceptada` (HU-03)
* `PolizaEmitida` (HU-03)
* `PolizaConsultada` (HU-04)
* `PolizaRenovada` (HU-05)
* `PolizaCancelada` (HU-05)
* `SiniestroReportado` (HU-06)
* `SiniestroAsignadoAjustador` (HU-07)
* `SiniestroEvaluado` (HU-08)
* `SiniestroResuelto` (HU-08)
* `HistoricoSiniestrosConsultado` (HU-10)

## 2. Eventos Pivote 
1. **`CotizacionAceptada`**: Marca la transición entre el análisis prospectivo de riesgo/comercial y el compromiso contractual legal (emisión de la póliza). Cambia la responsabilidad de evaluación a cumplimiento legal.
2. **`SiniestroReportado`**: Conecta la vigencia de la póliza activa con la gestión operativa del reclamo. Requiere validar el estado de la póliza antes de permitir cualquier flujo de evaluación.

## 3. Bounded Contexts Candidatos
* **Cotización y Suscripción**: Determina la prima y la asegurabilidad según reglas de riesgo vigentes sin generar compromisos legales vigentes.
* **Pólizas**: Administra el ciclo de vida del contrato (emisión, vigencia, renovación y cancelación) y garantiza la validez legal del seguro.
* **Siniestros**: Gestiona el flujo operativo de reclamos, asignación a ajustadores y decisiones de liquidación de indemnizaciones.

## 4. Asignación de Subdominios
* **Dev 1 - Laura Garzón**: Subdominio de Cotización y Suscripción
* **Dev 2 - Diego Sierra**: Subdominio de Pólizas
* **Dev 3 - Nicolás Ruiz**: Subdominio de Siniestros

## 5. Repositorio de GitHub
* **URL**: [https://github.com/LauraGarzon28/digital-insurance-project](https://github.com/LauraGarzon28/digital-insurance-project)