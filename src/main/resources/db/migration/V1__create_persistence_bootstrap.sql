CREATE TABLE persistence_bootstrap (
    id SMALLINT PRIMARY KEY,
    initialized_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT persistence_bootstrap_singleton CHECK (id = 1)
);

INSERT INTO persistence_bootstrap (id) VALUES (1);
