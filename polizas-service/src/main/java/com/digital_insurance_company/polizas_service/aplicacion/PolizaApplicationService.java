package com.digital_insurance_company.polizas_service.aplicacion;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.UUID;

import com.digital_insurance_company.polizas_service.dominio.CotizacionNoEncontradaException;
import com.digital_insurance_company.polizas_service.dominio.DatosCotizacionParaEmision;
import com.digital_insurance_company.polizas_service.dominio.EmisionPolizaService;
import com.digital_insurance_company.polizas_service.dominio.Poliza;
import com.digital_insurance_company.polizas_service.dominio.PolizaFactory;
import com.digital_insurance_company.polizas_service.dominio.PolizaNoEncontradaException;
import com.digital_insurance_company.polizas_service.dominio.puertos.ConsultaCotizacionPort;
import com.digital_insurance_company.polizas_service.dominio.puertos.PolizaRepository;
import com.digital_insurance_company.polizas_service.dominio.puertos.PolizaUseCase;
import com.digital_insurance_company.polizas_service.dominio.puertos.PublicadorEventos;

public class PolizaApplicationService implements PolizaUseCase {

    static final String TOPICO_EMITIDAS = "polizas.emitidas";
    static final String TOPICO_RENOVADAS = "polizas.renovadas";
    static final String TOPICO_CANCELACION_SOLICITADA = "polizas.cancelacion-solicitada";
    static final String TOPICO_CANCELADAS = "polizas.canceladas";
    static final String TOPICO_CANCELACION_RECHAZADA = "polizas.cancelacion-rechazada";

    private final PolizaRepository repositorio;
    private final ConsultaCotizacionPort consultaCotizacion;
    private final PublicadorEventos publicador;
    private final EmisionPolizaService emisionService;

    public PolizaApplicationService(PolizaRepository repositorio,
                                     ConsultaCotizacionPort consultaCotizacion,
                                     PublicadorEventos publicador,
                                     PolizaFactory factory) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.consultaCotizacion = Objects.requireNonNull(consultaCotizacion);
        this.publicador = Objects.requireNonNull(publicador);
        this.emisionService = new EmisionPolizaService(Objects.requireNonNull(factory));
    }

    @Override
    public Poliza emitirPoliza(UUID cotizacionId, LocalDate fechaInicio, LocalDate fechaFin) {
        DatosCotizacionParaEmision datos = consultaCotizacion.buscarParaEmision(cotizacionId)
                .orElseThrow(() -> new CotizacionNoEncontradaException(cotizacionId));
        Poliza guardada = repositorio.guardar(emisionService.emitir(datos, fechaInicio, fechaFin));

        publicador.publicar(TOPICO_EMITIDAS, guardada.id().toString(), Map.of(
                "polizaId", guardada.id().toString(),
                "clienteId", guardada.clienteId().toString(),
                "cotizacionId", guardada.cotizacionId().toString(),
                "valorAsegurado", guardada.valorAsegurado().monto().toPlainString(),
                "fechaInicio", guardada.vigencia().fechaInicio().toString(),
                "fechaFin", guardada.vigencia().fechaFin().toString()));
        return guardada;
    }

    @Override
    public Poliza renovarPoliza(UUID polizaId) {
        Poliza poliza = buscarOLanzar(polizaId);
        poliza.renovar();
        Poliza guardada = repositorio.guardar(poliza);

        publicador.publicar(TOPICO_RENOVADAS, guardada.id().toString(), Map.of(
                "polizaId", guardada.id().toString(),
                "clienteId", guardada.clienteId().toString(),
                "fechaInicio", guardada.vigencia().fechaInicio().toString(),
                "fechaFin", guardada.vigencia().fechaFin().toString()));
        return guardada;
    }

    @Override
    public Poliza consultarPoliza(UUID polizaId) {
        return buscarOLanzar(polizaId);
    }

    @Override
    public List<Poliza> listarPorCliente(UUID clienteId) {
        Objects.requireNonNull(clienteId, "clienteId es obligatorio");
        return repositorio.buscarPorClienteId(clienteId);
    }

    @Override
    public Poliza solicitarCancelacion(UUID polizaId) {
        Poliza poliza = buscarOLanzar(polizaId);
        if (poliza.enCancelacion()) {
            return poliza; // idempotente: la Saga ya está en curso
        }
        poliza.iniciarCancelacion();
        Poliza guardada = repositorio.guardar(poliza);

        publicador.publicar(TOPICO_CANCELACION_SOLICITADA, guardada.id().toString(), Map.of(
                "polizaId", guardada.id().toString(),
                "clienteId", guardada.clienteId().toString()));
        return guardada;
    }

    @Override
    public Poliza confirmarCancelacion(UUID polizaId) {
        Poliza poliza = buscarOLanzar(polizaId);
        if (!poliza.enCancelacion()) {
            return poliza; // idempotente: ya se confirmó, o nunca se solicitó
        }
        poliza.confirmarCancelacion();
        Poliza guardada = repositorio.guardar(poliza);

        publicador.publicar(TOPICO_CANCELADAS, guardada.id().toString(), Map.of(
                "polizaId", guardada.id().toString(),
                "clienteId", guardada.clienteId().toString()));
        return guardada;
    }

    @Override
    public Poliza revertirCancelacion(UUID polizaId, String motivo) {
        Poliza poliza = buscarOLanzar(polizaId);
        if (!poliza.enCancelacion()) {
            return poliza; // idempotente
        }
        poliza.revertirCancelacion();
        Poliza guardada = repositorio.guardar(poliza);

        publicador.publicar(TOPICO_CANCELACION_RECHAZADA, guardada.id().toString(), Map.of(
                "polizaId", guardada.id().toString(),
                "motivo", motivo == null ? "sin motivo" : motivo));
        return guardada;
    }

    private Poliza buscarOLanzar(UUID polizaId) {
        return repositorio.buscarPorId(polizaId)
                .orElseThrow(() -> new PolizaNoEncontradaException(polizaId));
    }
}