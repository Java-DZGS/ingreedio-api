--liquibase formatted sql

--changeset gitqueenzofia:remove-foreign-key-product_id
ALTER TABLE users_products DROP CONSTRAINT users_products_product_id_fkey;

--changeset gitqueenzofia:drop-products_categories-table
DROP TABLE products_categories;

--changeset gitqueenzofia:drop-products-table
DROP TABLE products;


--changeset migoox:create-roles-table
CREATE TABLE roles
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

--changeset migoox:create-permissions-table
CREATE TABLE permissions
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(64) NOT NULL UNIQUE,
    description VARCHAR(256)
);

--changeset migoox:create-role_permissions-table
CREATE TABLE roles_permissions
(
    id            BIGSERIAL PRIMARY KEY,
    role_id       BIGINT NOT NULL REFERENCES roles(id),
    permission_id BIGINT NOT NULL REFERENCES permissions(id),
    level         VARCHAR(128) NOT NULl
);

--changeset migoox:create-user_roles-table
CREATE TABLE users_roles
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id)
);
