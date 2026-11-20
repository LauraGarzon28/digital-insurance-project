package com.cotizaciones_service.cotizacion.infraestructura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cotizaciones_service.cotizacion.aplicacion.CotizacionApplicationService;
import com.cotizaciones_service.cotizacion.aplicacion.CotizacionUseCase;
import com.cotizaciones_service.cotizacion.aplicacion.RepositorioCotizaciones;
import com.cotizaciones_service.cotizacion.aplicacion.ports.ConsultaClientePort;
import com.cotizaciones_service.cotizacion.dominio.CotizacionFactory;
import com.cotizaciones_service.cotizacion.dominio.EvaluacionRiesgoService;

@Configuration
public class CotizacionConfiguration {

    @Bean
    @SuppressWarnings("unused")
    CotizacionFactory cotizacionFactory() {
        return new CotizacionFactory();
    }

    @Bean
    @SuppressWarnings("unused")
    EvaluacionRiesgoService evaluacionRiesgoService() {
        return new EvaluacionRiesgoService();
    }

    @Bean
    @SuppressWarnings("unused")
    CotizacionUseCase cotizacionUseCase(
            CotizacionFactory cotizacionFactory,
            EvaluacionRiesgoService evaluacionRiesgoService,
            RepositorioCotizaciones repositorioCotizaciones,
            ConsultaClientePort consultaClientePort) {
        return new CotizacionApplicationService(
                cotizacionFactory,
                evaluacionRiesgoService,
                repositorioCotizaciones,
                consultaClientePort);
    }
}
