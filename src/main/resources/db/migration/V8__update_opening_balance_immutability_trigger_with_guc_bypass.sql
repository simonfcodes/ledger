CREATE OR REPLACE FUNCTION transactions_opening_balance_immutability() RETURNS trigger AS $transactions_opening_balance_immutability$
BEGIN
    -- If bypass variable is set, allow delete function to bypass immutability check.
    -- This is needed to allow cascade delete of transactions when an account is deleted, while still
    -- preventing users from manually deleting or updating opening balance transactions.
    IF TG_OP = 'DELETE' AND current_setting('app.allow_opening_balance_cascade_delete', true) = 'true' THEN
        RETURN OLD;
    END IF;
    -- If not bypassed for delete check that old record is not opening balance
    IF OLD.source = 'OPENING_BALANCE' THEN
        RAISE EXCEPTION 'cannot % opening balance transaction', lower(TG_OP);
    END IF;
    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$transactions_opening_balance_immutability$ LANGUAGE plpgsql;