package com.digital_insurance_company.siniestros.dominio.puertos;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

import com.digital_insurance_company.siniestros.dominio.EstadoPolizaReferencia;
import com.digital_insurance_company.siniestros.dominio.EstadoSiniestro;
import com.digital_insurance_company.siniestros.dominio.Siniestro;

public interface SiniestroUseCase {

    Siniestro reportarSiniestro(UUID polizaId, String descripcion, BigDecimal valorAseguradoPoliza);

    Siniestro evaluarSiniestro(UUID siniestroId, EstadoPolizaReferencia estadoPoliza, boolean esFraude,
            BigDecimal montoPropuesto);

    Siniestro asignarAjustador(UUID siniestroId, UUID ajustadorId);

    Siniestro consultarSiniestro(UUID siniestroId);

    List<Siniestro> listarSiniestros(UUID ajustadorId, UUID polizaId, EstadoSiniestro estado);
}
