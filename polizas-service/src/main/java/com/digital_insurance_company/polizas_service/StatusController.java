package com.digital_insurance_company.polizas_service;

import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/status")
    public StatusResponse status() {
        return new StatusResponse("polizas-service", "UP", Instant.now());
    }

    public record StatusResponse(String servicio, String estado, Instant momento) {

    }
}
