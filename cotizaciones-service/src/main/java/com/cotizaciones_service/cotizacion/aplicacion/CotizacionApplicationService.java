package com.cotizaciones_service.cotizacion.aplicacion;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import com.cotizaciones_service.compartido.ClienteNoEncontradoException;
import com.cotizaciones_service.compartido.CotizacionNoEncontradaException;
import com.cotizaciones_service.cotizacion.aplicacion.ports.ConsultaClientePort;
import com.cotizaciones_service.cotizacion.dominio.Cotizacion;
import com.cotizaciones_service.cotizacion.dominio.CotizacionFactory;
import com.cotizaciones_service.cotizacion.dominio.EvaluacionRiesgoService;
import com.cotizaciones_service.cotizacion.dominio.ReglasSuscripcion;
import com.cotizaciones_service.cotizacion.dominio.Riesgo;

public class CotizacionApplicationService implements CotizacionUseCase {

    private final CotizacionFactory cotizacionFactory;
    private final EvaluacionRiesgoService evaluacionRiesgoService;
    private final RepositorioCotizaciones repositorioCotizaciones;
    private final ConsultaClientePort consultaCliente;

    public CotizacionApplicationService(CotizacionFactory cotizacionFactory,
            EvaluacionRiesgoService evaluacionRiesgoService,
            RepositorioCotizaciones repositorioCotizaciones,
            ConsultaClientePort consultaCliente) {
        this.cotizacionFactory = Objects.requireNonNull(cotizacionFactory);
        this.evaluacionRiesgoService = Objects.requireNonNull(evaluacionRiesgoService);
        this.repositorioCotizaciones = Objects.requireNonNull(repositorioCotizaciones);
        this.consultaCliente = Objects.requireNonNull(consultaCliente);
    }

    @Override
    public Cotizacion solicitar(UUID clienteId, Riesgo riesgo) {
        if (!consultaCliente.existe(clienteId)) {
            throw new ClienteNoEncontradoException(clienteId);
        }
        return repositorioCotizaciones.guardar(cotizacionFactory.crear(clienteId, riesgo));
    }

    @Override
    public Optional<Cotizacion> buscarPorId(UUID id) {
        return repositorioCotizaciones.buscarPorId(id);
    }

    @Override
    public Cotizacion evaluar(UUID id, ReglasSuscripcion reglas) {
        Cotizacion cotizacion = obtener(id);
        cotizacion.registrarEvaluacion(
                evaluacionRiesgoService.evaluar(cotizacion.riesgo(), reglas));
        return repositorioCotizaciones.guardar(cotizacion);
    }

    @Override
    public Cotizacion aprobarRevisionManual(UUID id) {
        Cotizacion cotizacion = obtener(id);
        cotizacion.aprobarRevisionManual();
        return repositorioCotizaciones.guardar(cotizacion);
    }

    @Override
    public Cotizacion rechazar(UUID id) {
        Cotizacion cotizacion = obtener(id);
        cotizacion.rechazar();
        return repositorioCotizaciones.guardar(cotizacion);
    }

    @Override
    public Cotizacion aceptar(UUID id) {
        Cotizacion cotizacion = obtener(id);
        cotizacion.aceptar();
        return repositorioCotizaciones.guardar(cotizacion);
    }

    @Override
    public List<Cotizacion> listar(UUID clienteId, String estado) {
        return repositorioCotizaciones.listar(clienteId, estado);
    }

    private Cotizacion obtener(UUID id) {
        Objects.requireNonNull(id, "El id de la cotización es obligatorio");
        return repositorioCotizaciones.buscarPorId(id)
                .orElseThrow(() -> new CotizacionNoEncontradaException(id));
    }
}