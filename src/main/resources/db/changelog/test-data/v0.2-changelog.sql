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
CREATE TABLE auth_infos_roles
(
    id      BIGSERIAL PRIMARY KEY,
    auth_info_id BIGINT NOT NULL REFERENCES auth_info(id),
    role_id BIGINT NOT NULL REFERENCES roles(id)
);

--changeset migoox:remove-level-column-from-roles_permissions
ALTER TABLE roles_permissions
DROP COLUMN level;

--changeset migoox:add-roles
INSERT INTO roles (name) VALUES ('USER'), ('MODERATOR');

--changeset migoox:add-permissions-v1
INSERT INTO permissions (name, description)
VALUES ('REMOVE_USER_OPINION', 'Allows removing user opinions'),
       ('REMOVE_PRODUCT', 'Allows removing products'),
       ('ADD_PRODUCT', 'Allows adding new products'),
       ('REPORT_USER_OPINION', 'Allows reporting user opinions');

INSERT INTO roles_permissions
    (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
    CROSS JOIN permissions p
WHERE (r.name = 'MODERATOR' AND p.name IN ('REMOVE_USER_OPINION', 'REMOVE_PRODUCT', 'ADD_PRODUCT'))
   OR (r.name = 'USER' AND p.name = 'REPORT_USER_OPINION');