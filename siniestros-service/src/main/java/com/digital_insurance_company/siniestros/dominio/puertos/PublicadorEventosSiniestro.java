package com.digital_insurance_company.siniestros.dominio.puertos;

import com.digital_insurance_company.siniestros.dominio.Siniestro;

/**
 * Puerto secundario de salida para publicar los hechos del contexto de Siniestros.
 * El dominio no conoce el bróker: la implementación vive en infraestructura.
 */
public interface PublicadorEventosSiniestro {

    void siniestroReportado(Siniestro siniestro);

    void siniestroAsignado(Siniestro siniestro);

    void siniestroResuelto(Siniestro siniestro);
}
