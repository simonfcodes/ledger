CREATE INDEX idx_transactions_account_pagination
ON transactions (account_id, transaction_date DESC, created_at DESC, id DESC);