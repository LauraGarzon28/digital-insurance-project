CREATE TABLE IF NOT EXISTS siniestros (
    id UUID PRIMARY KEY,
    poliza_id UUID NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    valor_asegurado_poliza NUMERIC(19, 2) NOT NULL,
    monto_aprobado NUMERIC(19, 2)
);

CREATE INDEX IF NOT EXISTS idx_siniestros_poliza ON siniestros (poliza_id);
