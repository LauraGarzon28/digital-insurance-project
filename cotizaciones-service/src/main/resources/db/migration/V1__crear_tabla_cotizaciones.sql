CREATE TABLE cotizaciones (
    id                   UUID PRIMARY KEY,
    cliente_id           UUID           NOT NULL,
    descripcion_riesgo   VARCHAR(255)   NOT NULL,
    valor_asegurado      NUMERIC(18, 2) NOT NULL,
    puntaje_riesgo       INTEGER        NOT NULL CHECK (puntaje_riesgo BETWEEN 0 AND 100),
    prima_monto          NUMERIC(18, 2),
    prima_version_reglas VARCHAR(50),
    estado               VARCHAR(30)    NOT NULL,
    creada_en            TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX idx_cotizaciones_cliente ON cotizaciones (cliente_id);