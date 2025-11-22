ALTER TABLE chat_messages
ADD COLUMN reply_to_id BIGINT;

ALTER TABLE chat_messages
ADD CONSTRAINT fk_chat_reply
FOREIGN KEY (reply_to_id) REFERENCES chat_messages(id) ON DELETE SET NULL;