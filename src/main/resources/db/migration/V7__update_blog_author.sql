-- 1. Remove the old string column
ALTER TABLE blog_posts DROP COLUMN author;

-- 2. Add the new Foreign Key column
ALTER TABLE blog_posts ADD COLUMN author_id BIGINT;

-- 3. Add the constraint
ALTER TABLE blog_posts
ADD CONSTRAINT fk_blog_author
FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL;