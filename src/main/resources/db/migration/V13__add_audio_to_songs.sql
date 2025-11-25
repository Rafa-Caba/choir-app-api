-- Add Audio columns to the songs table
ALTER TABLE songs
ADD COLUMN audio_url VARCHAR(255),
ADD COLUMN audio_public_id VARCHAR(255);