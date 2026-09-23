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
1. **`CotizacionAceptada`**: Marca la transiciÃ³n entre el anÃ¡lisis prospectivo de riesgo/comercial y el compromiso contractual legal (emisiÃ³n de la pÃ³liza). Cambia la responsabilidad de evaluaciÃ³n a cumplimiento legal.
2. **`SiniestroReportado`**: Conecta la vigencia de la pÃ³liza activa con la gestiÃ³n operativa del reclamo. Requiere validar el estado de la pÃ³liza antes de permitir cualquier flujo de evaluaciÃ³n.

## 3. Bounded Contexts Candidatos
* **CotizaciÃ³n y SuscripciÃ³n**: Determina la prima y la asegurabilidad segÃºn reglas de riesgo vigentes sin generar compromisos legales vigentes.
* **PÃ³lizas**: Administra el ciclo de vida del contrato (emisiÃ³n, vigencia, renovaciÃ³n y cancelaciÃ³n) y garantiza la validez legal del seguro.
* **Siniestros**: Gestiona el flujo operativo de reclamos, asignaciÃ³n a ajustadores y decisiones de liquidaciÃ³n de indemnizaciones.

## 4. AsignaciÃ³n de Subdominios
* **Dev 1 - Laura GarzÃ³n**: Subdominio de CotizaciÃ³n y SuscripciÃ³n
* **Dev 2 - Diego Sierra**: Subdominio de PÃ³lizas
* **Dev 3 - Sergio**:  Subdominio de Siniestros

## 5. Repositorio de GitHub
* **URL**: [https://github.com/LauraGarzon28/digital-insurance-project](https://github.com/LauraGarzon28/digital-insurance-project)

