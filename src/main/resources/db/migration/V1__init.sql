CREATE TABLE currencies (
    code CHAR(3) PRIMARY KEY,
    name VARCHAR(100),
    symbol VARCHAR(5),
    decimal_places INT
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    mfa_backup_codes TEXT[],
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE user_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    base_currency_code CHAR(3) NOT NULL,
    timezone VARCHAR(50),
    date_format VARCHAR(20),
    number_format VARCHAR(20),
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_profiles_currency FOREIGN KEY (base_currency_code) REFERENCES currencies(code),
    CONSTRAINT chk_display_name CHECK (display_name ~ '^[a-zA-ZÀ-ÖØ-öø-ÿ0-9 \-''.]+$')
);

CREATE TABLE institutions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    country_code CHAR(2) NOT NULL,
    logo_url VARCHAR(500),
    website VARCHAR(500),
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE trusted_devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    device_token_hash VARCHAR(64) NOT NULL,
    device_name VARCHAR(255) NOT NULL,
    last_used_at TIMESTAMPTZ DEFAULT now(),
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT fk_trusted_devices_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID,
    name VARCHAR(100) NOT NULL,
    parent_id UUID,
    color VARCHAR(7) NOT NULL,
    icon VARCHAR(50),
    created_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(7) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT fk_tags_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TYPE account_type AS ENUM (
    'CURRENT',
    'SAVINGS',
    'CREDIT_CARD',
    'LOAN'
);

CREATE TYPE connection_type AS ENUM (
    'PLAID',
    'MANUAL',
    'CSV_IMPORT'
);

CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    institution_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    type account_type NOT NULL,
    currency_code CHAR(3) NOT NULL,
    country_code CHAR(2) NOT NULL,
    current_balance DECIMAL(19,4) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    connection_type connection_type NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_accounts_institution FOREIGN KEY (institution_id) REFERENCES institutions(id),
    CONSTRAINT fk_accounts_currency FOREIGN KEY (currency_code) REFERENCES currencies(code)
);

CREATE TABLE credit_card_details (
    account_id UUID PRIMARY KEY,
    credit_limit DECIMAL(19,4) NOT NULL DEFAULT 0.00,
    apr DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    last_statement_balance DECIMAL(19,4) NOT NULL DEFAULT 0.00,
    last_statement_date DATE,
    next_payment_due_date DATE,
    next_payment_amount DECIMAL(19,4) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_credit_card_details_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE TABLE savings_details (
    account_id UUID PRIMARY KEY,
    interest_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_savings_details_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE TABLE current_account_details (
    account_id UUID PRIMARY KEY,
    overdraft_limit DECIMAL(19,4) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_current_account_details_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE TABLE loan_details (
    account_id UUID PRIMARY KEY,
    loan_amount DECIMAL(19,4) NOT NULL DEFAULT 0.00,
    interest_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    term_months INT NOT NULL DEFAULT 0,
    monthly_payment DECIMAL(19,4) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_loan_details_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE TYPE transaction_type AS ENUM (
    'CREDIT',
    'DEBIT'
);

CREATE TYPE transaction_source AS ENUM (
    'PLAID',
    'MANUAL',
    'CSV_IMPORT',
    'OPENING_BALANCE'
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    direction transaction_type NOT NULL,
    currency_code CHAR(3) NOT NULL,
    transaction_date DATE NOT NULL,
    original_amount DECIMAL(19,4) NOT NULL,
    original_currency_code CHAR(3) NOT NULL,
    is_recurring BOOLEAN DEFAULT FALSE,
    recurrence_pattern VARCHAR(50),
    notes TEXT,
    description TEXT,
    reference VARCHAR(500),
    posted_at DATE,
    category_id UUID,
    source transaction_source NOT NULL,
    plaid_transaction_id VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_currency FOREIGN KEY (currency_code) REFERENCES currencies(code),
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_transactions_original_currency FOREIGN KEY (original_currency_code) REFERENCES currencies(code)
);

CREATE TABLE transaction_tag (
    transaction_id UUID NOT NULL,
    tag_id UUID NOT NULL,
    CONSTRAINT transaction_tag_pkey PRIMARY KEY (transaction_id, tag_id),
    CONSTRAINT fk_transaction_tag_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_tag_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE

);

CREATE TABLE user_hidden_category (
    user_id UUID NOT NULL,
    category_id UUID NOT NULL,
    CONSTRAINT user_hidden_category_pkey PRIMARY KEY (user_id, category_id),
    CONSTRAINT fk_user_hidden_category_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_hidden_category_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE TABLE exchange_rates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_currency_code CHAR(3) NOT NULL,
    to_currency_code CHAR(3) NOT NULL,
    rate DECIMAL(19,8) NOT NULL,
    rate_date DATE NOT NULL,
    source VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT fk_exchange_rates_from_currency FOREIGN KEY (from_currency_code) REFERENCES currencies(code),
    CONSTRAINT fk_exchange_rates_to_currency FOREIGN KEY (to_currency_code) REFERENCES currencies(code),
    CONSTRAINT unique_exchange_rate UNIQUE (from_currency_code, to_currency_code, rate_date)

);

CREATE TYPE plaid_auth_status AS ENUM (
    'ACTIVE',
    'ERROR',
    'PENDING_REAUTH'
);

CREATE TABLE plaid_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    institution_id UUID NOT NULL,
    plaid_item_id VARCHAR(255) NOT NULL,
    access_token VARCHAR(500) NOT NULL,
    status plaid_auth_status,
    created_at TIMESTAMPTZ DEFAULT now(),
    last_synced_at TIMESTAMPTZ,
    CONSTRAINT fk_plaid_items_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_plaid_items_institution FOREIGN KEY (institution_id) REFERENCES institutions(id)
);

CREATE TABLE plaid_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plaid_item_id UUID NOT NULL,
    plaid_account_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    mask CHAR(4) NOT NULL,
    plaid_account_type VARCHAR(50),
    plaid_account_subtype VARCHAR(50),
    linked_account_id UUID,
    created_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT fk_plaid_accounts_item FOREIGN KEY (plaid_item_id) REFERENCES plaid_items(id) ON DELETE CASCADE,
    CONSTRAINT fk_plaid_accounts_linked_account FOREIGN KEY (linked_account_id) REFERENCES accounts(id) ON DELETE SET NULL
);

CREATE TYPE audit_action AS ENUM (
    'CREATE',
    'UPDATE',
    'DELETE'
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    action audit_action NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    old_value JSONB,
    new_value JSONB,
    ip_address VARCHAR(45),
    created_at TIMESTAMPTZ DEFAULT now()
);