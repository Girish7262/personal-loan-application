ALTER TABLE users 
ADD COLUMN refresh_token_hash VARCHAR(255) NULL,
ADD COLUMN refresh_token_expiry TIMESTAMP NULL;
