INSERT INTO search_sessions (
    search_id,
    hotel_id,
    check_in,
    check_out,
    ages_sequence,
    visit_count,
    created_at,
    updated_at
)
SELECT
    'load-' || lpad(n::text, 7, '0'),
    'HOTEL01',
    DATE '2027-01-01',
    DATE '2027-01-06',
    '30,25',
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM generate_series(1, 1000000) AS n
ON CONFLICT (search_id) DO NOTHING;

SELECT count(*) AS seeded_sessions
FROM search_sessions
WHERE search_id LIKE 'load-%';
