--liquibase formatted sql

--changeset gitqueenzofia:remove-foreign-key-product_id
ALTER TABLE users_products DROP CONSTRAINT users_products_product_id_fkey;

--changeset gitqueenzofia:drop-products_categories-table
DROP TABLE products_categories

--changeset gitqueenzofia:drop-products-table
DROP TABLE products

