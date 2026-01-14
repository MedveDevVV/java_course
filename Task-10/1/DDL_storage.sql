CREATE DATABASE test_db;

\c test_db

CREATE SCHEMA storage;

CREATE TABLE storage.product
(
    maker VARCHAR(10) NOT NULL,
    model VARCHAR(10) PRIMARY KEY,
    type  VARCHAR(50) NOT NULL
);

CREATE TABLE storage.pc
(
    code  INT PRIMARY KEY,
    model VARCHAR(50) REFERENCES storage.product (model),
    speed SMALLINT    NOT NULL,
    ram   SMALLINT    NOT NULL,
    hd    REAL        NOT NULL,
    cd    VARCHAR(10) NOT NULL,
    price money
);

CREATE TABLE storage.laptop
(
    code   INT PRIMARY KEY,
    model  VARCHAR(50) REFERENCES storage.product (model),
    speed  SMALLINT NOT NULL,
    ram    SMALLINT NOT NULL,
    hd     REAL     NOT NULL,
    screen SMALLINT NOT NULL,
    price  money
);

CREATE TABLE storage.printer
(
    code  INT PRIMARY KEY,
    model VARCHAR(50) REFERENCES storage.product (model),
    color char(1)     NOT NULL,
    type  VARCHAR(10) NOT NULL,
    price money
);
