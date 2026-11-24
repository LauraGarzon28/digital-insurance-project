CREATE TABLE outbox_event (
    id          BIGSERIAL PRIMARY KEY,
    topico      VARCHAR(255) NOT NULL,
    clave       VARCHAR(255) NOT NULL,
    payload     VARCHAR(4000) NOT NULL,
    creado_en   TIMESTAMP WITH TIME ZONE NOT NULL,
    publicado   BOOLEAN NOT NULL DEFAULT FALSE
);
