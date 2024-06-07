--liquibase formatted sql

--changeset kubazuch:add-product-cleanup-user
CREATE OR REPLACE PROCEDURE delete_product_user(prod_id bigint)
AS $$
BEGIN
    DELETE FROM users_products WHERE product_id = prod_id;
END
$$ LANGUAGE plpgsql;

--changeset kubazuch:add-product-cleanup-review
CREATE OR REPLACE PROCEDURE delete_product_review(prod_id bigint)
AS $$
BEGIN
    DELETE FROM reviews WHERE product_id = prod_id;
END
$$ LANGUAGE plpgsql;