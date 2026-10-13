package com.digital_insurance_company.cotizacion.aplicacion;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.digital_insurance_company.compartido.CotizacionNoEncontradaException;
import com.digital_insurance_company.cliente.aplicacion.ClienteNoEncontradoException;
import com.digital_insurance_company.cliente.aplicacion.RepositorioClientes;
import com.digital_insurance_company.cotizacion.dominio.Cotizacion;
import com.digital_insurance_company.cotizacion.dominio.CotizacionFactory;
import com.digital_insurance_company.cotizacion.dominio.EvaluacionRiesgoService;
import com.digital_insurance_company.cotizacion.dominio.ReglasSuscripcion;
import com.digital_insurance_company.cotizacion.dominio.Riesgo;

public class CotizacionApplicationService implements CotizacionUseCase {

    private final CotizacionFactory cotizacionFactory;
    private final EvaluacionRiesgoService evaluacionRiesgoService;
    private final RepositorioCotizaciones repositorioCotizaciones;
    private final RepositorioClientes repositorioClientes;

    public CotizacionApplicationService(CotizacionFactory cotizacionFactory,
            EvaluacionRiesgoService evaluacionRiesgoService,
            RepositorioCotizaciones repositorioCotizaciones,
            RepositorioClientes repositorioClientes) {
        this.cotizacionFactory = Objects.requireNonNull(cotizacionFactory);
        this.evaluacionRiesgoService = Objects.requireNonNull(evaluacionRiesgoService);
        this.repositorioCotizaciones = Objects.requireNonNull(repositorioCotizaciones);
        this.repositorioClientes = Objects.requireNonNull(repositorioClientes);
    }

    @Override
    public Cotizacion solicitar(UUID clienteId, Riesgo riesgo) {
        repositorioClientes.buscarPorId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException(clienteId));
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

    private Cotizacion obtener(UUID id) {
        Objects.requireNonNull(id, "El id de la cotización es obligatorio");
        return repositorioCotizaciones.buscarPorId(id)
                .orElseThrow(() -> new CotizacionNoEncontradaException(id));
    }
}
