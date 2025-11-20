CREATE TABLE settings (
    id BIGINT PRIMARY KEY,
    app_title VARCHAR(255) NOT NULL,

    -- Social Links (Flattened)
    facebook VARCHAR(255),
    instagram VARCHAR(255),
    youtube VARCHAR(255),
    whatsapp VARCHAR(255),

    -- Rich Text
    about_app JSONB,

    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- CRITICAL: Insert the singleton row (ID 1)
INSERT INTO settings (id, app_title, updated_at)
VALUES (1, 'Choir App', CURRENT_TIMESTAMP);