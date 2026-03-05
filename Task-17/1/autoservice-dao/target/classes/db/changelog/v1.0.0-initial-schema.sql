CREATE SCHEMA IF NOT EXISTS service;

CREATE TABLE IF NOT EXISTS service.car_service_masters
(
    id            UUID PRIMARY KEY default gen_random_uuid(),
    full_name     VARCHAR(100) NOT NULL CHECK (length(trim(full_name)) > 0),
    date_of_birth DATE CHECK (date_of_birth < current_date)
    );

CREATE TABLE IF NOT EXISTS service.workshop_places
(
    id   UUID PRIMARY KEY default gen_random_uuid(),
    name VARCHAR(100) NOT NULL CHECK (length(trim(name)) > 0)
    );

CREATE TABLE IF NOT EXISTS service.repair_orders
(
    id            UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    description   TEXT        NOT NULL,
    creation_date DATE        NOT NULL CHECK (creation_date <= current_date),
    start_date    DATE CHECK (start_date >= creation_date),
    end_date      DATE CHECK (end_date >= start_date),
    status        VARCHAR(50) NOT NULL DEFAULT 'CREATED',
    total_price   DECIMAL(10, 2) CHECK (total_price >= 0),
    master_id     UUID REFERENCES service.car_service_masters (id),
    place_id      UUID REFERENCES service.workshop_places (id)
    );