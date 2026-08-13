CREATE TABLE producessed_transactions(
    transaction_number VARCHAR(255) PRIMARY KEY,
    processed_at TIMESTAMP DEFAULT NOW()
);
--для дедупликации