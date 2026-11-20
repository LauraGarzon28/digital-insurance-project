package com.digital_insurance_company.polizas_service.infraestructura.salida.cotizacion;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.digital_insurance_company.polizas_service.dominio.DatosCotizacionParaEmision;
import com.digital_insurance_company.polizas_service.dominio.puertos.ConsultaCotizacionPort;

@Component
public class InMemoryConsultaCotizacionPort implements ConsultaCotizacionPort {

    private final Map<UUID, DatosCotizacionParaEmision> cotizaciones = new ConcurrentHashMap<>();

    public void sembrar(DatosCotizacionParaEmision datos) {
        cotizaciones.put(datos.cotizacionId(), datos);
    }

    @Override
    public Optional<DatosCotizacionParaEmision> buscarParaEmision(UUID cotizacionId) {
        return Optional.ofNullable(cotizaciones.get(cotizacionId));
    }
}
