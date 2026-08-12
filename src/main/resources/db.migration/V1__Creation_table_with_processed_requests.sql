CREATE TABLE producessed_requests(
    request_id VARCHAR(36) PRIMARY KEY,
    processed_at TIMESTAMP DEFAULT NOW()
);