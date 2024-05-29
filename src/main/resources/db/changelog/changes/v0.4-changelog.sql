--liquibase formatted sql

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

--changeset gitqueenzofia:add-date-column-to-reviews-table
ALTER TABLE reviews ADD created_at TIMESTAMP NOT NULL;

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

--changeset mslup:add-report-review-permission
INSERT INTO permissions (name, description)
VALUES ('REPORT_REVIEW', 'Allows reporting reviews');

--changeset mslup:add-report-review-permission-to-user
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE (r.name = 'USER' AND p.name = 'REPORT_REVIEW');

--changeset mslup:add-report-management-permissions
INSERT INTO permissions (name, description)
VALUES ('GET_REPORTS', 'Allows getting reports'),
       ('DELETE_REPORT', 'Allows deleting a report');

--changeset mslup:add-report-management-permissions-to-moderator
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE (r.name = 'MODERATOR' AND p.name IN ('GET_REPORTS', 'DELETE_REPORT'));

--changeset mslup:add-like-review-permissions
INSERT INTO permissions (name, description)
VALUES ('LIKE_REVIEW', 'Allows liking reviews');

-- changeset mslup:add-like-review-permissions-to-user
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE (r.name = 'USER' AND p.name = 'LIKE_REVIEW');

--changeset mslup:create-users-liked-reviews-table
CREATE TABLE users_liked_reviews
(
    id        BIGSERIAL PRIMARY KEY,
    user_id   BIGINT NOT NULL,
    review_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (review_id) REFERENCES reviews (id)
);

--changeset mslup:create-users-disliked-reviews-table
CREATE TABLE users_disliked_reviews
(
    id        BIGSERIAL PRIMARY KEY,
    user_id   BIGINT NOT NULL,
    review_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (review_id) REFERENCES reviews (id)
);

--changeset mslup:add-like-and-dislike-column-to-reviews-table
ALTER TABLE reviews
    ADD COLUMN likes_count INT NOT NULL DEFAULT 0,
    ADD COLUMN dislikes_count INT NOT NULL DEFAULT 0;

--changeset mslup:add-like-review-permissions-to-moderator
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE (r.name = 'MODERATOR' AND p.name = 'LIKE_REVIEW');

--changeset kubazuch:add-string-matches-query-function
CREATE OR REPLACE FUNCTION string_matches_query(string text, query text[]) RETURNS bigint
AS $$
BEGIN
    RETURN (
        SELECT count(*) FROM (SELECT unnest(query) AS token) AS tokens
        WHERE string like token || '%' OR string like '% ' || token || '%'
    );
END
$$ LANGUAGE plpgsql;

--changeset kubazuch:add-get-user-info-permission
INSERT INTO permissions (name, description)
VALUES ('GET_USER_INFO', 'Allows getting info of any user.');
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r CROSS JOIN permissions p
WHERE (r.name = 'MODERATOR' AND p.name = 'GET_USER_INFO');
