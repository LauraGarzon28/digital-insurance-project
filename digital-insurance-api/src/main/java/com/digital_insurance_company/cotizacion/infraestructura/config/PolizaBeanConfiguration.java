package com.digital_insurance_company.cotizacion.infraestructura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.digital_insurance_company.poliza.aplicacion.PolizaApplicationService;
import com.digital_insurance_company.poliza.dominio.PolizaFactory;
import com.digital_insurance_company.poliza.dominio.puertos.ConsultaCotizacionPort;
import com.digital_insurance_company.poliza.dominio.puertos.PolizaRepository;
import com.digital_insurance_company.poliza.dominio.puertos.PolizaUseCase;

@Configuration
public class PolizaBeanConfiguration {

    @Bean
    public PolizaFactory polizaFactory() {
        return new PolizaFactory();
    }

    @Bean
    public PolizaUseCase polizaUseCase(PolizaRepository repositorio,
            ConsultaCotizacionPort consultaCotizacion,
            PolizaFactory factory) {
        return new PolizaApplicationService(repositorio, consultaCotizacion, factory);
    }

}
