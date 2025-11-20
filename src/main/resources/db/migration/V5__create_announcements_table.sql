CREATE TABLE announcements (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    content JSONB,
    image_url VARCHAR(512),
    image_public_id VARCHAR(255),
    is_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);