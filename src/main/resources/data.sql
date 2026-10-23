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
