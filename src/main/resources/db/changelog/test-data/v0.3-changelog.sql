--liquidbase formatted sql

--changeset gitqueenzofia:create-users_ingredients-table
CREATE TABLE users_ingredients
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients (id)
);