CREATE TABLE blocked_owners(
    owner VARCHAR(255) PRIMARY KEY,
    blocked_at TIMESTAMP DEFAULT NOW()
)
