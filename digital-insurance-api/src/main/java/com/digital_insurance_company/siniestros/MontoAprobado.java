package com.digital_insurance_company.siniestros;

public record MontoAprobado(double valor, double valorAseguradoPoliza) {
    public MontoAprobado {
        if (valor < 0) {
            throw new IllegalArgumentException("El monto aprobado no puede ser negativo");
        }
        if (valor > valorAseguradoPoliza) {
            throw new IllegalArgumentException("El monto aprobado no puede superar el valor asegurado de la póliza");
        }
    }
}
