--liquibase formatted sql

--changeset gitqueenzofia:create-users-table
CREATE TABLE users
(
    id           BIGSERIAL PRIMARY KEY,
    email        VARCHAR(128) NOT NULL UNIQUE,
    user_name    VARCHAR(128) NOT NULL UNIQUE,
    display_name VARCHAR(128) NOT NULL,
    password     VARCHAR(128) NOT NULL
);

--changeset gitqueenzofia:create-products-table
CREATE TABLE products
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(128) NOT NULL,
    small_image_url   VARCHAR(256),
    large_image_url   VARCHAR(256),
    provider          VARCHAR(128) NOT NULL,
    brand             VARCHAR(128) NOT NULL,
    short_description VARCHAR(256),
    long_description  VARCHAR(512),
    volume            INT          NOT NULL
);

--changeset gitqueenzofia:create-ingredients-table
CREATE TABLE ingredients
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL
);

--changeset gitqueenzofia:create-users_products-table
CREATE TABLE users_products
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (product_id) REFERENCES products (id)
);

--changeset gitqueenzofia:create-users_allergens-table
CREATE TABLE users_allergens
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients (id)
);

--changeset gitqueenzofia:create-categories-table
CREATE TABLE categories
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE
);

--changeset gitqueenzofia:create-products_categories-table
CREATE TABLE products_categories
(
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products (id),
    FOREIGN KEY (category_id) REFERENCES categories (id)
);

--changeset gitqueenzofia:add-unique-constraint-ingredients
ALTER TABLE ingredients
ADD CONSTRAINT ingredients_unique_name UNIQUE (name);

--changeset kubazuch:add-auth-info
CREATE TABLE auth_info
(
    id       BIGSERIAL PRIMARY KEY,
    user_id  BIGSERIAL NOT NULL UNIQUE,
    username VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL
);

--changeset kubazuch:user-remove-credentials
ALTER TABLE users DROP COLUMN user_name;
ALTER TABLE users DROP COLUMN password;

--changeset kubazuch:refresh-token
CREATE TABLE refresh_token
(
    id              BIGSERIAL PRIMARY KEY,
    token           VARCHAR(128) NOT NULL,
    expiration_date TIMESTAMP WITH TIME ZONE NOT NULL,
    auth_id         BIGSERIAL NOT NULL
);