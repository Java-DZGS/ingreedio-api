--liquibase formatted sql

--changeset mslup:create-providers-table
CREATE TABLE providers
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

--changeset mslup:create-brands-table
CREATE TABLE brands
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);