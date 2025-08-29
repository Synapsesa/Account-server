ALTER TABLE eventuate.message
ADD COLUMN IF NOT EXISTS message_partition SMALLINT;
