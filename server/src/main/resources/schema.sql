CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(250) NOT NULL CHECK (LENGTH(name) >= 2),
    email VARCHAR(254) NOT NULL UNIQUE CHECK (LENGTH(email) >= 6)
                                       CHECK (LENGTH(SPLIT_PART(email, '@', 1)) <= 64)
                                       CHECK (LENGTH(SPLIT_PART(email, '@', 2)) <= 63)
);

CREATE TABLE IF NOT EXISTS compilations (
    id SERIAL PRIMARY KEY,
    pinned BOOLEAN NOT NULL DEFAULT false,
    title VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
    id SERIAL PRIMARY KEY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    id SERIAL PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL CHECK (LENGTH(annotation) >= 20),
    category_id BIGINT,
    confirmed_requests BIGINT DEFAULT 0,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(7000) NOT NULL CHECK (LENGTH(description) >= 20),
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT false,
    participant_limit BIGINT DEFAULT 0,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN NOT NULL DEFAULT true,
    state VARCHAR(255) NOT NULL,
    title VARCHAR(120) NOT NULL CHECK (LENGTH(title) >= 3),
    views BIGINT DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (initiator_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES locations(id)
);

CREATE TABLE IF NOT EXISTS requests (
    id SERIAL PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);