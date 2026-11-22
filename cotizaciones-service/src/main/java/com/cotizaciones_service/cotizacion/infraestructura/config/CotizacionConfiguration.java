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
    CotizacionFactory cotizacionFactory() {
        return new CotizacionFactory();
    }

    @Bean
    EvaluacionRiesgoService evaluacionRiesgoService() {
        return new EvaluacionRiesgoService();
    }

    @Bean
    CotizacionUseCase cotizacionUseCase(
            CotizacionFactory cotizacionFactory,
            EvaluacionRiesgoService evaluacionRiesgoService,
            RepositorioCotizaciones repositorioCotizaciones,
            ConsultaClientePort consultaCliente) {
        return new CotizacionApplicationService(
                cotizacionFactory,
                evaluacionRiesgoService,
                repositorioCotizaciones,
                consultaCliente);
    }
}
