package com.digital_insurance_company.polizas_service.infraestructura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.digital_insurance_company.polizas_service.aplicacion.PolizaApplicationService;
import com.digital_insurance_company.polizas_service.dominio.PolizaFactory;
import com.digital_insurance_company.polizas_service.dominio.puertos.ConsultaCotizacionPort;
import com.digital_insurance_company.polizas_service.dominio.puertos.PolizaRepository;
import com.digital_insurance_company.polizas_service.dominio.puertos.PolizaUseCase;
import com.digital_insurance_company.polizas_service.dominio.puertos.PublicadorEventos;

@Configuration
public class PolizaBeanConfiguration {

    @Bean
    public PolizaFactory polizaFactory() {
        return new PolizaFactory();
    }

    @Bean
    public PolizaUseCase polizaUseCase(PolizaRepository repositorio,
            ConsultaCotizacionPort consultaCotizacion,
            PublicadorEventos publicadorEventos,
            PolizaFactory factory) {
        return new PolizaApplicationService(repositorio, consultaCotizacion, publicadorEventos, factory);
    }
}
