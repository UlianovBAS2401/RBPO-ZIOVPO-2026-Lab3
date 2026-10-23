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
