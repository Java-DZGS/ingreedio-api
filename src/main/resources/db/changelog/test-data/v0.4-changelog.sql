--liquidbase formatted sql

--changeset gitqueenzofia:add-edit_product-permission
INSERT INTO permissions (name, description)
VALUES ('EDIT_PRODUCT', 'Allows editing products');

--changeset gitqueenzofia:add-edit_product-permission-to-moderator
INSERT INTO roles_permissions
(role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE (r.name = 'MODERATOR' AND p.name IN ('EDIT_PRODUCT'));

--changeset gitqueenzofia:create-reviews-table
CREATE TABLE reviews
(
    id          BIGSERIAL PRIMARY KEY,
    user_id  BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    rating INT NOT NULL,
    content VARCHAR(512) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);