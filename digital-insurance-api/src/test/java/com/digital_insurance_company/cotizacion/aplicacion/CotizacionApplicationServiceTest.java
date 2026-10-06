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
            });
    private final ReglasSuscripcion reglas = new ReglasSuscripcion(
            "v1", new BigDecimal("0.02"), 70);

    @Test
    void coordinaSolicitudEvaluacionYAceptacion() {
        Cotizacion cotizacion = casoDeUso.solicitar(
                UUID.randomUUID(),
                new Riesgo("Vivienda", new BigDecimal("100000000"), 30));

        casoDeUso.evaluar(cotizacion, reglas);
        casoDeUso.aceptar(cotizacion);

        assertEquals(EstadoCotizacion.ACEPTADA, cotizacion.estado());
    }
}
