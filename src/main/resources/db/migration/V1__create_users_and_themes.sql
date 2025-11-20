-- 1. Create Themes Table
CREATE TABLE themes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    is_dark BOOLEAN DEFAULT FALSE,
    primary_color VARCHAR(20),
    accent_color VARCHAR(20),
    background_color VARCHAR(20),
    text_color VARCHAR(20),
    card_color VARCHAR(20),
    button_color VARCHAR(20),
    nav_color VARCHAR(50)
);

-- 2. Create Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',

    -- Choir specific
    instrument VARCHAR(100) DEFAULT 'Voz',
    voice BOOLEAN DEFAULT TRUE,
    bio TEXT,

    -- Media
    image_url VARCHAR(512),
    image_public_id VARCHAR(255),

    -- Theme Relation
    theme_id BIGINT,

    -- Timestamps
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_theme FOREIGN KEY (theme_id) REFERENCES themes(id)
);

-- 3. Seed Default Themes
-- (Using generic placeholders for now, you can update these colors via API later)
INSERT INTO themes (name, is_dark, primary_color, accent_color, background_color, text_color, card_color, button_color, nav_color)
VALUES
('Clásico', false, '#6200ea', '#00b0ff', '#ffffff', '#000000', '#f5f5f5', '#6200ea', '#ffffff'),
('Noche', true, '#b388ff', '#80d8ff', '#121212', '#ffffff', '#1e1e1e', '#b388ff', '#1e1e1e');