CREATE TABLE gallery_images (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    image_url VARCHAR(512) NOT NULL,
    image_public_id VARCHAR(255),

    -- Flags
    image_start BOOLEAN DEFAULT FALSE,
    image_top_bar BOOLEAN DEFAULT FALSE,
    image_us BOOLEAN DEFAULT FALSE,
    image_logo BOOLEAN DEFAULT FALSE,
    image_gallery BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);