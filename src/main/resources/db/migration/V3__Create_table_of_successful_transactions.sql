CREATE TABLE successful_transactions(
    transaction_number VARCHAR(255) PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL,
    amount NUMERIC(19,4),
    owner VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP DEFAULT NOW()
)
