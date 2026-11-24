package com.digital_insurance_company.siniestros.dominio.puertos;

import java.util.Optional;
import java.util.UUID;

import com.digital_insurance_company.siniestros.dominio.DatosPolizaParaSiniestro;

public interface ConsultaPolizaPort {

    Optional<DatosPolizaParaSiniestro> buscarPorId(UUID polizaId);
}
