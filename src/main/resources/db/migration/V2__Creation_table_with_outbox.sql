CREATE TABLE outbox_event (
    key VARCHAR(255) PRIMARY KEY,
    topic VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    sent BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_outbox_event_sent ON outbox_event(sent, created_at);