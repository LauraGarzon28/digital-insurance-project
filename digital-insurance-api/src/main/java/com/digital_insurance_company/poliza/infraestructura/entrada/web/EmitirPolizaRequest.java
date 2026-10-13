package com.digital_insurance_company.poliza.infraestructura.entrada.web;

import java.time.LocalDate;
import java.util.UUID;


public record EmitirPolizaRequest(UUID cotizacionId, LocalDate fechaInicio, LocalDate fechaFin ) {

}
