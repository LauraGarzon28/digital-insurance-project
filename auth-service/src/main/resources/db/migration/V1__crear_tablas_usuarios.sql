CREATE TABLE usuarios (
    id            UUID PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    correo        VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    cliente_id    UUID,             
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE usuario_roles (
    usuario_id UUID        NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    rol        VARCHAR(20) NOT NULL,
    PRIMARY KEY (usuario_id, rol)
);