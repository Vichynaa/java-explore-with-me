CREATE TABLE IF NOT EXISTS hits (
    id SERIAL PRIMARY KEY,
    app VARCHAR(255) NOT NULL,
    uri VARCHAR(255) NOT NULL,
    ip VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);