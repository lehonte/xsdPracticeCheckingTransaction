ALTER TABLE successful_transactions DROP COLUMN phone_number;

ALTER TABLE successful_transactions ADD COLUMN email VARCHAR(255) DEFAULT 0;

UPDATE successful_transactions
SET email='${admin_email}'
WHERE owner='admin';

COMMIT;