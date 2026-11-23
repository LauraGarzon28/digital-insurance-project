package com.digital_insurance_company.polizas_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PolizasServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolizasServiceApplication.class, args);
	}

}