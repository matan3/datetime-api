CREATE TABLE IF NOT EXISTS conversion_log (
    id SERIAL PRIMARY KEY,
    input_datetime VARCHAR(50) NOT NULL,
    from_timezone VARCHAR(50) NOT NULL,
    to_timezone VARCHAR(50) NOT NULL,
    output_datetime VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);