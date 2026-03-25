CREATE TABLE IF NOT EXISTS service.roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(55) NOT NULL UNIQUE CHECK (length(trim(name)) > 0)
);

CREATE TABLE IF NOT EXISTS service.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(55) NOT NULL UNIQUE CHECK (length(trim(username)) > 0),
    password VARCHAR(255) NOT NULL CHECK (length(password) > 0),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS service.user_roles (
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES service.users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES service.roles(id) ON DELETE CASCADE
);

COMMENT ON TABLE service.users IS 'Пользователи системы';
COMMENT ON TABLE service.roles IS 'Роли пользователей';
COMMENT ON TABLE service.user_roles IS 'Связь пользователей и ролей';

INSERT INTO service.roles (name) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_MANAGER'),
    ('ROLE_MASTER')
ON CONFLICT (name) DO NOTHING;

ALTER TABLE service.car_service_masters
    ADD COLUMN user_id INTEGER UNIQUE,
    ADD FOREIGN KEY (user_id) REFERENCES service.users(id) ON DELETE SET NULL;