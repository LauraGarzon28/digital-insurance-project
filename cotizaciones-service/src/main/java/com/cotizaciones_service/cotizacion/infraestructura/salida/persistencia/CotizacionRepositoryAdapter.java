package com.cotizaciones_service.cotizacion.infraestructura.salida.persistencia;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.cotizaciones_service.cotizacion.aplicacion.RepositorioCotizaciones;
import com.cotizaciones_service.cotizacion.dominio.Cotizacion;
import com.cotizaciones_service.cotizacion.dominio.EstadoCotizacion;
import com.cotizaciones_service.cotizacion.dominio.Prima;
import com.cotizaciones_service.cotizacion.dominio.Riesgo;

@Component
public class CotizacionRepositoryAdapter implements RepositorioCotizaciones {

    private final CotizacionJpaRepository jpa;

    public CotizacionRepositoryAdapter(CotizacionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Cotizacion guardar(Cotizacion cotizacion) {
        jpa.save(aEntidad(cotizacion));
        return cotizacion;
    }

    @Override
    public Optional<Cotizacion> buscarPorId(UUID id) {
        return jpa.findById(id).map(this::aDominio);
    }

    private CotizacionJpaEntity aEntidad(Cotizacion c) {
        Prima prima = c.prima();
        return new CotizacionJpaEntity(
                c.id(),
                c.clienteId(),
                c.riesgo().descripcion(),
                c.riesgo().valorAsegurado(),
                c.riesgo().puntaje(),
                prima == null ? null : prima.monto(),
                prima == null ? null : prima.versionReglas(),
                c.estado().name());
    }

    private Cotizacion aDominio(CotizacionJpaEntity e) {
        Riesgo riesgo = new Riesgo(
                e.getDescripcionRiesgo(), e.getValorAsegurado(), e.getPuntajeRiesgo());
        Prima prima = e.getPrimaMonto() == null
                ? null
                : new Prima(e.getPrimaMonto(), e.getPrimaVersionReglas());
        return Cotizacion.reconstituir(
                e.getId(), e.getClienteId(), riesgo, prima,
                EstadoCotizacion.valueOf(e.getEstado()));
    }
    
}