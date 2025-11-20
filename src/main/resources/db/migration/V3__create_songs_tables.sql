CREATE TABLE song_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    sort_order INTEGER DEFAULT 0
);

CREATE TABLE songs (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content JSONB,  -- This creates the special JSON column
    type VARCHAR(100),
    composer VARCHAR(255),
    song_type_id BIGINT REFERENCES song_types(id)
);

-- Initial Data
INSERT INTO song_types (name, sort_order) VALUES
('Entrada', 1), ('Kyrie', 2), ('Gloria', 3), ('Salmo', 4), ('Aleluya', 5),
('Presentación de Dones', 6), ('Santo', 7), ('Cordero', 8), ('Comunión', 9), ('Salida', 10);