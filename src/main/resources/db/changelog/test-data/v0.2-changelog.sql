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

--changeset migoox:remove-level-column-from-roles_permissions
ALTER TABLE roles_permissions
DROP COLUMN level;

--changeset migoox:add-roles
INSERT INTO roles (id, name) VALUES (1, 'USER');
INSERT INTO roles (id, name) VALUES (2, 'MODERATOR');

--changeset migoox:add-permissions-v1
INSERT INTO permissions(id, name, description) VALUES (1, 'REMOVE_USER_OPINION', 'Allows removing user opinions');
INSERT INTO permissions(id, name, description) VALUES (2, 'REMOVE_PRODUCT', 'Allows removing products');
INSERT INTO permissions(id, name, description) VALUES (3, 'ADD_PRODUCT', 'Allows adding new products');
INSERT INTO permissions(id, name, description) VALUES (4, 'REPORT_USER_OPINION', 'Allows reporting user opinions');
INSERT INTO roles_permissions(role_id, permission_id) VALUES (2, 1);
INSERT INTO roles_permissions(role_id, permission_id) VALUES (2, 2);
INSERT INTO roles_permissions(role_id, permission_id) VALUES (2, 3);
INSERT INTO roles_permissions(role_id, permission_id) VALUES (1, 4);
