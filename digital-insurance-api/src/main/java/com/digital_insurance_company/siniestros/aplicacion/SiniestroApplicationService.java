package com.digital_insurance_company.siniestros.aplicacion;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import com.digital_insurance_company.siniestros.dominio.EstadoPolizaReferencia;
import com.digital_insurance_company.siniestros.dominio.EvaluacionSiniestroService;
import com.digital_insurance_company.siniestros.dominio.Siniestro;
import com.digital_insurance_company.siniestros.dominio.SiniestroFactory;
import com.digital_insurance_company.siniestros.dominio.SiniestroNoEncontradoException;
import com.digital_insurance_company.siniestros.dominio.puertos.RepositorioSiniestros;
import com.digital_insurance_company.siniestros.dominio.puertos.SiniestroUseCase;

public class SiniestroApplicationService implements SiniestroUseCase {

    private final SiniestroFactory siniestroFactory;
    private final EvaluacionSiniestroService evaluacionSiniestroService;
    private final RepositorioSiniestros repositorio;

    public SiniestroApplicationService(SiniestroFactory siniestroFactory,
            EvaluacionSiniestroService evaluacionSiniestroService,
            RepositorioSiniestros repositorio) {
        this.siniestroFactory = Objects.requireNonNull(siniestroFactory);
        this.evaluacionSiniestroService = Objects.requireNonNull(evaluacionSiniestroService);
        this.repositorio = Objects.requireNonNull(repositorio);
    }

    @Override
    public Siniestro reportarSiniestro(UUID polizaId, String descripcion, BigDecimal valorAseguradoPoliza) {
        return repositorio.guardar(
                siniestroFactory.reportarSiniestro(polizaId, descripcion, valorAseguradoPoliza));
    }

    @Override
    public Siniestro evaluarSiniestro(UUID siniestroId, EstadoPolizaReferencia estadoPoliza, boolean esFraude,
            BigDecimal montoPropuesto) {
        Siniestro siniestro = buscarOLanzar(siniestroId);
        evaluacionSiniestroService.evaluar(siniestro, estadoPoliza, esFraude, montoPropuesto);
        return repositorio.guardar(siniestro);
    }

    @Override
    public Siniestro consultarSiniestro(UUID siniestroId) {
        return buscarOLanzar(siniestroId);
    }

    private Siniestro buscarOLanzar(UUID siniestroId) {
        return repositorio.buscarPorId(siniestroId)
                .orElseThrow(() -> new SiniestroNoEncontradoException(siniestroId));
    }
}
