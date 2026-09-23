package com.digital_insurance_company.poliza;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public record VigenciaPoliza(LocalDate fechaInicio, LocalDate fechaFin) {

    public VigenciaPoliza {
        Objects.requireNonNull(fechaInicio, "Debe existir una fecha de inicio");
        Objects.requireNonNull(fechaFin, "Debe existir una fecha de fin");
        if (!fechaInicio.isBefore(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio (%s) debe ser anterior a la fecha de fin (%s)".formatted(fechaInicio, fechaFin));
        }
    }

    public boolean estaVigente(LocalDate fecha) {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public boolean haVencido(LocalDate hoy){
        return hoy.isAfter(fechaFin);
    }

    public VigenciaPoliza renovar(){
        Long duracionEnDias = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        LocalDate nuevoInicio = fechaFin.plusDays(1);
        return new VigenciaPoliza(nuevoInicio, nuevoInicio.plusDays(duracionEnDias));
    }
}
