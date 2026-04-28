CREATE OR REPLACE FUNCTION transactions_opening_balance_immutability() RETURNS trigger AS $transactions_opening_balance_immutability$
BEGIN
    -- check that old record is not opening balance
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

CREATE TRIGGER transactions_opening_balance_immutability BEFORE UPDATE OR DELETE ON transactions
    FOR EACH ROW EXECUTE FUNCTION transactions_opening_balance_immutability();