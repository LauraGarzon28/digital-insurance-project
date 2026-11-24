CREATE TABLE polizas (
    id              UUID PRIMARY KEY,
    cliente_id      UUID NOT NULL,
    cotizacion_id   UUID NOT NULL,
    valor_asegurado NUMERIC(19, 2) NOT NULL,
    fecha_inicio    DATE NOT NULL,
    fecha_fin       DATE NOT NULL,
    cancelada       BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_polizas_cotizacion_id ON polizas (cotizacion_id);
CREATE INDEX idx_polizas_cliente_id ON polizas (cliente_id);
