package com.digital_insurance_company.poliza.aplicacion;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import com.digital_insurance_company.poliza.dominio.DatosCotizacionParaEmision;
import com.digital_insurance_company.poliza.dominio.EmisionPolizaService;
import com.digital_insurance_company.poliza.dominio.Poliza;
import com.digital_insurance_company.poliza.dominio.PolizaFactory;
import com.digital_insurance_company.poliza.dominio.PolizaNoEncontradaException;
import com.digital_insurance_company.poliza.dominio.puertos.ConsultaCotizacionPort;
import com.digital_insurance_company.poliza.dominio.puertos.PolizaRepository;
import com.digital_insurance_company.poliza.dominio.puertos.PolizaUseCase;

public class PolizaApplicationService implements PolizaUseCase {

    private final PolizaRepository repositorio;
    private final ConsultaCotizacionPort consultaCotizacion;
    private final EmisionPolizaService emisionService;

    public PolizaApplicationService(PolizaRepository repositorio,
            ConsultaCotizacionPort consultaCotizacion,
            PolizaFactory factory) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.consultaCotizacion = Objects.requireNonNull(consultaCotizacion);
        this.emisionService = new EmisionPolizaService(Objects.requireNonNull(factory));
    }

    @Override
    public Poliza emitirPoliza(UUID cotizacionId, LocalDate fechaInicio, LocalDate fechaFin) {
        DatosCotizacionParaEmision datos = consultaCotizacion.buscarParaEmision(cotizacionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la cotización " + cotizacionId));
        Poliza nueva = emisionService.emitir(datos, fechaInicio, fechaFin);
        return repositorio.guardar(nueva);
    }

    @Override
    public Poliza renovarPoliza(UUID polizaId) {
        Poliza poliza = buscarOLanzar(polizaId);
        poliza.renovar();
        return repositorio.guardar(poliza);
    }

    @Override
    public Poliza cancelarPoliza(UUID polizaId) {
        Poliza poliza = buscarOLanzar(polizaId);
        poliza.cancelar();
        return repositorio.guardar(poliza);
    }

    @Override
    public Poliza consultarPoliza(UUID polizaId) {
        return buscarOLanzar(polizaId);
    }

    private Poliza buscarOLanzar(UUID polizaId) {
        return repositorio.buscarPorId(polizaId)
                .orElseThrow(() -> new PolizaNoEncontradaException(polizaId));
    }
}
