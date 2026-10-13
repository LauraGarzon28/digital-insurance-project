package com.digital_insurance_company.poliza.infraestructura.salida.cotizacion;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.digital_insurance_company.cotizacion.aplicacion.CotizacionUseCase;
import com.digital_insurance_company.cotizacion.dominio.Cotizacion;
import com.digital_insurance_company.cotizacion.dominio.EstadoCotizacion;
import com.digital_insurance_company.poliza.dominio.DatosCotizacionParaEmision;
import com.digital_insurance_company.poliza.dominio.puertos.ConsultaCotizacionPort;

@Component
public class ConsultaCotizacionAdapter implements ConsultaCotizacionPort {

    private final CotizacionUseCase casosDeUsoCotizacion;

    public ConsultaCotizacionAdapter(CotizacionUseCase casosDeUsoCotizacion) {
        this.casosDeUsoCotizacion = casosDeUsoCotizacion;
    }

    @Override
    public Optional<DatosCotizacionParaEmision> buscarParaEmision(UUID cotizacionId) {
        return casosDeUsoCotizacion.buscarPorId(cotizacionId).map(this::traducir);
    }

    private DatosCotizacionParaEmision traducir(Cotizacion cotizacion) {
        boolean aceptada = cotizacion.estado() == EstadoCotizacion.ACEPTADA;
        return new DatosCotizacionParaEmision(
                cotizacion.id(),
                cotizacion.clienteId(),
                cotizacion.riesgo().valorAsegurado(),
                aceptada);
    }
}
