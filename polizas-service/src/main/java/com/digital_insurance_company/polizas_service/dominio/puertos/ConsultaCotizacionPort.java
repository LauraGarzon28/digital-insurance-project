package com.digital_insurance_company.polizas_service.dominio.puertos;

import java.util.Optional;
import java.util.UUID;

import com.digital_insurance_company.polizas_service.dominio.DatosCotizacionParaEmision;

public interface ConsultaCotizacionPort {

    Optional<DatosCotizacionParaEmision> buscarParaEmision(UUID cotizacionId);

}
