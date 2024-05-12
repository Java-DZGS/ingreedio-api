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