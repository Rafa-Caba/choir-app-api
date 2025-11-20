-- 1. Messages Table
CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT REFERENCES users(id),
    content JSONB,
    type VARCHAR(20) NOT NULL DEFAULT 'TEXT',

    -- Files/Media
    file_url VARCHAR(512),
    filename VARCHAR(255),
    image_url VARCHAR(512),
    image_public_id VARCHAR(255),

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 2. Reactions Table
CREATE TABLE chat_reactions (
    message_id BIGINT NOT NULL REFERENCES chat_messages(id) ON DELETE CASCADE,
    emoji VARCHAR(10),
    username VARCHAR(255)
);