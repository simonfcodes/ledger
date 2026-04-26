-- UK banks
INSERT INTO institutions (id, name, country_code) VALUES
    (gen_random_uuid(), 'HSBC UK', 'GB'),
    (gen_random_uuid(), 'Lloyds Bank', 'GB'),
    (gen_random_uuid(), 'Barclays', 'GB'),
    (gen_random_uuid(), 'NatWest', 'GB'),
    (gen_random_uuid(), 'Santander UK', 'GB'),
    (gen_random_uuid(), 'Monzo', 'GB'),
    (gen_random_uuid(), 'Starling Bank', 'GB'),
    (gen_random_uuid(), 'Revolut', 'GB'),
    (gen_random_uuid(), 'Nationwide', 'GB'),
    (gen_random_uuid(), 'Virgin Money', 'GB'),
    (gen_random_uuid(), 'Vanquis', 'GB'),
    (gen_random_uuid(), 'TSB Bank', 'GB'),
    (gen_random_uuid(), 'Metro Bank', 'GB'),
    (gen_random_uuid(), 'Halifax', 'GB');

-- US banks
INSERT INTO institutions (id, name, country_code) VALUES
    (gen_random_uuid(), 'Bank of America', 'US'),
    (gen_random_uuid(), 'Chase', 'US'),
    (gen_random_uuid(), 'Wells Fargo', 'US'),
    (gen_random_uuid(), 'Citibank', 'US'),
    (gen_random_uuid(), 'Capital One', 'US'),
    (gen_random_uuid(), 'PNC Bank', 'US'),
    (gen_random_uuid(), 'TD Bank', 'US'),
    (gen_random_uuid(), 'BB&T', 'US'),
    (gen_random_uuid(), 'SunTrust', 'US'),
    (gen_random_uuid(), 'U.S. Bank', 'US'),
    (gen_random_uuid(), 'HSBC US', 'US');

-- Generic
INSERT INTO institutions (id, name, country_code) VALUES
    (gen_random_uuid(), 'Cash', 'XX'),
    (gen_random_uuid(), 'Other', 'XX');