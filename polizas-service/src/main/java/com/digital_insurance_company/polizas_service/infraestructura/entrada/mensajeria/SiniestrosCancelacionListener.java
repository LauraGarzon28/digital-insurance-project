package com.digital_insurance_company.polizas_service.infraestructura.entrada.mensajeria;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.digital_insurance_company.polizas_service.infraestructura.config.RabbitMQConfiguration;
import com.digital_insurance_company.polizas_service.dominio.PolizaNoEncontradaException;
import com.digital_insurance_company.polizas_service.dominio.puertos.PolizaUseCase;

@Component
public class SiniestrosCancelacionListener {

    private static final Logger log = LoggerFactory.getLogger(SiniestrosCancelacionListener.class);

    private final PolizaUseCase polizaUseCase;

    public SiniestrosCancelacionListener(PolizaUseCase polizaUseCase) {
        this.polizaUseCase = polizaUseCase;
    }

    @RabbitListener(queues = RabbitMQConfiguration.COLA_CANCELACION_VERIFICADA)
    public void alVerificarseCancelacion(String mensaje) {
        try {
            String polizaIdTexto = extraerCampo(mensaje, "polizaId");
            String aprobadaTexto = extraerCampo(mensaje, "aprobada");

            if (polizaIdTexto == null || aprobadaTexto == null) {
                throw new IllegalArgumentException("El mensaje no contiene polizaId o aprobada");
            }

            UUID polizaId = UUID.fromString(polizaIdTexto);
            boolean aprobada = Boolean.parseBoolean(aprobadaTexto);

            if (aprobada) {
                log.info("Saga: Siniestros aprobó la cancelación de {}. Cancelando póliza.", polizaId);
                polizaUseCase.confirmarCancelacion(polizaId);
            } else {
                String motivo = extraerCampo(mensaje, "motivo");
                if (motivo == null) {
                    motivo = "sin motivo";
                }
                log.warn("Saga: Siniestros rechazó la cancelación de {} ({}). Compensando.", polizaId, motivo);
                polizaUseCase.revertirCancelacion(polizaId, motivo);
            }
        } catch (PolizaNoEncontradaException e) {
            // Un mensaje para una póliza que no existe no debe reintentarse eternamente.
            log.warn("Saga: se ignora el mensaje, {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // Mensaje malformado: se registra y no se reencola infinitamente.
            log.error("Saga: error al deserializar el mensaje de verificación de cancelación: {}", mensaje, e);
        }
    }

    private String extraerCampo(String mensaje, String campo) {
        Pattern patron = Pattern.compile("\\\"" + Pattern.quote(campo) + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"|\\\"" + Pattern.quote(campo) + "\\\"\\s*:\\s*(true|false)");
        Matcher coincidencia = patron.matcher(mensaje);
        if (!coincidencia.find()) {
            return null;
        }
        return coincidencia.group(1) != null ? coincidencia.group(1) : coincidencia.group(2);
    }
}