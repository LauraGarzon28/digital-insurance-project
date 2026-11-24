package com.digital_insurance_company.polizas_service.dominio.puertos;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.digital_insurance_company.polizas_service.dominio.Poliza;

public interface PolizaUseCase {

    Poliza emitirPoliza(UUID cotizacionId, LocalDate fechaInicio, LocalDate fechaFin);

    Poliza renovarPoliza(UUID polizaId);

    Poliza consultarPoliza(UUID polizaId);

    List<Poliza> listarPorCliente(UUID clienteId);

    Poliza solicitarCancelacion(UUID polizaId);

    Poliza confirmarCancelacion(UUID polizaId);

    Poliza revertirCancelacion(UUID polizaId, String motivo);
}
