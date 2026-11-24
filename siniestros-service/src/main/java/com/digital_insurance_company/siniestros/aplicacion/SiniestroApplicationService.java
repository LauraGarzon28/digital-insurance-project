package com.digital_insurance_company.siniestros.aplicacion;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.util.List;

import com.digital_insurance_company.siniestros.dominio.EstadoPolizaReferencia;
import com.digital_insurance_company.siniestros.dominio.EstadoSiniestro;
import com.digital_insurance_company.siniestros.dominio.EvaluacionSiniestroService;
import com.digital_insurance_company.siniestros.dominio.Siniestro;
import com.digital_insurance_company.siniestros.dominio.SiniestroFactory;
import com.digital_insurance_company.siniestros.dominio.SiniestroNoEncontradoException;
import com.digital_insurance_company.siniestros.dominio.PolizaNoEncontradaException;
import com.digital_insurance_company.siniestros.dominio.puertos.ConsultaPolizaPort;
import com.digital_insurance_company.siniestros.dominio.puertos.PublicadorEventosSiniestro;
import com.digital_insurance_company.siniestros.dominio.puertos.RepositorioSiniestros;
import com.digital_insurance_company.siniestros.dominio.puertos.SiniestroUseCase;

public class SiniestroApplicationService implements SiniestroUseCase {

    private final SiniestroFactory siniestroFactory;
    private final EvaluacionSiniestroService evaluacionSiniestroService;
    private final RepositorioSiniestros repositorio;
    private final PublicadorEventosSiniestro publicador;
    private final ConsultaPolizaPort consultaPoliza;

    public SiniestroApplicationService(SiniestroFactory siniestroFactory,
            EvaluacionSiniestroService evaluacionSiniestroService,
            RepositorioSiniestros repositorio,
            PublicadorEventosSiniestro publicador,
            ConsultaPolizaPort consultaPoliza) {
        this.siniestroFactory = Objects.requireNonNull(siniestroFactory);
        this.evaluacionSiniestroService = Objects.requireNonNull(evaluacionSiniestroService);
        this.repositorio = Objects.requireNonNull(repositorio);
        this.publicador = Objects.requireNonNull(publicador);
        this.consultaPoliza = Objects.requireNonNull(consultaPoliza);
    }

    @Override
    public Siniestro reportarSiniestro(UUID polizaId, String descripcion) {
        var poliza = consultaPoliza.buscarPorId(polizaId)
                .orElseThrow(() -> new PolizaNoEncontradaException(polizaId));
        Siniestro siniestro = repositorio.guardar(
                siniestroFactory.reportarSiniestro(polizaId, descripcion, poliza.valorAsegurado()));
        publicador.siniestroReportado(siniestro);
        return siniestro;
    }

    @Override
    public Siniestro evaluarSiniestro(UUID siniestroId, boolean esFraude, BigDecimal montoPropuesto) {
        Siniestro siniestro = buscarOLanzar(siniestroId);
        var poliza = consultaPoliza.buscarPorId(siniestro.getPolizaId())
                .orElseThrow(() -> new PolizaNoEncontradaException(siniestro.getPolizaId()));
        evaluacionSiniestroService.evaluar(siniestro, poliza.estado(), esFraude, montoPropuesto);
        Siniestro guardado = repositorio.guardar(siniestro);
        if (guardado.getEstado() == EstadoSiniestro.APROBADO
                || guardado.getEstado() == EstadoSiniestro.RECHAZADO) {
            publicador.siniestroResuelto(guardado);
        }
        return guardado;
    }

    @Override
    public Siniestro asignarAjustador(UUID siniestroId, UUID ajustadorId) {
        Siniestro siniestro = buscarOLanzar(siniestroId);
        siniestro.asignarAjustador(ajustadorId);
        Siniestro guardado = repositorio.guardar(siniestro);
        publicador.siniestroAsignado(guardado);
        return guardado;
    }

    @Override
    public Siniestro consultarSiniestro(UUID siniestroId) {
        return buscarOLanzar(siniestroId);
    }

    @Override
    public List<Siniestro> listarSiniestros(UUID ajustadorId, UUID polizaId, EstadoSiniestro estado) {
        return repositorio.listarTodos().stream()
                .filter(s -> ajustadorId == null || ajustadorId.equals(s.getAjustadorId()))
                .filter(s -> polizaId == null || polizaId.equals(s.getPolizaId()))
                .filter(s -> estado == null || estado.equals(s.getEstado()))
                .toList();
    }

    private Siniestro buscarOLanzar(UUID siniestroId) {
        return repositorio.buscarPorId(siniestroId)
                .orElseThrow(() -> new SiniestroNoEncontradoException(siniestroId));
    }
}
