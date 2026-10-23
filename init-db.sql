DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'utility_app_user') THEN
        CREATE USER utility_app_user WITH PASSWORD 'secure_pass123';
    END IF;
END
$$;

GRANT CONNECT ON DATABASE utility_accounting_db TO utility_app_user;
GRANT USAGE ON SCHEMA public TO utility_app_user;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_users_email_not_empty CHECK (length(trim(email)) > 0)
);

CREATE TABLE IF NOT EXISTS premises (
    id BIGSERIAL PRIMARY KEY,
    address VARCHAR(500) NOT NULL,
    area NUMERIC(10, 2) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_premises_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_premises_area_positive CHECK (area > 0)
);

CREATE TABLE IF NOT EXISTS meters (
    id BIGSERIAL PRIMARY KEY,
    premises_id BIGINT NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    serial_number VARCHAR(100) NOT NULL UNIQUE,
    unit_rate NUMERIC(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_meters_premises FOREIGN KEY (premises_id) REFERENCES premises(id) ON DELETE RESTRICT,
    CONSTRAINT chk_meters_unit_rate_positive CHECK (unit_rate > 0),
    CONSTRAINT chk_meters_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'FAULTY'))
);

CREATE TABLE IF NOT EXISTS meter_readings (
    id BIGSERIAL PRIMARY KEY,
    meter_id BIGINT NOT NULL,
    period VARCHAR(50) NOT NULL,
    reading_value NUMERIC(12, 3) NOT NULL,
    verification_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    recorded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_readings_meter FOREIGN KEY (meter_id) REFERENCES meters(id) ON DELETE CASCADE,
    CONSTRAINT uq_meter_period UNIQUE (meter_id, period),
    CONSTRAINT chk_reading_value_non_negative CHECK (reading_value >= 0),
    CONSTRAINT chk_verification_status CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED'))
);

CREATE TABLE IF NOT EXISTS utility_charges (
    id BIGSERIAL PRIMARY KEY,
    premises_id BIGINT NOT NULL,
    meter_id BIGINT,
    period VARCHAR(50) NOT NULL,
    volume NUMERIC(12, 3) NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'CALCULATED',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_charges_premises FOREIGN KEY (premises_id) REFERENCES premises(id) ON DELETE RESTRICT,
    CONSTRAINT fk_charges_meter FOREIGN KEY (meter_id) REFERENCES meters(id) ON DELETE SET NULL,
    CONSTRAINT chk_charges_volume_non_negative CHECK (volume >= 0),
    CONSTRAINT chk_charges_amount_non_negative CHECK (amount >= 0),
    CONSTRAINT chk_charges_status CHECK (status IN ('CALCULATED', 'PAID', 'CANCELLED'))
);

INSERT INTO users (id, full_name, email, phone, created_at)
VALUES
    (1, 'Иванов Иван Иванович', 'ivanov@example.com', '+79991112233', CURRENT_TIMESTAMP),
    (2, 'Петров Петр Сергеевич', 'petrov@example.com', '+79992223344', CURRENT_TIMESTAMP),
    (3, 'Сидорова Анна Павловна', 'sidorova@example.com', '+79993334455', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

INSERT INTO premises (id, address, area, user_id, created_at)
VALUES
    (1, 'г. Москва, ул. Ленина, д. 10, кв. 15', 54.50, 1, CURRENT_TIMESTAMP),
    (2, 'г. Москва, ул. Ленина, д. 10, кв. 16', 72.00, 2, CURRENT_TIMESTAMP),
    (3, 'г. Москва, ул. Гагарина, д. 5', 120.00, 3, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

INSERT INTO meters (id, premises_id, resource_type, serial_number, unit_rate, status, created_at)
VALUES
    (1, 1, 'COLD_WATER', 'MTR-CW-1001', 50.90, 'ACTIVE', CURRENT_TIMESTAMP),
    (2, 1, 'ELECTRICITY', 'MTR-EL-2001', 6.43, 'ACTIVE', CURRENT_TIMESTAMP),
    (3, 2, 'COLD_WATER', 'MTR-CW-1002', 50.90, 'ACTIVE', CURRENT_TIMESTAMP),
    (4, 3, 'GAS', 'MTR-GAS-3001', 7.80, 'ACTIVE', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

INSERT INTO meter_readings (id, meter_id, period, reading_value, verification_status, recorded_at)
VALUES
    (1, 1, '2026-08', 120.000, 'VERIFIED', CURRENT_TIMESTAMP - INTERVAL '2 month'),
    (2, 1, '2026-09', 128.500, 'VERIFIED', CURRENT_TIMESTAMP - INTERVAL '1 month'),
    (3, 2, '2026-09', 340.000, 'VERIFIED', CURRENT_TIMESTAMP - INTERVAL '1 month'),
    (4, 3, '2026-09', 85.000, 'VERIFIED', CURRENT_TIMESTAMP - INTERVAL '1 month')
ON CONFLICT (id) DO NOTHING;

INSERT INTO utility_charges (id, premises_id, meter_id, period, volume, amount, status, created_at)
VALUES
    (1, 1, 1, '2026-09', 8.500, 432.65, 'PAID', CURRENT_TIMESTAMP - INTERVAL '25 day'),
    (2, 1, 2, '2026-09', 55.000, 353.65, 'CALCULATED', CURRENT_TIMESTAMP - INTERVAL '20 day')
ON CONFLICT (id) DO NOTHING;

SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval('premises_id_seq', (SELECT COALESCE(MAX(id), 1) FROM premises));
SELECT setval('meters_id_seq', (SELECT COALESCE(MAX(id), 1) FROM meters));
SELECT setval('meter_readings_id_seq', (SELECT COALESCE(MAX(id), 1) FROM meter_readings));
SELECT setval('utility_charges_id_seq', (SELECT COALESCE(MAX(id), 1) FROM utility_charges));

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO utility_app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO utility_app_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO utility_app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO utility_app_user;
