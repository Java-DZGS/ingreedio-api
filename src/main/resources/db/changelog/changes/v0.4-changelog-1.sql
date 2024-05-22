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

--changeset mslup:create-reports-table
CREATE TABLE reports
(
    id        BIGSERIAL PRIMARY KEY,
    user_id   BIGINT       NOT NULL,
    review_id BIGINT       NOT NULL,
    content   VARCHAR(512) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (review_id) REFERENCES reviews (id)
);