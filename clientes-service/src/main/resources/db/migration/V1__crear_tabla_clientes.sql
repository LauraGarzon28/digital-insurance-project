CREATE TABLE clientes (
    id               UUID PRIMARY KEY,
    nombre_completo  VARCHAR(255) NOT NULL,
    tipo_documento   VARCHAR(10)  NOT NULL,
    numero_documento VARCHAR(30)  NOT NULL UNIQUE,
    correo           VARCHAR(255) NOT NULL,
    telefono         VARCHAR(20)  NOT NULL,
    creado_en        TIMESTAMPTZ  NOT NULL DEFAULT now()
);