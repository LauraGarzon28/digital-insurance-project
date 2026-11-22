CREATE TABLE IF NOT EXISTS decisiones_siniestro (
    id BIGSERIAL PRIMARY KEY,
    mensaje_id UUID NOT NULL UNIQUE,
    siniestro_id UUID NOT NULL,
    poliza_id UUID NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    monto_aprobado NUMERIC(19, 2),
    ocurrido_en TIMESTAMP WITH TIME ZONE NOT NULL,
    registrado_en TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_decisiones_siniestro ON decisiones_siniestro (siniestro_id);

CREATE TABLE IF NOT EXISTS mensajes_procesados (
    mensaje_id UUID PRIMARY KEY,
    procesado_en TIMESTAMP WITH TIME ZONE NOT NULL
);
