package com.digital_insurance_company.cotizacion.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.digital_insurance_company.cotizacion.dominio.Cotizacion;
import com.digital_insurance_company.cotizacion.dominio.CotizacionFactory;
import com.digital_insurance_company.cotizacion.dominio.EvaluacionRiesgoService;
import com.digital_insurance_company.cotizacion.dominio.EstadoCotizacion;
import com.digital_insurance_company.cotizacion.dominio.ReglasSuscripcion;
import com.digital_insurance_company.cotizacion.dominio.Riesgo;
import com.digital_insurance_company.cliente.aplicacion.RepositorioClientes;
import com.digital_insurance_company.cliente.dominio.Cliente;

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
            }, new RepositorioClientes() {
                @Override
                public Cliente guardar(Cliente cliente) {
                    return cliente;
                }

                @Override
                public Optional<Cliente> buscarPorId(UUID id) {
                    return Optional.of(new Cliente(
                            id, "Cliente de prueba", "CC", "123", "test@example.com", "3000000000"));
                }

                @Override
                public boolean existePorNumeroDocumento(String numeroDocumento) {
                    return false;
                }
            });
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
}
