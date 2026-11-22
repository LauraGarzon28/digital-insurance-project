package com.digital_insurance_company.siniestros.aplicacion;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import com.digital_insurance_company.siniestros.dominio.EstadoPolizaReferencia;
import com.digital_insurance_company.siniestros.dominio.EstadoSiniestro;
import com.digital_insurance_company.siniestros.dominio.EvaluacionSiniestroService;
import com.digital_insurance_company.siniestros.dominio.Siniestro;
import com.digital_insurance_company.siniestros.dominio.SiniestroFactory;
import com.digital_insurance_company.siniestros.dominio.SiniestroNoEncontradoException;
import com.digital_insurance_company.siniestros.dominio.puertos.PublicadorEventosSiniestro;
import com.digital_insurance_company.siniestros.dominio.puertos.RepositorioSiniestros;
import com.digital_insurance_company.siniestros.dominio.puertos.SiniestroUseCase;

public class SiniestroApplicationService implements SiniestroUseCase {

    private final SiniestroFactory siniestroFactory;
    private final EvaluacionSiniestroService evaluacionSiniestroService;
    private final RepositorioSiniestros repositorio;
    private final PublicadorEventosSiniestro publicador;

    public SiniestroApplicationService(SiniestroFactory siniestroFactory,
            EvaluacionSiniestroService evaluacionSiniestroService,
            RepositorioSiniestros repositorio,
            PublicadorEventosSiniestro publicador) {
        this.siniestroFactory = Objects.requireNonNull(siniestroFactory);
        this.evaluacionSiniestroService = Objects.requireNonNull(evaluacionSiniestroService);
        this.repositorio = Objects.requireNonNull(repositorio);
        this.publicador = Objects.requireNonNull(publicador);
    }

    @Override
    public Siniestro reportarSiniestro(UUID polizaId, String descripcion, BigDecimal valorAseguradoPoliza) {
        Siniestro siniestro = repositorio.guardar(
                siniestroFactory.reportarSiniestro(polizaId, descripcion, valorAseguradoPoliza));
        publicador.siniestroReportado(siniestro);
        return siniestro;
    }

    @Override
    public Siniestro evaluarSiniestro(UUID siniestroId, EstadoPolizaReferencia estadoPoliza, boolean esFraude,
            BigDecimal montoPropuesto) {
        Siniestro siniestro = buscarOLanzar(siniestroId);
        evaluacionSiniestroService.evaluar(siniestro, estadoPoliza, esFraude, montoPropuesto);
        Siniestro guardado = repositorio.guardar(siniestro);
        if (guardado.getEstado() == EstadoSiniestro.APROBADO
                || guardado.getEstado() == EstadoSiniestro.RECHAZADO) {
            publicador.siniestroResuelto(guardado);
        }
        return guardado;
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
