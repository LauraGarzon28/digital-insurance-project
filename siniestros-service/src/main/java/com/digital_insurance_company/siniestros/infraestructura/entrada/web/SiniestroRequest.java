package com.digital_insurance_company.siniestros.infraestructura.entrada.web;

import java.util.UUID;

public record SiniestroRequest(UUID polizaId, String descripcion) {
}
