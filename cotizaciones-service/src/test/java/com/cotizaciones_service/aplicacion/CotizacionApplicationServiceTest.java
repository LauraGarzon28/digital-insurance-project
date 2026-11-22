package com.cotizaciones_service.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.cotizaciones_service.cotizacion.aplicacion.CotizacionApplicationService;
import com.cotizaciones_service.cotizacion.aplicacion.CotizacionUseCase;
import com.cotizaciones_service.cotizacion.aplicacion.RepositorioCotizaciones;
import com.cotizaciones_service.cotizacion.dominio.Cotizacion;
import com.cotizaciones_service.cotizacion.dominio.CotizacionFactory;
import com.cotizaciones_service.cotizacion.dominio.EstadoCotizacion;
import com.cotizaciones_service.cotizacion.dominio.EvaluacionRiesgoService;
import com.cotizaciones_service.cotizacion.dominio.ReglasSuscripcion;
import com.cotizaciones_service.cotizacion.dominio.Riesgo;
import com.cotizaciones_service.cotizacion.infraestructura.salida.persistencia.CotizacionRepositoryInMemoryAdapter;
import com.cotizaciones_service.compartido.ClienteNoEncontradoException;


class CotizacionApplicationServiceTest {

    private final CotizacionUseCase casoDeUso = new CotizacionApplicationService(
            new CotizacionFactory(), new EvaluacionRiesgoService(),
            new RepositorioCotizaciones() {
                private Cotizacion cotizacion;

                @Override
                public Cotizacion guardar(Cotizacion cotizacion) {
                    this.cotizacion = cotizacion;
                    return cotizacion;
                }

                @Override
                public Optional<Cotizacion> buscarPorId(UUID id) {
                    return Optional.ofNullable(cotizacion);
                }

                @Override
                public java.util.List<Cotizacion> listar(UUID clienteId, String estado) {
                    if (cotizacion == null) return java.util.List.of();
                    if (clienteId != null && !clienteId.equals(cotizacion.clienteId())) return java.util.List.of();
                    if (estado != null && !estado.isBlank() && !estado.equals(cotizacion.estado().name())) return java.util.List.of();
                    return java.util.List.of(cotizacion);
                }
            }, id -> true);
    private final ReglasSuscripcion reglas = new ReglasSuscripcion(
            "v1", new BigDecimal("0.02"), 70);

    @Test
    void coordinaSolicitudEvaluacionYAceptacion() {
        Cotizacion cotizacion = casoDeUso.solicitar(
                UUID.randomUUID(),
                new Riesgo("Vivienda", new BigDecimal("100000000"), 30));

        casoDeUso.evaluar(cotizacion.id(), reglas);
        casoDeUso.aceptar(cotizacion.id());

        assertEquals(EstadoCotizacion.ACEPTADA, cotizacion.estado());
    }

    @Test
    void noSolicitaCotizacionSiElClienteNoExiste() {
        CotizacionUseCase casoDeUso = new CotizacionApplicationService(
                new CotizacionFactory(), new EvaluacionRiesgoService(),
                new CotizacionRepositoryInMemoryAdapter(),
                id -> false);

        assertThrows(ClienteNoEncontradoException.class, () -> casoDeUso.solicitar(
                UUID.randomUUID(),
                new Riesgo("Vivienda", new BigDecimal("1000000"), 10)));
    }
}
