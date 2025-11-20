-- 1. The Posts Table
CREATE TABLE blog_posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    content JSONB,
    author VARCHAR(255),
    image_url VARCHAR(512),
    image_public_id VARCHAR(255),
    is_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 2. The Likes Table (Many-to-Many)
CREATE TABLE blog_likes (
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, user_id),
    CONSTRAINT fk_likes_post FOREIGN KEY (post_id) REFERENCES blog_posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. The Comments Table (Element Collection)
CREATE TABLE blog_comments (
    post_id BIGINT NOT NULL,
    author VARCHAR(255),
    text JSONB,
    date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES blog_posts(id) ON DELETE CASCADE
);